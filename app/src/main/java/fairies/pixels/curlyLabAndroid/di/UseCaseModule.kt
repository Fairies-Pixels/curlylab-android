package fairies.pixels.curlyLabAndroid.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.domain.repository.products.ProductsRepository
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignInUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.SignUpUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.auth.ValidatePasswordUseCase
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

    @Provides
    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCase(authRepository)
    }

    @Provides
    fun provideSignInUseCase(authRepository: AuthRepository): SignInUseCase {
        return SignInUseCase(authRepository)
    }

    @Provides
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase {
        return ValidatePasswordUseCase()
    }
}