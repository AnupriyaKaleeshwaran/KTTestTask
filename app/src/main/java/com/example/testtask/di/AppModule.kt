package com.example.testtask.di

import com.example.testtask.data.model.Location
import com.example.testtask.data.model.User
import com.example.testtask.data.repository.AuthRepository
import com.example.testtask.data.repository.LocationRepository
import com.example.testtask.util.RealmManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val config = RealmConfiguration.Builder(
            schema = setOf(User::class, Location::class)
        )
            .name("location.realm")
            .build()

        return Realm.open(config)
    }

    @Provides
    @Singleton
    fun provideRealmManager(realm: Realm): RealmManager = RealmManager(realm)

    @Provides
    @Singleton
    fun provideAuthRepository(realmManager: RealmManager): AuthRepository =
        AuthRepository(realmManager)

    @Provides
    @Singleton
    fun provideLocationRepository(realmManager: RealmManager): LocationRepository =
        LocationRepository(realmManager)
}
