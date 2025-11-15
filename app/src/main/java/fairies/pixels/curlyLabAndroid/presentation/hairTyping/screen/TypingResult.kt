package fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fairies.pixels.curlyLabAndroid.R
import fairies.pixels.curlyLabAndroid.presentation.theme.PurpleGrey40
import coil.compose.AsyncImage
import fairies.pixels.curlyLabAndroid.presentation.theme.Golos
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.Pink40

@Composable
fun TypingResult(result: String, onSave: () -> Unit, onNotSave: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        val text = buildAnnotatedString {
            append("У вас ")
            withStyle(style = MaterialTheme.typography.displayLarge.toSpanStyle().copy(color = Pink40)) {
                append(result)
            }
            append(" волосы!")
        }
        BasicText(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                textAlign = TextAlign.Center,
                color = PurpleGrey40
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        AsyncImage(
            model = R.drawable.hairtype,
            contentDescription = "icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    onNotSave()
                },
                border = BorderStroke(
                    width = 2.dp,
                    color = PurpleGrey40
                )
            ) {
                Text(
                    text = "Пока не сохранять",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = Golos),
                    color = PurpleGrey40
                )
            }
            Button(
                onClick = {
                    onSave()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurpleGrey40)
            )
            {
                Text(
                    text = "Сохранить",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = Golos),
                    color = LightBeige
                )
            }
        }
    }
}