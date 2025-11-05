package com.SharaSpot.core.model.SharaSpot

import com.SharaSpot.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName


class ChargingResponse : BaseResponse<Session?>()

class StartChargingBody(
    @SerializedName("power_source_id") private val powersourceId: String,
    @SerializedName("quantity") private val quantity: String,
    @SerializedName("connector") private val connector: Int?
)

class StopChargingBody(
    @SerializedName("order_id") private val orderId: String,
    @SerializedName("power_source_id") private val powersourceId: String,
    @SerializedName("connector") private val connector: Int?
)

