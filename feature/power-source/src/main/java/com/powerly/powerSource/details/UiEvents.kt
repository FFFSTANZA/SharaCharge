package com.SharaSpot.powerSource.details

import com.powerly.core.model.location.Target

sealed class SourceEvents() {
    data object Close : SourceEvents()
    data object Reviews : SourceEvents()
    data object Media : SourceEvents()
    data object HowToCharge : SourceEvents()
    data object Balance : SourceEvents()
    data object Charge : SourceEvents()
    data object Contribute : SourceEvents()
    data class CAll(val contact: String?) : SourceEvents()
    data class DriveToStation(val target: Target) : SourceEvents()

    /**
     * View a specific photo in full screen
     * @param photoUrl URL of the photo to display
     */
    data class ViewPhoto(val photoUrl: String) : SourceEvents()
}
