package fairies.pixels.curlyLabAndroid.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.data.repository.analysis.AnalysisRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.repository.auth.AuthRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.repository.products.ProductsRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.repository.profile.HairTypesRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.repository.profile.UsersRepositoryImpl
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHairTypesRepository(
        impl: HairTypesRepositoryImpl
    ): HairTypesRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepository(
        impl: UsersRepositoryImpl
    ): UsersRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(
        impl: AnalysisRepositoryImpl
    ): AnalysisRepository
}