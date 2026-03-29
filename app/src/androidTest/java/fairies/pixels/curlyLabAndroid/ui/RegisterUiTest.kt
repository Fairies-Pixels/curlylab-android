package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RegisterUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun registerNewUser_success() {
        val email = "t${System.currentTimeMillis()}@test.com"
        val password = "123456"
        val name = "test_user_name"

        registerUser(email, name, password)

        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodesWithText("Вход").fetchSemanticsNodes().isNotEmpty()
        }

        loginUser(email, password)

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Profile").performClick()

        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodesWithText(name).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(name).assertIsDisplayed()

        deleteUser()
    }
}