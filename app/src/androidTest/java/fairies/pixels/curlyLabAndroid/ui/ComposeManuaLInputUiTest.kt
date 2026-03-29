package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeManualInputUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also {
        composeTestRule = it
    }

    private val TIMEOUT = 5000L

    @Test
    fun composition_manualInput_successFlow() {
        val email = "t${System.currentTimeMillis()}@ya.ru"
        val password = "123456"
        val name = "test_comp_user"

        registerUser(email, name, password)
        loginUser(email, password)

        composeTestRule.onNodeWithText("Проверка составов")
            .performClick()
        composeTestRule.waitForIdle()

        val compositionText = "Aqua, Cetyl Alcohol, Dimethicone, Parfum"
        composeTestRule
            .onNode(hasSetTextAction(), useUnmergedTree = true)
            .performTextInput(compositionText)

        composeTestRule
            .onNode(hasText("Проверить") and hasClickAction())
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule
                .onAllNodes(
                    hasText("Обнаружены проблемы:", substring = true) or
                            hasText("Всё в порядке!", substring = true)
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNode(
            hasText("Обнаружены проблемы:", substring = true) or
                    hasText("Всё в порядке!", substring = true)
        ).assertExists()

        Espresso.pressBack()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodesWithText(name)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText(name).assertIsDisplayed()

        deleteUser()
    }
}