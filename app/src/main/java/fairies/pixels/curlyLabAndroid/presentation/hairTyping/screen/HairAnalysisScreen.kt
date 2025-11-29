package fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel.HairAnalysisViewModel
import fairies.pixels.curlyLabAndroid.presentation.theme.*
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HairAnalysisScreen(
    navController: NavController,
    viewModel: HairAnalysisViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // ---------- URI SAVER (как в CompositionCheckScreen) ----------
    val UriSaver: Saver<Uri?, String> = Saver(
        save = { it?.toString() ?: "" },
        restore = { if (it.isNotEmpty()) Uri.parse(it) else null }
    )

    var selectedImageUri by rememberSaveable(stateSaver = UriSaver) {
        mutableStateOf<Uri?>(null)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri

        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                viewModel.analyze(bytes)
            }
        }
    }

    // ---------- BOTTOM SHEET ----------
    val bottomSheetState = rememberBottomSheetScaffoldState()

    LaunchedEffect(result, error) {
        if (!result.isNullOrEmpty() || error != null) {
            bottomSheetState.bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        sheetPeekHeight = 48.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrightPink)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "≋ꕤ≋𐤀≋ꕤ≋𐤀≋ꕤ≋𐤀≋ꕤ≋𐤀≋ꕤ",
                    color = LightBeige,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = Golos,
                        fontSize = 28.sp
                    )
                )
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrightPink)
                    .padding(16.dp)
            ) {
                Text(
                    text = when {
                        isLoading -> "Ожидаем анализ…"
                        error != null -> "Ошибка: $error"
                        !result.isNullOrEmpty() -> "Тип волос: ${result!!.uppercase()}"
                        else -> "Загрузите фото"
                    },
                    fontFamily = Golos,
                    fontSize = 20.sp,
                    color = LightBeige
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LightBeige)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Определение типа волос",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = Golos,
                        fontSize = 30.sp
                    ),
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = """
                        Загрузи фото, на котором хорошо видно твои волосы.
                        Желательно — со спины или вид сбоку.
                        
                        Нейросеть определит тип за пару секунд 💛
                    """.trimIndent(),
                    color = DarkGreen,
                    fontFamily = Golos,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ---------- Выбранное фото ----------
                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color.White, shape = RoundedCornerShape(16.dp))
                    )
                }
            }

            // ---------- Кнопки ----------
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightPink),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Загрузить фото", color = Color.White, fontFamily = Golos)
                }
            }
        }
    }
}
