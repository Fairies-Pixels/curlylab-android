package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginAndLogoutUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun login_thenLogout_successFlow() {
        val email = "e2e_test_${System.currentTimeMillis()}@example.com"
        val password = "123456"
        val name = "test_logout_user"

        registerUser(email = email, name = name, password = password)
        loginUser(email, password)

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Профиль").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("More options").performClick()
        composeTestRule.onNodeWithText("Выйти").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Войти").fetchSemanticsNodes().isNotEmpty()
        }

        loginUser(email, password)
        deleteUser()
    }
}