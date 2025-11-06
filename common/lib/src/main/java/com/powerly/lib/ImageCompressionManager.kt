package com.SharaSpot.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Manages image compression for contributions
 * Compresses images to 1024x1024 max dimension with 80% quality
 */
class ImageCompressionManager(private val context: Context) {

    companion object {
        private const val MAX_DIMENSION = 1024
        private const val COMPRESSION_QUALITY = 80
    }

    /**
     * Compresses an image from URI
     *
     * @param imageUri The source image URI
     * @param outputFile Optional output file, creates temp file if not provided
     * @return The compressed image file
     */
    suspend fun compressImage(
        imageUri: Uri,
        outputFile: File? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Read the image
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(IOException("Cannot open image"))

            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                return@withContext Result.failure(IOException("Cannot decode image"))
            }

            // Get orientation from EXIF
            val orientation = getImageOrientation(imageUri)

            // Rotate bitmap if needed
            val rotatedBitmap = rotateBitmap(originalBitmap, orientation)

            // Calculate new dimensions
            val (newWidth, newHeight) = calculateDimensions(
                rotatedBitmap.width,
                rotatedBitmap.height
            )

            // Resize bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(
                rotatedBitmap,
                newWidth,
                newHeight,
                true
            )

            // Clean up original if different from resized
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            if (originalBitmap != resizedBitmap) {
                originalBitmap.recycle()
            }

            // Create output file
            val compressedFile = outputFile ?: createTempImageFile()

            // Compress and save
            FileOutputStream(compressedFile).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
            }

            // Clean up
            resizedBitmap.recycle()

            Result.success(compressedFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Compresses multiple images
     *
     * @param imageUris List of image URIs to compress
     * @return List of compressed image files
     */
    suspend fun compressImages(imageUris: List<Uri>): Result<List<File>> = withContext(Dispatchers.IO) {
        try {
            val compressedFiles = imageUris.map { uri ->
                val result = compressImage(uri)
                result.getOrThrow()
            }
            Result.success(compressedFiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gets the orientation of an image from EXIF data
     */
    private fun getImageOrientation(imageUri: Uri): Int {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return ExifInterface.ORIENTATION_NORMAL

            val exif = ExifInterface(inputStream)
            inputStream.close()

            return exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            return ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Rotates a bitmap according to EXIF orientation
     */
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Calculates new dimensions maintaining aspect ratio
     */
    private fun calculateDimensions(width: Int, height: Int): Pair<Int, Int> {
        if (width <= MAX_DIMENSION && height <= MAX_DIMENSION) {
            return Pair(width, height)
        }

        val ratio = width.toFloat() / height.toFloat()

        return if (width > height) {
            val newWidth = MAX_DIMENSION
            val newHeight = (MAX_DIMENSION / ratio).toInt()
            Pair(newWidth, newHeight)
        } else {
            val newHeight = MAX_DIMENSION
            val newWidth = (MAX_DIMENSION * ratio).toInt()
            Pair(newWidth, newHeight)
        }
    }

    /**
     * Creates a temporary file for compressed image
     */
    private fun createTempImageFile(): File {
        val timestamp = System.currentTimeMillis()
        val fileName = "compressed_$timestamp.jpg"
        return File(context.cacheDir, fileName)
    }

    /**
     * Gets the file size in KB
     */
    fun getFileSizeKB(file: File): Long {
        return file.length() / 1024
    }

    /**
     * Gets the file size in MB
     */
    fun getFileSizeMB(file: File): Double {
        return file.length() / (1024.0 * 1024.0)
    }

    /**
     * Deletes temporary compressed files
     */
    fun cleanupTempFiles() {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("compressed_")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}
