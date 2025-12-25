package fairies.pixels.curlyLabAndroid.presentation.composition.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import fairies.pixels.curlyLabAndroid.presentation.composition.screen.viewmodel.CompositionViewModel
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.Golos
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige02

@Composable
fun CompositionCheckScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: CompositionViewModel = hiltViewModel()

    val inputText by viewModel.inputText
    val result by viewModel.result.collectAsState()

    val UriSaver: Saver<Uri?, String> = Saver(
        save = { it?.toString() ?: "" },
        restore = { if (it.isNotEmpty()) Uri.parse(it) else null }
    )

    var imageUri by rememberSaveable(stateSaver = UriSaver) {
        mutableStateOf<Uri?>(null)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val maxLength = 2000
    val isLimitExceeded = inputText.length > maxLength
    val canCheck = !isLimitExceeded && inputText.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Проверка состава",
                style = MaterialTheme.typography.displayLarge,
                color = DarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.onInputTextChange(it) },
                    label = {
                        Text(
                            "Введите или вставьте состав",
                            fontFamily = Golos,
                            color = DarkGreen
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 10,
                    isError = isLimitExceeded,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = DarkGreen.copy(alpha = 0.5f),
                        focusedLabelColor = DarkGreen,
                        unfocusedLabelColor = DarkGreen.copy(alpha = 0.7f),
                        cursorColor = DarkGreen,
                        focusedContainerColor = LightBeige02,
                        unfocusedContainerColor = LightBeige02,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${inputText.length} / $maxLength",
                        color = if (isLimitExceeded) Color.Red else Color.Gray,
                        fontFamily = Golos
                    )
                }

                if (isLimitExceeded) {
                    Text(
                        text = "Превышен лимит в 2000 символов",
                        color = Color.Red,
                        fontFamily = Golos
                    )
                }
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            if (result.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = result,
                    color = DarkGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = BrightPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Загрузить фото", color = Color.White, fontFamily = Golos)
            }

            Button(
                onClick = { viewModel.analyze(context, imageUri) },
                enabled = canCheck,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkGreen,
                    disabledContainerColor = DarkGreen.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text(
                    text = if (isLimitExceeded) "Слишком длинный текст" else "Проверить",
                    color = if (canCheck) Color.White else Color.White.copy(alpha = 0.7f),
                    fontFamily = Golos
                )
            }
        }
    }
}