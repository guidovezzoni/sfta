package com.guidovezzoni.sfta.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BikeDto(
    @SerialName("model") val model: String? = null,
    @SerialName("variant") val variant: String? = null,
    @SerialName("firmware_version") val firmwareVersion: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
)
