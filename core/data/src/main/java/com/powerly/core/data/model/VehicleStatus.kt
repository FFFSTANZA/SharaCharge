package com.SharaSpot.core.data.model

import com.SharaSpot.core.model.SharaSpot.VehicleMaker
import com.SharaSpot.core.model.SharaSpot.VehicleModel
import com.SharaSpot.core.model.util.Message

sealed class MakersStatus {
    data class Error(val msg: Message) : MakersStatus()
    data class Success(val makers: Map<String, List<VehicleMaker>>) : MakersStatus()
    data object Loading : MakersStatus()
}

sealed class ModelsStatus {
    data class Error(val msg: Message) : ModelsStatus()
    data class Success(val models: List<VehicleModel>) : ModelsStatus()
    data object Loading : ModelsStatus()
}
