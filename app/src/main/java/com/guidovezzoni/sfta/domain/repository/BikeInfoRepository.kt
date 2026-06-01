package com.guidovezzoni.sfta.domain.repository

import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto

interface BikeInfoRepository {
    suspend fun getBikeInfoSnapshot(): Result<BikeInfoSnapshotDto>
}
