package fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightPink

@Composable
fun HairTypingScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding(24.dp)
    )
    {
        Text(
            text = "Типизация волос",
            style = MaterialTheme.typography.displayLarge,
            color = DarkGreen
        )

        Spacer(Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Пройдите тесты для определения Вашего типа волос",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkGreen
                )
            }

            item {
                FeatureCard(
                    color = AccentPink,
                    title = "Пористость по фото",
                    height = 120.dp,
                    onClick = {navController.navigate(Screen.HairAnalysis.route)  }
                )
            }

            item {
                FeatureCard(
                    color = LightPink,
                    title = "Пористость волос",
                    height = 120.dp,
                    onClick = { navController.navigate(Screen.PorosityTextTyping.route) }
                )
            }

            item {
                FeatureCard(
                    color = LightGreen,
                    title = "Толщина волос",
                    height = 120.dp,
                    onClick = { navController.navigate(Screen.ThicknessTextTyping.route) }
                )
            }

            item {
                FeatureCard(
                    color = DarkGreen,
                    title = "Окрашенность волос",
                    height = 120.dp,
                    onClick = { navController.navigate(Screen.IsColoredTextTyping.route)  }
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    color: Color,
    title: String,
    height: Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}