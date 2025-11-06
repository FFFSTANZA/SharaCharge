package com.SharaSpot.core.data.repoImpl

import android.net.Uri
import com.SharaSpot.core.data.repositories.ContributionRepository
import com.SharaSpot.core.model.api.ApiStatus
import com.SharaSpot.core.model.contribution.*
import com.SharaSpot.core.network.asErrorMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Implementation of ContributionRepository using Firebase Firestore and Storage
 *
 * Note: This is a mock implementation that stores data locally.
 * In production, integrate with Firebase Firestore and Firebase Storage.
 */
@Single
class ContributionRepositoryImpl(
    @Named("IO") private val ioDispatcher: CoroutineDispatcher,
    // Uncomment when Firebase is available:
    // private val firestore: FirebaseFirestore,
    // private val storage: FirebaseStorage,
    // private val auth: FirebaseAuth
) : ContributionRepository {

    // In-memory storage for mock implementation
    private val contributions = mutableListOf<Contribution>()

    init {
        // Add sample data for testing
        addSampleContributions()
    }

    private fun addSampleContributions() {
        val now = System.currentTimeMillis()

        // Sample photos
        contributions.add(
            Contribution(
                id = "photo_1",
                chargerId = "1", // Match the default charger ID in PowerSourceContent
                userId = "user_1",
                userName = "Priya Kumar",
                type = ContributionType.PHOTO,
                timestamp = now - 3600_000, // 1 hour ago
                evCoinsEarned = 20,
                photoUrl = "https://via.placeholder.com/400x300/4CAF50/FFFFFF?text=Charger+View",
                photoCategory = "charger"
            )
        )

        contributions.add(
            Contribution(
                id = "photo_2",
                chargerId = "1",
                userId = "user_2",
                userName = "Raj Patel",
                type = ContributionType.PHOTO,
                timestamp = now - 7200_000, // 2 hours ago
                evCoinsEarned = 20,
                photoUrl = "https://via.placeholder.com/400x300/2196F3/FFFFFF?text=Parking+Area",
                photoCategory = "parking"
            )
        )

        contributions.add(
            Contribution(
                id = "photo_3",
                chargerId = "1",
                userId = "user_3",
                userName = "Anjali Sharma",
                type = ContributionType.PHOTO,
                timestamp = now - 86400_000, // 1 day ago
                evCoinsEarned = 20,
                photoUrl = "https://via.placeholder.com/400x300/FF9800/FFFFFF?text=Plug+Close-up",
                photoCategory = "plugs"
            )
        )

        // Sample reviews
        contributions.add(
            Contribution(
                id = "review_1",
                chargerId = "1",
                userId = "user_4",
                userName = "Suresh Reddy",
                type = ContributionType.REVIEW,
                timestamp = now - 3600_000, // 1 hour ago
                evCoinsEarned = 30,
                rating = 4.5f,
                comment = "Great charging station! Fast charging and well-maintained. The parking area is clean and spacious."
            )
        )

        contributions.add(
            Contribution(
                id = "review_2",
                chargerId = "1",
                userId = "user_5",
                userName = "Meera Singh",
                type = ContributionType.REVIEW,
                timestamp = now - 86400_000, // 1 day ago
                evCoinsEarned = 30,
                rating = 5.0f,
                comment = "Perfect location! Easy to find and the charger worked perfectly with my Tata Nexon EV.",
                validationCount = 5
            )
        )

        contributions.add(
            Contribution(
                id = "review_3",
                chargerId = "1",
                userId = "user_6",
                userName = "Vikram Joshi",
                type = ContributionType.REVIEW,
                timestamp = now - 172800_000, // 2 days ago
                evCoinsEarned = 30,
                rating = 4.0f,
                comment = "Good charging speed. Could use better shade in parking area.",
                validationCount = 3
            )
        )

        // Sample wait time
        contributions.add(
            Contribution(
                id = "wait_1",
                chargerId = "1",
                userId = "user_1",
                userName = "Priya Kumar",
                type = ContributionType.WAIT_TIME,
                timestamp = now - 900_000, // 15 min ago
                evCoinsEarned = 10,
                waitTimeMinutes = 5,
                queueLength = 1
            )
        )

        // Sample plug checks
        contributions.add(
            Contribution(
                id = "plug_1",
                chargerId = "1",
                userId = "user_2",
                userName = "Raj Patel",
                type = ContributionType.PLUG_CHECK,
                timestamp = now - 3600_000, // 1 hour ago
                evCoinsEarned = 25,
                plugType = "CCS",
                plugWorking = true,
                powerOutput = "50kW",
                vehicleTested = "Tata Nexon EV"
            )
        )

        contributions.add(
            Contribution(
                id = "plug_2",
                chargerId = "1",
                userId = "user_3",
                userName = "Anjali Sharma",
                type = ContributionType.PLUG_CHECK,
                timestamp = now - 259200_000, // 3 days ago
                evCoinsEarned = 25,
                plugType = "CHAdeMO",
                plugWorking = false,
                powerOutput = null,
                vehicleTested = null
            )
        )

        contributions.add(
            Contribution(
                id = "plug_3",
                chargerId = "1",
                userId = "user_7",
                userName = "Karthik Nair",
                type = ContributionType.PLUG_CHECK,
                timestamp = now - 7200_000, // 2 hours ago
                evCoinsEarned = 25,
                plugType = "Type 2",
                plugWorking = true,
                powerOutput = "7kW",
                vehicleTested = "MG ZS EV"
            )
        )

        // Sample status update
        contributions.add(
            Contribution(
                id = "status_1",
                chargerId = "1",
                userId = "user_1",
                userName = "Priya Kumar",
                type = ContributionType.STATUS_UPDATE,
                timestamp = now - 600_000, // 10 min ago
                evCoinsEarned = 15,
                chargerStatus = ChargerStatus.AVAILABLE
            )
        )
    }

    override suspend fun createContribution(request: CreateContributionRequest): ApiStatus<Contribution> =
        withContext(ioDispatcher) {
            try {
                // TODO: Replace with actual Firebase user data
                val userId = "mock_user_id"
                val userName = "Mock User"

                val contribution = Contribution(
                    id = UUID.randomUUID().toString(),
                    chargerId = request.chargerId,
                    userId = userId,
                    userName = userName,
                    type = request.type,
                    timestamp = System.currentTimeMillis(),
                    evCoinsEarned = request.type.evCoinsReward,
                    photoUrl = request.photoUrl,
                    photoCategory = request.photoCategory,
                    rating = request.rating,
                    comment = request.comment,
                    waitTimeMinutes = request.waitTimeMinutes,
                    queueLength = request.queueLength,
                    plugType = request.plugType,
                    plugWorking = request.plugWorking,
                    powerOutput = request.powerOutput,
                    vehicleTested = request.vehicleTested,
                    chargerStatus = request.chargerStatus,
                    validationCount = 0
                )

                // Mock implementation - add to in-memory list
                contributions.add(contribution)

                /* Production Firebase implementation:
                val contributionMap = hashMapOf(
                    "id" to contribution.id,
                    "chargerId" to contribution.chargerId,
                    "userId" to contribution.userId,
                    "userName" to contribution.userName,
                    "type" to contribution.type.name,
                    "timestamp" to contribution.timestamp,
                    "evCoinsEarned" to contribution.evCoinsEarned,
                    "photoUrl" to contribution.photoUrl,
                    "photoCategory" to contribution.photoCategory,
                    "rating" to contribution.rating,
                    "comment" to contribution.comment,
                    "waitTimeMinutes" to contribution.waitTimeMinutes,
                    "queueLength" to contribution.queueLength,
                    "plugType" to contribution.plugType,
                    "plugWorking" to contribution.plugWorking,
                    "powerOutput" to contribution.powerOutput,
                    "vehicleTested" to contribution.vehicleTested,
                    "chargerStatus" to contribution.chargerStatus?.name,
                    "validationCount" to contribution.validationCount
                )

                firestore.collection("contributions")
                    .document(contribution.id)
                    .set(contributionMap)
                    .await()
                */

                ApiStatus.Success(contribution)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getContributions(chargerId: String): ApiStatus<List<Contribution>> =
        withContext(ioDispatcher) {
            try {
                // Mock implementation
                val chargerContributions = contributions.filter { it.chargerId == chargerId }
                    .sortedByDescending { it.timestamp }

                /* Production Firebase implementation:
                val snapshot = firestore.collection("contributions")
                    .whereEqualTo("chargerId", chargerId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val chargerContributions = snapshot.documents.mapNotNull { doc ->
                    doc.toContribution()
                }
                */

                ApiStatus.Success(chargerContributions)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getUserContributions(userId: String): ApiStatus<List<Contribution>> =
        withContext(ioDispatcher) {
            try {
                // Mock implementation
                val userContributions = contributions.filter { it.userId == userId }
                    .sortedByDescending { it.timestamp }

                /* Production Firebase implementation:
                val snapshot = firestore.collection("contributions")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val userContributions = snapshot.documents.mapNotNull { doc ->
                    doc.toContribution()
                }
                */

                ApiStatus.Success(userContributions)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun getContributionSummary(chargerId: String): ApiStatus<ContributionSummary> =
        withContext(ioDispatcher) {
            try {
                val chargerContributions = contributions.filter { it.chargerId == chargerId }

                val totalPhotos = chargerContributions.count { it.type == ContributionType.PHOTO }
                val ratings = chargerContributions.mapNotNull { it.rating }
                val averageRating = if (ratings.isNotEmpty()) ratings.average().toFloat() else null

                // Latest wait time (not older than 2 hours)
                val latestWaitTimeContribution = chargerContributions
                    .filter { it.type == ContributionType.WAIT_TIME && it.waitTimeMinutes != null }
                    .maxByOrNull { it.timestamp }

                val latestWaitTime = latestWaitTimeContribution?.let {
                    val isStale = System.currentTimeMillis() - it.timestamp > WaitTimeInfo.STALE_THRESHOLD_MILLIS
                    WaitTimeInfo(
                        minutes = it.waitTimeMinutes!!,
                        queueLength = it.queueLength,
                        timestamp = it.timestamp,
                        isStale = isStale
                    )
                }

                // Current status (most recent)
                val currentStatus = chargerContributions
                    .filter { it.type == ContributionType.STATUS_UPDATE && it.chargerStatus != null }
                    .maxByOrNull { it.timestamp }
                    ?.chargerStatus

                // Plug status aggregation
                val plugStatusMap = mutableMapOf<String, MutableList<Contribution>>()
                chargerContributions
                    .filter { it.type == ContributionType.PLUG_CHECK && it.plugType != null }
                    .forEach { contrib ->
                        plugStatusMap.getOrPut(contrib.plugType!!) { mutableListOf() }.add(contrib)
                    }

                val plugStatusList = plugStatusMap.map { (plugType, contribs) ->
                    val latestPlugContrib = contribs.maxByOrNull { it.timestamp }!!
                    PlugStatusInfo(
                        plugType = plugType,
                        isWorking = latestPlugContrib.plugWorking ?: false,
                        powerOutput = latestPlugContrib.powerOutput,
                        lastVerified = latestPlugContrib.timestamp,
                        verificationCount = contribs.size
                    )
                }

                val recentContributions = chargerContributions
                    .sortedByDescending { it.timestamp }
                    .take(10)

                val summary = ContributionSummary(
                    chargerId = chargerId,
                    totalContributions = chargerContributions.size,
                    totalPhotos = totalPhotos,
                    averageRating = averageRating,
                    latestWaitTime = latestWaitTime,
                    currentStatus = currentStatus,
                    plugStatusList = plugStatusList,
                    recentContributions = recentContributions
                )

                ApiStatus.Success(summary)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun uploadPhoto(imageUri: Uri, chargerId: String): ApiStatus<String> =
        withContext(ioDispatcher) {
            try {
                // Mock implementation - return placeholder URL
                val photoId = UUID.randomUUID().toString()
                val mockUrl = "https://firebasestorage.googleapis.com/chargers/$chargerId/photos/$photoId.jpg"

                /* Production Firebase implementation:
                val photoId = UUID.randomUUID().toString()
                val storageRef = storage.reference
                    .child("chargers/$chargerId/photos/$photoId.jpg")

                val uploadTask = storageRef.putFile(imageUri).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()

                return@withContext ApiStatus.Success(downloadUrl.toString())
                */

                ApiStatus.Success(mockUrl)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun validateContribution(contributionId: String): ApiStatus<Boolean> =
        withContext(ioDispatcher) {
            try {
                // Mock implementation
                val contribution = contributions.find { it.id == contributionId }
                if (contribution != null) {
                    val index = contributions.indexOf(contribution)
                    contributions[index] = contribution.copy(validationCount = contribution.validationCount + 1)
                }

                /* Production Firebase implementation:
                firestore.collection("contributions")
                    .document(contributionId)
                    .update("validationCount", FieldValue.increment(1))
                    .await()
                */

                ApiStatus.Success(true)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    override suspend fun awardEVCoins(userId: String, amount: Int): ApiStatus<Boolean> =
        withContext(ioDispatcher) {
            try {
                // Mock implementation - always succeed
                // In production, this should update the user's balance in the backend

                /* Production implementation:
                // Option 1: Update via backend API
                val response = remoteDataSource.awardEVCoins(userId, amount)
                if (response.isSuccess) ApiStatus.Success(true)
                else ApiStatus.Error(response.getMessage())

                // Option 2: Update Firebase directly
                firestore.collection("users")
                    .document(userId)
                    .update("evCoins", FieldValue.increment(amount.toLong()))
                    .await()
                */

                ApiStatus.Success(true)
            } catch (e: Exception) {
                ApiStatus.Error(e.asErrorMessage)
            }
        }

    /* Helper extension function for Firebase (uncomment when Firebase is available)
    private fun DocumentSnapshot.toContribution(): Contribution? {
        return try {
            Contribution(
                id = getString("id") ?: return null,
                chargerId = getString("chargerId") ?: return null,
                userId = getString("userId") ?: return null,
                userName = getString("userName") ?: return null,
                type = ContributionType.valueOf(getString("type") ?: return null),
                timestamp = getLong("timestamp") ?: return null,
                evCoinsEarned = getLong("evCoinsEarned")?.toInt() ?: 0,
                photoUrl = getString("photoUrl"),
                photoCategory = getString("photoCategory"),
                rating = getDouble("rating")?.toFloat(),
                comment = getString("comment"),
                waitTimeMinutes = getLong("waitTimeMinutes")?.toInt(),
                queueLength = getLong("queueLength")?.toInt(),
                plugType = getString("plugType"),
                plugWorking = getBoolean("plugWorking"),
                powerOutput = getString("powerOutput"),
                vehicleTested = getString("vehicleTested"),
                chargerStatus = getString("chargerStatus")?.let { ChargerStatus.valueOf(it) },
                validationCount = getLong("validationCount")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }
    */
}
