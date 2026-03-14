package fairies.pixels.curlyLabAndroid

import androidx.test.core.app.ApplicationProvider
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import fairies.pixels.curlyLabAndroid.presentation.profile.viewmodel.ProfileViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelIntegrationTest {

    private lateinit var usersRepository: UsersRepository
    private lateinit var hairTypesRepository: HairTypesRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var authDataStore: AuthDataStore

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        usersRepository = mockk()
        hairTypesRepository = mockk()
        authRepository = mockk()
        authDataStore = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun profile_data_loads_after_authorization() = runTest {

        val userId = "user123"

        val user = UserResponse(
            id = userId,
            username = "Alice",
            imageUrl = "https://image.com/avatar.jpg",
            createdAt = LocalDateTime.now()
        )

        val hairType = HairTypeResponse(
            porosity = "HIGH",
            thickness = "THICK",
            isColored = true
        )

        coEvery { authDataStore.getUserId() } returns userId
        coEvery { usersRepository.getUser(userId) } returns user
        coEvery { hairTypesRepository.getHairType(userId) } returns hairType

        val application = ApplicationProvider.getApplicationContext<CurlyLabApplication>()

        viewModel = ProfileViewModel(
            usersRepository,
            hairTypesRepository,
            authRepository,
            authDataStore,
            hairTypesRepository,
            application
        )

        viewModel.loadProfileData()

        advanceUntilIdle()

        assertEquals("Alice", viewModel.userName.value)
    }


}