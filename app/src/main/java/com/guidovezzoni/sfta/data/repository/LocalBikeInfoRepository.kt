package com.guidovezzoni.sfta.data.repository

import android.content.res.AssetManager
import com.guidovezzoni.sfta.data.model.BikeInfoSnapshotDto
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private const val ASSET_FILE_NAME = "bike_info_snapshot.json"

class LocalBikeInfoRepository(
    private val assetManager: AssetManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BikeInfoRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getBikeInfoSnapshot(): Result<BikeInfoSnapshotDto> =
        withContext(ioDispatcher) {
            runCatching {
                val jsonString = assetManager.open(ASSET_FILE_NAME)
                    .bufferedReader()
                    .use { it.readText() }
                json.decodeFromString<BikeInfoSnapshotDto>(jsonString)
            }
        }
}
