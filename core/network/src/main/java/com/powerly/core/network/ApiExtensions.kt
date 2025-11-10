package com.SharaSpot.core.network

import android.util.Log
import com.powerly.core.model.api.ValidationErrorResponse
import com.powerly.core.model.util.Message
import com.google.gson.Gson
import okhttp3.Headers
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response


private const val TAG = "ApiExtensions"

val Exception.asErrorMessage: Message get() = Message(this.message.orEmpty(), Message.ERROR)
val Throwable.asErrorMessage: Message get() = Message(this.message.orEmpty(), Message.ERROR)

val HttpException.asErrorMessage: Message
    get() {
        var msg = this.message.orEmpty()
        this.response()?.errorBody()?.let { errorBody ->
            try {
                val jObjError = JSONObject(errorBody.string())
                if (jObjError.has("message")) msg = jObjError.get("message").toString()
                else if (jObjError.has("msg")) msg = jObjError.get("msg").toString()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response: ${e.message}", e)
            }
        }
        return Message(msg, Message.ERROR, code = code())
    }

val Response<*>.asErrorMessage: Message
    get() {
        val msg = try {
            val errorBodyString = this.errorBody()?.string()
            if (errorBodyString != null) {
                val jObjError = JSONObject(errorBodyString)
                if (jObjError.has("message")) jObjError.get("message").toString()
                else if (jObjError.has("msg")) jObjError.get("msg").toString()
                else ""
            } else {
                "Unknown error"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing error response: ${e.message}", e)
            e.message.orEmpty()
        }
        return Message(msg, Message.ERROR, code = this.code())
    }

val Headers.needToRefreshToken: Boolean get() = (this["Token-Refresh-Required"] ?: "0") == "1"

val HttpException.asValidationErrorMessage: Message
    get() {
        val msg: String = try {
            // extract error message body as json like that
            //{"success":0,"message":"...","errors":{"last_name":["..."]}}
            val errorBodyString = response()?.errorBody()?.string()
            if (errorBodyString != null) {
                val errorBody = JSONObject(errorBodyString).toString()
                // deserialize json
                val errorResponse = Gson().fromJson(
                    errorBody,
                    ValidationErrorResponse::class.java
                )
                // extract first validation error message if exists
                if (errorResponse.errors.isNullOrEmpty()) errorResponse.message
                else errorResponse.firstError
            } else {
                this.message.orEmpty()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing validation error: ${e.message}", e)
            this.message.orEmpty()
        }
        return Message(msg, Message.ERROR, code = code())
    }
