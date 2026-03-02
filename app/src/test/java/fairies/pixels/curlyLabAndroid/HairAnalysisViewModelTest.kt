package fairies.pixels.curlyLabAndroid

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import io.mockk.*
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel.HairAnalysisViewModel
import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.data.remote.model.response.analysis.AnalysisRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoroutinesApi::class)
class HairAnalysisViewModelTest {

    private lateinit var authDataStore: AuthDataStore
    private lateinit var hairRepo: HairTypesRepository
    private lateinit var analysisRepo: AnalysisRepository
    private lateinit var viewModel: HairAnalysisViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        authDataStore = mockk(relaxed = true)
        hairRepo = mockk(relaxed = true)
        analysisRepo = mockk()

        viewModel = HairAnalysisViewModel(
            authDataStore,
            hairRepo,
            analysisRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearMocks(authDataStore, hairRepo, analysisRepo)
    }


    @Test
    fun `analyze success returns porosity and stops loading`() = runTest {

        coEvery {
            analysisRepo.analyzePhoto(any())
        } returns "ВЫСОКАЯ ПОРИСТОСТЬ"

        viewModel.analyze(byteArrayOf(1, 2, 3))

        advanceUntilIdle()

        assertEquals("ВЫСОКАЯ ПОРИСТОСТЬ", viewModel.result.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `analyze failure sets error`() = runTest {

        coEvery {
            analysisRepo.analyzePhoto(any())
        } throws RuntimeException("Network broken")

        viewModel.analyze(byteArrayOf())

        advanceUntilIdle()

        assertEquals("Network broken", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.result.value)
    }


    @Test
    fun `saveResult when user exists saves successfully`() = runTest {

        coEvery { authDataStore.getUserId() } returns "user123"
        viewModel.apply {
            val field = this::class.java.getDeclaredField("_result")
            field.isAccessible = true
            (field.get(this) as MutableStateFlow<String?>).value =
                "ВЫСОКАЯ ПОРИСТОСТЬ"
        }

        coEvery {
            hairRepo.updateHairType(
                any(),
                any()
            )
        } just Runs

        viewModel.saveResult()

        advanceUntilIdle()

        assertTrue(viewModel.saved.value == true)

        coVerify {
            hairRepo.updateHairType(
                eq("user123"),
                match {
                    it.porosity == PorosityTypes.POROUS.dbCode
                }
            )
        }
    }

    @Test
    fun `saveResult without user does nothing`() = runTest {

        coEvery { authDataStore.getUserId() } returns null

        viewModel.saveResult()

        advanceUntilIdle()

        assertNull(viewModel.saved.value)

        coVerify(exactly = 0) {
            hairRepo.updateHairType(any(), any())
        }
    }

    @Test
    fun `saveResult when repository fails sets saved false`() = runTest {

        coEvery { authDataStore.getUserId() } returns "user123"

        viewModel.apply {
            val field = this::class.java.getDeclaredField("_result")
            field.isAccessible = true
            (field.get(this) as MutableStateFlow<String?>).value =
                "НИЗКАЯ ПОРИСТОСТЬ"
        }

        coEvery {
            hairRepo.updateHairType(any(), any())
        } throws RuntimeException("DB error")

        viewModel.saveResult()

        advanceUntilIdle()

        assertEquals(false, viewModel.saved.value)
    }

    
}