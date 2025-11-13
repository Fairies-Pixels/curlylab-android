package fairies.pixels.curlyLabAndroid.presentation.guide.screen

import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen

@Composable
fun GuideScreen(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Гайд по уходу за кудрями",
            style = MaterialTheme.typography.headlineSmall,
            color = DarkGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = """
                ВРЕМЕННЫЙ
                1. Используй шампунь без сульфатов.
                2. Не расчесывай сухие волосы.
                3. Обильно используй кондиционер.
                4. Применяй метод сквиш-ту-кондиш.
                5. Используй микрофибровое полотенце.
                6. Не бойся геля – он фиксирует форму.
                7. Дай волосам высохнуть естественно.
                
            """.trimIndent(),
            color = DarkGreen,
            textAlign = TextAlign.Start
        )
    }
}
