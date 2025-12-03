package fairies.pixels.curlyLabAndroid.presentation.profile.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import fairies.pixels.curlyLabAndroid.R
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import fairies.pixels.curlyLabAndroid.presentation.navigation.Screen
import fairies.pixels.curlyLabAndroid.presentation.profile.viewmodel.ProfileViewModel
import fairies.pixels.curlyLabAndroid.presentation.theme.BrightPink
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightPink

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val error by viewModel.error.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val hairType by viewModel.hairType.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logoutState, deleteState) {
        if (logoutState?.isSuccess == true || deleteState?.isSuccess == true) {
            navController.navigate(Screen.AuthGraph.route) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.resetStates()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = DarkGreen),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ProfileImagePicker(
                            profileViewModel = viewModel
                        )


                        Text(
                            text = userName ?: "Имя",
                            style = MaterialTheme.typography.headlineMedium,
                            color = LightPink
                        )

                        Box {
                            IconButton(
                                onClick = { expanded = true }
                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = LightPink,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .border(
                                            1.dp,
                                            LightPink,
                                            RoundedCornerShape(8.dp)
                                        )
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .zIndex(1f)
                                    .background(BrightPink.copy(alpha = 0.95f))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Выйти") },
                                    onClick = {
                                        viewModel.logout()
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                            contentDescription = null,
                                            tint = LightPink
                                        )
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Удалить аккаунт") },
                                    onClick = {
                                        showDeleteDialog = true
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = LightPink
                                        )
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Удалить аватар") },
                                    onClick = {
                                        viewModel.deleteAvatar()
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.RemoveCircle,
                                            contentDescription = null,
                                            tint = LightPink
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {
                        HairTypeCard(
                            R.drawable.porosity,
                            hairType?.porosity?.let { PorosityTypes.getResultNameByDbCode(it) }
                                ?: "Пористость"
                        )
                        HairTypeCard(
                            R.drawable.thin,
                            hairType?.thickness?.let { ThicknessTypes.getResultNameByDbCode(it) }
                                ?: "Толщина"
                        )
                        HairTypeCard(
                            R.drawable.color, when (hairType?.isColored) {
                                true -> "Окрашенные"
                                false -> "Неокрашенные"
                                else -> "Окрашенность"
                            }
                        )
                    }
                }
            }

            when {
                error != null -> ErrorMessage(error ?: "Неизвестная ошибка")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удаление аккаунта") },
            text = { Text("Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ProfileImagePicker(
    profileViewModel: ProfileViewModel
) {
    val imageUrl by profileViewModel.imageUrl.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { profileViewModel.uploadAvatar(it) }
    }

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable { launcher.launch("image/*") }
            .border(2.dp, LightPink, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Аватар пользователя",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Загрузить фото",
                tint = LightPink,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun HairTypeCard(icon: Int, text: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = LightPink),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp)
        ) {
            AsyncImage(
                model = icon,
                contentDescription = "icon",
                contentScale = ContentScale.Fit,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = DarkGreen.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}