package com.guidovezzoni.sfta.domain.usecase

import com.guidovezzoni.sfta.domain.model.BikeInfo
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import javax.inject.Inject

class GetBikeInfoUseCase @Inject constructor(private val repository: BikeInfoRepository) {

    suspend operator fun invoke(): Result<BikeInfo> =
        repository.getBikeInfoSnapshot()
}
