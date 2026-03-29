package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddReviewUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun addReview_successFlow() {
        val email = "t${System.currentTimeMillis()}@test.com"
        val password = "123456"
        val name = "review_user"
        val reviewText = "Отличный продукт! Очень довольна покупкой."

        registerUser(email, name, password)
        loginUser(email, password)

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("База средств").performClick()

        composeTestRule.waitUntil(timeoutMillis = 15000) {
            composeTestRule.onAllNodesWithText("Кондиционер PROКУДРИ").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Кондиционер PROКУДРИ").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Оставить отзыв").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Оценка 4").performClick()

        composeTestRule.onNodeWithText("Ваш отзыв").performTextInput(reviewText)

        composeTestRule.onAllNodes(hasText("Отправить") and isEnabled())[0].performClick()

        Espresso.pressBack()
        composeTestRule.waitForIdle()
        Espresso.pressBack()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Удалить аккаунт").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Удалить").performClick()
    }
}