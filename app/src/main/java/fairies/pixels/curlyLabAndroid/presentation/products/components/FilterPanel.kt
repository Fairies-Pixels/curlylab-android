package fairies.pixels.curlyLabAndroid.presentation.products.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.Golos
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige

@Composable
fun FilterPanel(
    porosityTag: String?,
    coloringTag: String?,
    thicknessTag: String?,
    onTagClick: (String, String) -> Unit,
    onReset: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                LightBeige,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(16.dp)
    ) {
        TagFilterSheet(
            selectedTags = mapOf(
                "Пористость" to porosityTag,
                "Окрашенность" to coloringTag,
                "Толщина" to thicknessTag
            ),
            onTagClick = onTagClick,
            onClose = onSave
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ElevatedCard(
                onClick = onReset,
                colors = CardDefaults.cardColors(containerColor = BrightPink)
            ) {
                Text(
                    text = "Сбросить фильтры",
                    color = LightBeige,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = Golos)
                )
            }

            ElevatedCard(
                onClick = onSave,
                colors = CardDefaults.cardColors(containerColor = DarkGreen)
            ) {
                Text(
                    text = "Сохранить",
                    color = LightBeige,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = Golos)
                )
            }
        }
    }
}