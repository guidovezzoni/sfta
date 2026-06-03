package com.guidovezzoni.sfta.di

import android.content.Context
import android.content.res.AssetManager
import com.guidovezzoni.sfta.data.repository.LocalBikeInfoRepository
import com.guidovezzoni.sfta.domain.repository.BikeInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAssetManager(@ApplicationContext context: Context): AssetManager =
        context.assets

    @Provides
    @Singleton
    fun provideBikeInfoRepository(assetManager: AssetManager): BikeInfoRepository =
        LocalBikeInfoRepository(assetManager)
}
