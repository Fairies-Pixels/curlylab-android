package fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel.IsColoredTextTypingViewModel
import fairies.pixels.curlyLabAndroid.presentation.navigation.Screen
import fairies.pixels.curlyLabAndroid.presentation.theme.DarkGreen
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.Pink40
import fairies.pixels.curlyLabAndroid.presentation.theme.PurpleGrey40

@Composable
fun IsColoredTextTypingScreen(
    viewModel: IsColoredTextTypingViewModel = hiltViewModel(),
    navController: NavController
) {
    val questions by viewModel.questions.collectAsState()
    val currentQuestionId by viewModel.currentQuestionId.collectAsState()
    val result by viewModel.result.collectAsState()
    val saved by viewModel.saved.collectAsState()

    Box(
        modifier = Modifier
            .background(DarkGreen),
    ) {

        when (result) {
            null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                ) {

                    val size by animateFloatAsState(
                        targetValue = (currentQuestionId + 1) / (questions.size.toFloat()),
                        tween(
                            durationMillis = 1000,
                            delayMillis = 200,
                            easing = LinearOutSlowInEasing
                        )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 15.dp, end = 15.dp)
                    ) {

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        ) {
                            Text(
                                text = "Тест на окрашенность",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    textAlign = TextAlign.Center
                                ),
                                fontWeight = FontWeight.Medium,
                                color = LightBeige,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(LightBeige)
                            )
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth(size)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(Pink40)
                                    .animateContentSize()
                            ) {
                                Text(
                                    text = "${currentQuestionId + 1}/${questions.size}",
                                    color = LightBeige
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(30.dp),
                                modifier = Modifier
                                    .padding(top = 40.dp, bottom = 50.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(LightBeige),

                                    ) {
                                    Column {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                                        ) {
                                            Text(
                                                text = questions[currentQuestionId].topic,
                                                style = MaterialTheme.typography.headlineSmall.copy(
                                                    textAlign = TextAlign.Center
                                                ),
                                                fontWeight = FontWeight.Medium,
                                                color = PurpleGrey40,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 40.dp, vertical = 30.dp),
                                        ) {

                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                questions[currentQuestionId].answers.forEachIndexed { id, answer ->
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(16.dp))
                                                            .background(
                                                                if (answer.isSelected) {
                                                                    Pink40
                                                                } else {
                                                                    PurpleGrey40
                                                                }
                                                            )
                                                            .padding(12.dp)
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                viewModel.answering(
                                                                    id,
                                                                    currentQuestionId
                                                                )
                                                            },
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = answer.text,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = LightBeige
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (currentQuestionId > 0) {
                                                viewModel.previousQuestion()
                                            }
                                        },
                                        border = BorderStroke(
                                            width = 2.dp,
                                            color = if (currentQuestionId != 0) {
                                                PurpleGrey40
                                            } else {
                                                Color.Transparent
                                            }
                                        ),
                                        enabled = currentQuestionId != 0
                                    ) {
                                        Text(
                                            text = "Предыдущий",
                                            color = if (currentQuestionId != 0) {
                                                PurpleGrey40
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (currentQuestionId + 1 < questions.size) {
                                                viewModel.nextQuestion()
                                            } else {
                                                viewModel.getResult()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PurpleGrey40)
                                    )
                                    {
                                        Text(
                                            text = if (currentQuestionId + 1 == questions.size) {
                                                "К результату"
                                            } else {
                                                "Следующий"
                                            }, color = LightBeige
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                TypingResult(
                    result!!,
                    onSave = { viewModel.saveResult() },
                    onNotSave = { navController.navigate(Screen.Main.route)
                        navController.navigate(Screen.HairTyping.route)
                    }
                )
            }
        }

        val duration = Toast.LENGTH_SHORT
        val context = LocalContext.current
        when (saved) {
            true -> {
                Toast.makeText(context, "Результат успешно сохранен", duration).show()
                navController.navigate(Screen.Main.route)
                navController.navigate(Screen.HairTyping.route)
            }

            false -> {
                Toast.makeText(context, "Ошибка, не удалось сохранить результат", duration).show()
                navController.navigate(Screen.Main.route)
                navController.navigate(Screen.HairTyping.route)
            }

            null -> {}
        }
    }
}