package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PorosityTextTypingUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun completePorosityTest_allQuestionsAnswered_resultSaved() {
        val email = "t${System.currentTimeMillis()}@ya.ru"
        val password = "123456"
        val name = "test_porosity_user"

        registerUser(email, name, password)
        loginUser(email, password)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Типизация волос").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Пористость волос").performClick()

        val totalQuestions = 6

        repeat(totalQuestions) { questionIndex ->
            composeTestRule.onAllNodes(hasClickAction())
                .onFirst()
                .performClick()

            composeTestRule.onNodeWithText(
                if (questionIndex + 1 == totalQuestions) "К результату" else "Следующий"
            ).performClick()

            composeTestRule.waitForIdle()
        }

        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodesWithText("У вас пористые волосы!").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodes(hasText("Сохранить")).onFirst().performClick()
    }
}