package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

        val favoriteNodesCount = favoriteButtons.fetchSemanticsNodes().size
        assert(favoriteNodesCount >= 2) { "Ожидалось как минимум 2 продукта, найдено $favoriteNodesCount" }

        favoriteButtons[0].performClick()
        favoriteButtons[1].performClick()

        val updatedFavorites = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes().size
        assert(updatedFavorites >= 2) { "Ожидалось 2 добавленных в избранное, найдено $updatedFavorites" }

        composeTestRule.onNodeWithText("Избранное").performClick()
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodes(hasContentDescription("Удалить из избранного"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        val favoriteTabCount = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes().size
        assert(favoriteTabCount >= 2) { "Ожидалось 2 продукта в избранном, найдено $favoriteTabCount" }

        composeTestRule.activityRule.scenario.recreate()

        composeTestRule.onNodeWithText("База средств").performClick()
        composeTestRule.onNodeWithText("Избранное").performClick()
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT) {
            composeTestRule.onAllNodes(hasContentDescription("Удалить из избранного"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        val favoriteAfterRestart = composeTestRule
            .onAllNodes(hasContentDescription("Удалить из избранного"))
            .fetchSemanticsNodes().size
        assert(favoriteAfterRestart >= 2) { "Ожидалось 2 продукта после перезапуска, найдено $favoriteAfterRestart" }

        deleteUser()
    }
}