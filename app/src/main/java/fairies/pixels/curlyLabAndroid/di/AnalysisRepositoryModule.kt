package fairies.pixels.curlyLabAndroid.di


import fairies.pixels.curlyLabAndroid.data.repository.analysis.AnalysisRepositoryImpl
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalysisRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(
        impl: AnalysisRepositoryImpl
    ): AnalysisRepository
}