package fairies.pixels.curlyLabAndroid.presentation.products.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import java.util.UUID

@Composable
fun ProductsList(
    productsToShow: List<Product>,
    onFavoriteClick: (UUID) -> Unit,
    onProductClick: (Product) -> Unit,
    isProductFavorite: (UUID) -> Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(productsToShow) { product ->
            ProductCard(
                product = product,
                isFavorite = isProductFavorite(product.id),
                onFavoriteClick = { onFavoriteClick(product.id) },
                onProductClick = { onProductClick(product) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}