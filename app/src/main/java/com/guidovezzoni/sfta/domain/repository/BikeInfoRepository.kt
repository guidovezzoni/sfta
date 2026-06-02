package com.guidovezzoni.sfta.domain.repository

import com.guidovezzoni.sfta.domain.model.BikeInfo

interface BikeInfoRepository {
    suspend fun getBikeInfoSnapshot(): Result<BikeInfo>
}
