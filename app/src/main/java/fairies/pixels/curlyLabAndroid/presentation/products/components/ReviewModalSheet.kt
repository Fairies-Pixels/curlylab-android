package fairies.pixels.curlyLabAndroid.presentation.products.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import fairies.pixels.curlyLabAndroid.R
import fairies.pixels.curlyLabAndroid.domain.model.products.Product
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.presentation.products.viewmodel.ProductsViewModel
import fairies.pixels.curlyLabAndroid.presentation.theme.AccentPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.LightGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightPink
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewModalSheet(
    product: Product,
    onDismiss: () -> Unit,
    viewModel: ProductsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val reviews by viewModel.reviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scope = rememberCoroutineScope()
    val modalState = rememberModalBottomSheetState()
    var rating by rememberSaveable { mutableIntStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }
    var editingReview by rememberSaveable { mutableStateOf<Review?>(null) }

    var userId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var username by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userId = viewModel.getCurrentUserId()
        username = viewModel.getCurrentUsername()
        viewModel.loadReviews(product.id)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalState,
        modifier = modifier,
        containerColor = LightBeige
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightPink), contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(R.drawable.curly_wave),
                            error = painterResource(R.drawable.curly_wave)
                        )
                    }

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (editingReview != null) "Редактировать отзыв" else "Оставить отзыв",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkGreen
                    )

                    Spacer(Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { rating = i }, modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(
                                        if (i <= rating) R.drawable.pink_star
                                        else R.drawable.grey_star_border
                                    ),
                                    contentDescription = "Оценка $i",
                                    tint = if (i <= rating) AccentPink else Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Ваш отзыв") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    if (editingReview != null) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    editingReview = null
                                    rating = 0
                                    comment = ""
                                },
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, DarkGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Отменить", color = DarkGreen)
                            }

                            Button(
                                onClick = {
                                    editingReview?.let { review ->
                                        viewModel.deleteReview(product.id, review.id)
                                        editingReview = null
                                        rating = 0
                                        comment = ""
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentPink, contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Удалить")
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (userId != null && username != null) {
                                scope.launch {
                                    if (editingReview != null) {
                                        viewModel.updateReview(
                                            productId = product.id,
                                            reviewId = editingReview!!.id,
                                            mark = rating,
                                            reviewText = comment
                                        )
                                    } else {
                                        viewModel.submitReview(
                                            productId = product.id,
                                            mark = rating,
                                            reviewText = comment
                                        )
                                    }
                                    editingReview = null
                                    rating = 0
                                    comment = ""
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGreen, contentColor = Color.White
                        ),
                        enabled = rating > 0 && comment.isNotBlank() && userId != null
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White, modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(if (editingReview != null) "Обновить" else "Отправить")
                        }
                    }

                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Все отзывы",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkGreen
                    )
                    Icon(
                        painter = painterResource(R.drawable.pink_star),
                        contentDescription = null,
                        tint = DarkGreen.copy(alpha = 0.6f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            when {
                isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(color = AccentPink)
                    }
                }

                reviews.isEmpty() -> {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Пока нет отзывов",
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(reviews) { review ->
                            val isCurrentUserReview = review.userId == userId
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                            ) {
                                ReviewItem(
                                    review = review,
                                    showMenu = isCurrentUserReview,
                                    onEditClick = if (isCurrentUserReview) {
                                        {
                                            editingReview = review
                                            rating = review.rating
                                            comment = review.comment
                                        }
                                    } else null,
                                    onDeleteClick = if (isCurrentUserReview) {
                                        {
                                            viewModel.deleteReview(product.id, review.id)
                                        }
                                    } else null)
                            }
                        }
                    }
                }
            }
        }
    }
}
