package fairies.pixels.curlyLabAndroid.presentation.guide.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import fairies.pixels.curlyLabAndroid.presentation.theme.*
import android.content.Context
import androidx.compose.foundation.clickable



@Composable
fun GuideScreen(navController: NavController) {
    val context = LocalContext.current
    var sections by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(Unit) {
        val rawText = loadTextFromAssets(context, "guide_detailed.txt")
        sections = parseGuideSections(rawText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sections.forEachIndexed { index, (title, content) ->
            val isExpanded = expandedStates[index] ?: false

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedStates[index] = !isExpanded },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightBeige02)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        fontFamily = Golos,
                        style = MaterialTheme.typography.titleLarge,
                        color = BrightPink
                    )
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = content,
                            fontFamily = Golos,
                            style = MaterialTheme.typography.bodyLarge,
                            color = DarkGreen
                        )
                    }
                }
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = BrightPink),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Вернуться назад",
                color = Color.White,
                fontFamily = Golos,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}



fun loadTextFromAssets(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        "Ошибка загрузки гайда: ${e.localizedMessage}"
    }
}

fun parseGuideSections(text: String): List<Pair<String, String>> {
    val sections = mutableListOf<Pair<String, String>>()
    val lines = text.lines()
    var currentTitle: String? = null
    val currentContent = StringBuilder()

    for (line in lines) {
        if (line.startsWith("#")) {
            currentTitle?.let {
                sections.add(it to currentContent.toString().trim())
                currentContent.clear()
            }
            currentTitle = line.removePrefix("# ").trim()
        } else {
            currentContent.appendLine(line)
        }
    }

    currentTitle?.let {
        sections.add(it to currentContent.toString().trim())
    }

    return sections
}


