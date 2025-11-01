package fairies.pixels.curlyLabAndroid.presentation.home.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fairies.pixels.curlyLabAndroid.presentation.navigation.Screen
import fairies.pixels.curlyLabAndroid.presentation.theme.AccentPink
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightPink

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Привет!", style = MaterialTheme.typography.displayLarge, color = DarkGreen
        )

        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(color = LightPink,
                        title = "Проверка составов",
                        height = 280.dp,
                        onClick = { navController.navigate(Screen.CompositionCheck.route) })
                    FeatureCard(color = DarkGreen, title = "Гайд", height = 132.dp, onClick = {
                        navController.navigate(Screen.Guide.route)
                    })
                }
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FeatureCard(color = AccentPink,
                        title = "Словарь",
                        height = 132.dp,
                        onClick = { navController.navigate(Screen.Dictionary.route) })
                    FeatureCard(color = LightGreen,
                        title = "Типизация волос",
                        height = 280.dp,
                        onClick = { navController.navigate(Screen.HairTyping.route) })
                }
            }

            item(span = { GridItemSpan(2) }) {
                FeatureCard(color = BrightPink,
                    title = "База средств",
                    height = 132.dp,
                    onClick = { navController.navigate(Screen.Products.route) })
            }
        }
    }
}


@Composable
private fun FeatureCard(
    color: Color, title: String, height: Dp, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() }, contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}
