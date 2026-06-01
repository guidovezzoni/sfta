package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BikeDto(
    @SerialName("model") val model: String,
    @SerialName("variant") val variant: String,
    @SerialName("firmware_version") val firmwareVersion: String,
    @SerialName("image_url") val imageUrl: String,
)
