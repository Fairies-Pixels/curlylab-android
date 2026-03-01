package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel

import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ThicknessTextTypingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: HairTypesRepository = mockk(relaxed = true)
    private val authDataStore: AuthDataStore = mockk(relaxed = true)

    private lateinit var viewModel: ThicknessTextTypingViewModel

    @Before
    fun setUp() {
        viewModel = ThicknessTextTypingViewModel(repository, authDataStore)
    }

    @Test
    fun `answering меняет выбранный ответс`() {
        val questionId = 0
        val answerId = 2

        viewModel.answering(answerId, questionId)

        val updatedQuestion = viewModel.questions.value[questionId]

        updatedQuestion.answers.forEachIndexed { index, answer ->
            if (index == answerId) {
                assertTrue(answer.isSelected)
            } else {
                assertFalse(answer.isSelected)
            }
        }
    }

    @Test
    fun `nextQuestion увеличивает индекс`() {
        viewModel.nextQuestion()
        assertEquals(1, viewModel.currentQuestionId.value)
    }

    @Test
    fun `previousQuestion уменьшает индекс`() {
        viewModel.nextQuestion()
        viewModel.previousQuestion()

        assertEquals(0, viewModel.currentQuestionId.value)
    }

    @Test
    fun `getResult устанавливает BOLD если ответов больше bold`() {
        viewModel.answering(0, 0)
        viewModel.answering(0, 1)

        viewModel.getResult()

        assertEquals(ThicknessTypes.BOLD, viewModel.result.value)
    }

    @Test
    fun `getResult устанавливает THIN если ответов больше thin`() {
        viewModel.answering(2, 0)
        viewModel.answering(1, 1)

        viewModel.getResult()

        assertEquals(ThicknessTypes.THIN, viewModel.result.value)
    }

    @Test
    fun `getResult устанавливает MEDIUM если ответов больше medium`() {
        viewModel.answering(1, 0)
        viewModel.answering(2, 1)

        viewModel.getResult()

        assertEquals(ThicknessTypes.MEDIUM, viewModel.result.value)
    }

    @Test
    fun `saveResult сохраняет результат успешно`() = runTest {
        coEvery { authDataStore.getUserId() } returns "userId"

        viewModel.answering(0, 0)
        viewModel.answering(0, 1)
        viewModel.getResult()

        coEvery {
            repository.updateHairType(any(), any())
        } just Runs

        viewModel.saveResult()
        advanceUntilIdle()

        assertEquals(true, viewModel.saved.value)
    }

    @Test
    fun `saveResult устанавливает false если repository кидает исключение`() = runTest {
        coEvery { authDataStore.getUserId() } returns "userId"

        viewModel.answering(0, 0)
        viewModel.answering(0, 1)
        viewModel.getResult()

        coEvery {
            repository.updateHairType(any(), any())
        } throws RuntimeException()

        viewModel.saveResult()
        advanceUntilIdle()

        assertEquals(false, viewModel.saved.value)
    }

    @Test
    fun `saveResult устанавливает false если getUserId кидает исключение`() = runTest {
        coEvery { authDataStore.getUserId() } throws RuntimeException()

        viewModel.saveResult()
        advanceUntilIdle()

        assertEquals(false, viewModel.saved.value)
    }

    @Test
    fun `saveResult ничего не делает если userId null`() = runTest {
        coEvery { authDataStore.getUserId() } returns null

        viewModel.saveResult()
        advanceUntilIdle()

        assertNull(viewModel.saved.value)
    }
}