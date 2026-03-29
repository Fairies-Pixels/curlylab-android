package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterProductsUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun filterProducts_byTags() {
        val email = "t${System.currentTimeMillis()}@test.com"
        val password = "123456"
        val name = "filter_user"

        registerUser(email, name, password)
        loginUser(email, password)

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("База средств").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasContentDescription("Фильтры")).performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Высокая")[0].performClick()
        composeTestRule.onAllNodesWithText("Толстые")[0].performClick()
        composeTestRule.onAllNodesWithText("Да")[0].performClick()

        composeTestRule.onNodeWithText("Сохранить").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Кондиционер PROКУДРИ").fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.waitForIdle()

        Espresso.pressBack()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profile").performClick()

        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodesWithText(name).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(name).assertIsDisplayed()

        deleteUser()
    }
}