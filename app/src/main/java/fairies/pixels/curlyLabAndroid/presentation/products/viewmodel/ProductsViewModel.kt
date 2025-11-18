package fairies.pixels.curlyLabAndroid.presentation.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.domain.model.products.Favorite
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.domain.usecase.products.AddReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.DeleteReviewUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetProductsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetReviewsUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.GetUserFavoritesUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.ToggleFavoriteUseCase
import fairies.pixels.curlyLabAndroid.domain.usecase.products.UpdateReviewUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getUserFavoritesUseCase: GetUserFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val addReviewUseCase: AddReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedTab = MutableStateFlow(ProductsTab.ALL_PRODUCTS)
    val selectedTab: StateFlow<ProductsTab> = _selectedTab.asStateFlow()

    private val _porosityTag = MutableStateFlow<String?>(null)
    private val _coloringTag = MutableStateFlow<String?>(null)
    private val _thicknessTag = MutableStateFlow<String?>(null)

    val porosityTag: StateFlow<String?> = _porosityTag.asStateFlow()
    val coloringTag: StateFlow<String?> = _coloringTag.asStateFlow()
    val thicknessTag: StateFlow<String?> = _thicknessTag.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        _products,
        _porosityTag,
        _coloringTag,
        _thicknessTag
    ) { allProducts, porosity, coloring, thickness ->
        allProducts.filter { product ->
            val tags = product.tags.toSet()
            val porosityMatch = porosity == null || tags.contains(porosity)
            val coloringMatch = coloring == null || tags.contains(coloring)
            val thicknessMatch = thickness == null || tags.contains(thickness)
            porosityMatch && coloringMatch && thicknessMatch
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteProducts: StateFlow<List<Product>> = combine(
        _products,
        _favorites
    ) { allProducts, favorites ->
        allProducts.filter { product ->
            favorites.any { favorite -> favorite.productId == product.id }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadProducts()
        loadFavorites()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _products.value = getProductsUseCase()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки товаров: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                _favorites.value = getUserFavoritesUseCase(userId)
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки избранного: ${e.message}"
            }
        }
    }

    fun loadReviews(productId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userName = getCurrentUsername()
                _reviews.value = getReviewsUseCase(productId, userName)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки отзывов: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(productId: UUID) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                toggleFavoriteUseCase(userId, productId)

                val isCurrentlyFavorite = _favorites.value.any { it.productId == productId }
                if (isCurrentlyFavorite) {
                    _favorites.value = _favorites.value.filter { it.productId != productId }
                } else {
                    _favorites.value = _favorites.value + Favorite(userId, productId)
                }
            } catch (e: Exception) {
                _error.value = "Не удалось обновить избранное: ${e.message}"
            }
        }
    }

    fun submitReview(productId: UUID, mark: Int, reviewText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = addReviewUseCase(productId, mark, reviewText)
                if (response.isSuccessful) {
                    loadReviews(productId)
                    _error.value = null
                } else {
                    _error.value = "Ошибка при отправке отзыва: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка при отправке отзыва: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReview(productId: UUID, reviewId: UUID, mark: Int, reviewText: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = getCurrentUserId()
                val request = ReviewRequest(
                    userId = userId,
                    mark = mark,
                    review = reviewText
                )
                updateReviewUseCase(productId, reviewId, request)
                loadReviews(productId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка при обновлении отзыва: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteReview(productId: UUID, reviewId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteReviewUseCase(productId, reviewId)
                _reviews.value = _reviews.value.filter { it.id != reviewId }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении отзыва: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getCurrentUserId(): UUID {
        return UUID.fromString("11111111-1111-1111-1111-111111111111")
    }

    suspend fun getCurrentUsername(): String {
        return "user1"
    }

    fun toggleTag(category: String, tag: String) {
        when (category) {
            "Пористость" -> _porosityTag.value = if (_porosityTag.value == tag) null else tag
            "Окрашенность" -> _coloringTag.value = if (_coloringTag.value == tag) null else tag
            "Толщина" -> _thicknessTag.value = if (_thicknessTag.value == tag) null else tag
        }
    }

    fun selectTab(tab: ProductsTab) {
        _selectedTab.value = tab
    }

    fun clearAllFilters() {
        _porosityTag.value = null
        _coloringTag.value = null
        _thicknessTag.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun refreshData() {
        loadProducts()
        loadFavorites()
    }
}

enum class ProductsTab {
    ALL_PRODUCTS,
    FAVORITE_PRODUCTS
}