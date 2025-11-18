package fairies.pixels.curlyLabAndroid.presentation.products.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.presentation.products.components.ErrorMessage
import fairies.pixels.curlyLabAndroid.presentation.products.components.FilterPanel
import fairies.pixels.curlyLabAndroid.presentation.products.components.ProductsList
import fairies.pixels.curlyLabAndroid.presentation.products.components.ProductsLoading
import fairies.pixels.curlyLabAndroid.presentation.products.components.ReviewModalSheet
import fairies.pixels.curlyLabAndroid.presentation.products.components.SegmentedControl
import fairies.pixels.curlyLabAndroid.presentation.products.viewmodel.ProductsTab
import fairies.pixels.curlyLabAndroid.presentation.products.viewmodel.ProductsViewModel
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.Golos
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige

@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val favoriteProducts by viewModel.favoriteProducts.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val porosityTag by viewModel.porosityTag.collectAsState()
    val coloringTag by viewModel.coloringTag.collectAsState()
    val thicknessTag by viewModel.thicknessTag.collectAsState()

    var selectedProduct by rememberSaveable { mutableStateOf<Product?>(null) }
    var isFilterVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "База средств",
                style = MaterialTheme.typography.displayLarge,
                color = DarkGreen,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentedControl(
                    modifier = Modifier.weight(1f),
                    items = listOf("Все продукты", "Избранное"),
                    defaultSelectedItemIndex = if (selectedTab == ProductsTab.ALL_PRODUCTS) 0 else 1,
                    onItemSelection = { selectedIndex ->
                        when (selectedIndex) {
                            0 -> viewModel.selectTab(ProductsTab.ALL_PRODUCTS)
                            1 -> viewModel.selectTab(ProductsTab.FAVORITE_PRODUCTS)
                        }
                    }
                )

                ElevatedCard(
                    modifier = Modifier.clickable { isFilterVisible = !isFilterVisible },
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = BrightPink)
                ) {
                    Text(
                        text = "⋮",
                        color = LightBeige,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = Golos)
                    )
                }
            }

            AnimatedVisibility(
                visible = isFilterVisible,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                FilterPanel(
                    porosityTag = porosityTag,
                    coloringTag = coloringTag,
                    thicknessTag = thicknessTag,
                    onTagClick = viewModel::toggleTag,
                    onReset = {
                        viewModel.clearAllFilters()
                        isFilterVisible = false
                    },
                    onSave = { isFilterVisible = false }
                )
            }

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> ProductsLoading()
                error != null -> ErrorMessage(error ?: "Неизвестная ошибка")
                else -> {
                    val productsToShow = when (selectedTab) {
                        ProductsTab.ALL_PRODUCTS -> filteredProducts
                        ProductsTab.FAVORITE_PRODUCTS -> favoriteProducts
                    }

                    ProductsList(
                        productsToShow = productsToShow,
                        onProductClick = { product ->
                            selectedProduct = product
                            viewModel.loadReviews(product.id)
                        },
                        onFavoriteClick = { id -> viewModel.toggleFavorite(id) },
                        isProductFavorite = { id ->
                            favorites.any { it.productId == id }
                        }
                    )
                }
            }
        }

        selectedProduct?.let { product ->
            ReviewModalSheet(
                product = product,
                onDismiss = { selectedProduct = null },
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}