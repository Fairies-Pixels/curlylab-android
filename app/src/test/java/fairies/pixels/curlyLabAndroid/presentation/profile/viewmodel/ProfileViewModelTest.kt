package fairies.pixels.curlyLabAndroid.presentation.profile.viewmodel

import android.app.Application
import app.cash.turbine.test
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.domain.repository.auth.AuthRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import fairies.pixels.curlyLabAndroid.presentation.products.viewmodel.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val usersRepository: UsersRepository = mockk()
    private val hairTypesRepository: HairTypesRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val authDataStore: AuthDataStore = mockk()
    private val application: Application = mockk(relaxed = true)

    private lateinit var viewModel: ProfileViewModel

    private val testUserId = UUID.randomUUID().toString()

    @Before
    fun setUp() = runTest {
        coEvery { authDataStore.getUserId() } returns testUserId
        coEvery { usersRepository.getUser(any()) } returns mockk {
            every { username } returns "TestUser"
            every { imageUrl } returns "test.jpg"
        }
        coEvery { hairTypesRepository.getHairType(any()) } returns mockk()

        viewModel = ProfileViewModel(
            usersRepository,
            hairTypesRepository,
            authRepository,
            authDataStore,
            hairTypesRepository,
            application
        )

        advanceUntilIdle()
    }

    @Test
    fun `при инициализации загружается профиль`() = runTest {
        assertEquals("TestUser", viewModel.userName.value)
        assertEquals("test.jpg", viewModel.imageUrl.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `если пользователь не авторизован показывается ошибка`() = runTest {
        coEvery { authDataStore.getUserId() } returns null

        viewModel = ProfileViewModel(
            usersRepository,
            hairTypesRepository,
            authRepository,
            authDataStore,
            hairTypesRepository,
            application
        )

        advanceUntilIdle()

        assertEquals("Пользователь не авторизован", viewModel.error.value)
    }

    @Test
    fun `успешный logout обновляет состояние`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        advanceUntilIdle()

        assertTrue(viewModel.logoutState.value!!.isSuccess)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `ошибка logout устанавливает ошибку`() = runTest {
        coEvery { authRepository.logout() } throws Exception("Fail")

        viewModel.logout()
        advanceUntilIdle()

        assertTrue(viewModel.logoutState.value!!.isFailure)
        assertTrue(viewModel.error.value!!.contains("Fail"))
    }

    @Test
    fun `успешное удаление аккаунта`() = runTest {
        coEvery { authRepository.deleteAccount(any()) } returns Unit

        viewModel.deleteAccount()
        advanceUntilIdle()

        assertTrue(viewModel.deleteState.value!!.isSuccess)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `ошибка удаления аккаунта`() = runTest {
        coEvery { authRepository.deleteAccount(any()) } throws Exception("Delete error")

        viewModel.deleteAccount()
        advanceUntilIdle()

        assertTrue(viewModel.deleteState.value!!.isFailure)
        assertTrue(viewModel.error.value!!.contains("Delete error"))
    }

    @Test
    fun `resetStates очищает logout и delete состояния`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.logout()
        advanceUntilIdle()

        viewModel.resetStates()

        assertNull(viewModel.logoutState.value)
        assertNull(viewModel.deleteState.value)
    }

    @Test
    fun `успешное удаление аватара`() = runTest {
        coEvery { usersRepository.deleteUserAvatar(any()) } returns Unit

        viewModel.deleteAvatar()
        advanceUntilIdle()

        assertNull(viewModel.imageUrl.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `ошибка удаления аватара`() = runTest {
        coEvery { usersRepository.deleteUserAvatar(any()) } throws Exception("Avatar error")

        viewModel.deleteAvatar()
        advanceUntilIdle()

        assertTrue(viewModel.error.value!!.contains("Avatar error"))
    }

    @Test
    fun `успешное сохранение типа волос`() = runTest {
        coEvery { hairTypesRepository.updateHairType(any(), any()) } returns Unit

        viewModel.saveManualHairType("Пористость", "Низкая")
        advanceUntilIdle()

        assertEquals(true, viewModel.saved.value)
    }

    @Test
    fun `ошибка сохранения типа волос`() = runTest {
        coEvery { hairTypesRepository.updateHairType(any(), any()) } throws Exception()

        viewModel.saveManualHairType("Пористость", "Низкая")
        advanceUntilIdle()

        assertEquals(false, viewModel.saved.value)
    }

    @Test
    fun `сохранение при отсутствии userId не меняет saved`() = runTest {
        coEvery { authDataStore.getUserId() } returns null

        viewModel.saveManualHairType("Пористость", "Низкая")
        advanceUntilIdle()

        assertNull(viewModel.saved.value)
    }

    @Test
    fun `isLoading корректно переключается при logout`() = runTest {
        coEvery { authRepository.logout() } returns Unit

        viewModel.isLoading.test {

            viewModel.logout()

            assertEquals(false, awaitItem())

            assertEquals(true, awaitItem())

            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}