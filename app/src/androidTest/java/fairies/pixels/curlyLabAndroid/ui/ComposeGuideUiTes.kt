package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeGuideUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also {
        composeTestRule = it
    }

    private val TIMEOUT = 5000L

    @Test
    fun guide_expandSection_checkContent() {

        val email = "t${System.currentTimeMillis()}@ya.ru"
        val password = "123456"
        val name = "test_guide_user"

        registerUser(email, name, password)
        loginUser(email, password)


        composeTestRule.onNodeWithText("Гайд")
            .performClick()


        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule
                .onAllNodesWithText("Сушка")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }


        composeTestRule.onNodeWithText("Сушка")
            .performClick()


        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule
                .onAllNodes(
                    hasText("микрофибры", substring = true) or
                            hasText("диффузором", substring = true)
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }


        composeTestRule.onNode(
            hasText("микрофибры", substring = true) or
                    hasText("диффузором", substring = true)
        ).assertExists()

        deleteUser()
    }
}