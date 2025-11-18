package fairies.pixels.curlyLabAndroid.presentation.products.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.Golos
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige

@Composable
fun SegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    defaultSelectedItemIndex: Int = 0,
    onItemSelection: (Int) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableStateOf(defaultSelectedItemIndex) }

    Row(
        modifier = modifier
            .background(LightBeige, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        selectedIndex = index
                        onItemSelection(index)
                    }
                    .background(
                        color = if (selectedIndex == index) BrightPink else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = if (selectedIndex == index) LightBeige else DarkGreen,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Golos)
                )
            }
        }
    }
}