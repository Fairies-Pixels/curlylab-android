package fairies.pixels.curlyLabAndroid.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import fairies.pixels.curlyLabAndroid.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UploadAvatarUiTest : BaseAuthTest<MainActivity>() {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>().also { composeTestRule = it }

    @Test
    fun uploadAvatar_avatarFieldIsDisplayed() {
        val email = "t${System.currentTimeMillis()}@ya.ru"
        val password = "123456"
        val name = "test_logout_user"

        registerUser(email = email, name = name, password = password)
        loginUser(email, password)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profile").performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Загрузить фото")
            .assertExists()

        deleteUser()
    }
}