package fairies.pixels.curlyLabAndroid.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

abstract class BaseAuthTest<A : ComponentActivity> {

    protected lateinit var composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<A>, A>

    fun registerUser(email: String, name: String, password: String) {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasText("Зарегистрироваться") and hasClickAction())
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Имя").performTextInput(name)
        composeTestRule.onNodeWithText("Пароль").performTextInput(password)
        composeTestRule.onNodeWithText("Подтвердите пароль").performTextInput(password)
        composeTestRule.onNodeWithText("Зарегистрироваться").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Вход").fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun loginUser(email: String, password: String) {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Email").performTextInput(email)
        composeTestRule.onNodeWithText("Пароль").performTextInput(password)
        composeTestRule.onNodeWithText("Войти").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Профиль").fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun deleteUser() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Профиль").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Удалить аккаунт").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Удалить").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Войти").assertIsDisplayed()
    }
}