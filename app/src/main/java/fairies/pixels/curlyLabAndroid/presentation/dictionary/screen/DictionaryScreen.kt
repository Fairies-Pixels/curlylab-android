package fairies.pixels.curlyLabAndroid.presentation.dictionary.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fairies.pixels.curlyLabAndroid.presentation.dictionary.viewmodel.DictionaryViewModel
import fairies.pixels.curlyLabAndroid.presentation.dictionary.viewmodel.Word
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        viewModel.filterWords(searchQuery)
    }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(24.dp)
    ) {

        Text(
            text = "Словарь", style = MaterialTheme.typography.displayLarge, color = DarkGreen
        )

        TextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
                viewModel.filterWords(newValue)
            },
            label = { Text(text = "Поиск", color = DarkGreen) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LightBeige,
                focusedContainerColor = LightBeige
            )
        )

        LazyColumn {
            items(viewModel.words.value) { word ->
                WordItem(word)
            }
        }
    }
}

@Composable
fun WordItem(word: Word) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(LightBeige)
                .padding(16.dp)
        ) {
            Text(text = word.name, style = MaterialTheme.typography.titleLarge, color = DarkGreen)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = word.description, style = MaterialTheme.typography.bodyMedium, color = DarkGreen)
        }
    }
}