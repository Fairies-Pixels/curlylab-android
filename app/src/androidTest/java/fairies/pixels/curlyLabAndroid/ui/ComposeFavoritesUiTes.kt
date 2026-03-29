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
class ComposeFavoritesUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also {
        composeTestRule = it
    }

    private val TIMEOUT = 5000L

    @Test
    fun favorites_addAndCheckFlow() {
        val email = "t${System.currentTimeMillis()}@ya.ru"
        val password = "123456"
        val name = "test_fav_user"

        registerUser(email, name, password)
        loginUser(email, password)

        composeTestRule.onNodeWithText("База средств").performClick()
        composeTestRule.waitForIdle()

        val favoriteButtons = composeTestRule
            .onAllNodes(hasContentDescription("Добавить в избранное"))

        val buttonsCount = favoriteButtons.fetchSemanticsNodes().size
        assert(buttonsCount >= 2) {
            "Ожидалось минимум 2 продукта для добавления в избранное, но найдено $buttonsCount"
        }

        favoriteButtons[0].performClick()
        favoriteButtons[1].performClick()

        val unfavNodes = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes()
        assert(unfavNodes.size >= 2) {
            "Ожидалось минимум 2 элемента в избранном, но найдено ${unfavNodes.size}"
        }

        composeTestRule.onNodeWithText("Избранное").performClick()
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodes(hasContentDescription("Удалить из избранного"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        val favListNodes = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes()
        assert(favListNodes.size >= 2) {
            "Ожидалось минимум 2 элемента во вкладке Избранное, но найдено ${favListNodes.size}"
        }

        composeTestRule.activityRule.scenario.recreate()

        composeTestRule.onNodeWithText("База средств").performClick()
        composeTestRule.onNodeWithText("Избранное").performClick()
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodes(hasContentDescription("Удалить из избранного"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        val finalNodes = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes()
        assert(finalNodes.size >= 2) {
            "Избранное не сохранилось после пересоздания Activity, найдено ${finalNodes.size} элементов"
        }

        Espresso.pressBack()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profile").performClick()

        composeTestRule.waitUntil(timeoutMillis = fairies.pixels.curlyLabAndroid.ui.TIMEOUT) {
            composeTestRule.onAllNodesWithText(name).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText(name).assertIsDisplayed()

        deleteUser()
    }
}