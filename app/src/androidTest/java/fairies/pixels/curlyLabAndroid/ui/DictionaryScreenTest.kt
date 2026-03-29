package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.presentation.dictionary.screen.DictionaryScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DictionaryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dictionary_searchFiltersWords() {
        composeTestRule.setContent {
            DictionaryScreen()
        }

        composeTestRule.onNodeWithText("Диффузор").assertIsDisplayed()

        composeTestRule.onNodeWithText("Поиск")
            .performTextInput("LO")

        composeTestRule.onNodeWithText("LOC").assertIsDisplayed()
        composeTestRule.onNodeWithText("LOG").assertIsDisplayed()

        composeTestRule.onNodeWithText("Диффузор")
            .assertDoesNotExist()
    }
}