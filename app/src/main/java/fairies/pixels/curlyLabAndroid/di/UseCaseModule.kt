package fairies.pixels.curlyLabAndroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import fairies.pixels.curlyLabAndroid.domain.usecase.products.AddReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.DeleteReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetProductsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetReviewsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetUserFavoritesUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.ToggleFavoriteUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.UpdateReviewUseCase

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddReviewUseCase(repository: ProductsRepository): AddReviewUseCase {
        return AddReviewUseCase(repository)
    }

    @Provides
    fun provideDeleteReviewUseCase(repository: ProductsRepository): DeleteReviewUseCase {
        return DeleteReviewUseCase(repository)
    }

    @Provides
    fun provideGetProductsUseCase(repository: ProductsRepository): GetProductsUseCase {
        return GetProductsUseCase(repository)
    }

    @Provides
    fun provideGetUserFavoritesUseCase(repository: ProductsRepository): GetUserFavoritesUseCase {
        return GetUserFavoritesUseCase(repository)
    }

    @Provides
    fun provideGetReviewsUseCase(repository: ProductsRepository): GetReviewsUseCase {
        return GetReviewsUseCase(repository)
    }

    @Provides
    fun provideToggleFavoriteUseCase(repository: ProductsRepository): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(repository)
    }

    @Provides
    fun provideUpdateReviewUseCase(repository: ProductsRepository): UpdateReviewUseCase {
        return UpdateReviewUseCase(repository)
    }
}