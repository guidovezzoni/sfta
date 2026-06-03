package com.guidovezzoni.sfta.di

import android.content.Context
import android.content.res.AssetManager
import com.guidovezzoni.sfta.data.repository.LocalBikeInfoRepository
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppModuleTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @Test
    fun `GIVEN AppModule provides bindings WHEN BikeInfoRepository is requested THEN a LocalBikeInfoRepository instance is returned`() {
        val assetManager = mockk<AssetManager>()
        every { context.assets } returns assetManager

        val providedAssetManager = AppModule.provideAssetManager(context)
        val repository: BikeInfoRepository = AppModule.provideBikeInfoRepository(providedAssetManager)

        assertNotNull(repository)
        assertTrue(repository is LocalBikeInfoRepository)
    }
}
