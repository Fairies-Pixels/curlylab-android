package fairies.pixels.curlyLabAndroid.presentation.products.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import fairies.pixels.curlyLabAndroid.R
import fairies.pixels.curlyLabAndroid.domain.model.products.Review
import fairies.pixels.curlyLabAndroid.presentation.theme.AccentPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReviewItem(
    review: Review,
    showMenu: Boolean,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.userName, fontWeight = FontWeight.Bold, color = DarkGreen
                )
                Text(
                    text = review.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..5) {
                    Icon(
                        painter = painterResource(
                            if (i <= review.rating) R.drawable.pink_star
                            else R.drawable.grey_star_border
                        ),
                        contentDescription = null,
                        tint = if (i <= review.rating) AccentPink else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            if (showMenu) {
                Box {
                    IconButton(
                        onClick = { expanded = true }, modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Действия",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .zIndex(1f)
                            .background(LightBeige.copy(alpha = 0.95f))
                    ) {
                        onEditClick?.let { onClick ->
                            DropdownMenuItem(text = { Text("Редактировать") }, onClick = {
                                onClick()
                                expanded = false
                            }, leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = DarkGreen
                                )
                            })
                        }
                        onDeleteClick?.let { onClick ->
                            DropdownMenuItem(
                                text = { Text("Удалить", color = AccentPink) },
                                onClick = {
                                    onClick()
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = AccentPink
                                    )
                                })
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = review.comment, color = Color.DarkGray, modifier = Modifier.fillMaxWidth()
        )
    }
}