package fairies.pixels.curlyLabAndroid.presentation.hairTyping.viewmodel

import fairies.pixels.curlyLabAndroid.data.local.AuthDataStore
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import io.mockk.coEvery
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
class PorosityTextTypingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: HairTypesRepository
    private lateinit var authDataStore: AuthDataStore
    private lateinit var viewModel: PorosityTextTypingViewModel

    @Before
    fun setup() {
        repository = mockk()
        authDataStore = mockk()
        viewModel = PorosityTextTypingViewModel(repository, authDataStore)
    }

    @Test
    fun `answering меняет выбранный ответ`() {
        viewModel.answering(answerId = 1, questionId = 0)

        val question = viewModel.questions.value[0]
        assertTrue(question.answers[1].isSelected)
        assertFalse(question.answers[0].isSelected)
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
    fun `getResult устанавливает POROUS если ответов больше porous`() {
        repeat(6) {
            viewModel.answering(0, it)
        }

        viewModel.getResult()

        assertEquals(
            PorosityTypes.POROUS,
            viewModel.result.value
        )
    }

    @Test
    fun `getResult устанавливает NON_POROUS если выбраны соответствующие ответы`() {
        viewModel.answering(1, 0)
        viewModel.answering(0, 1)
        viewModel.answering(1, 2)
        viewModel.answering(1, 3)
        viewModel.answering(0, 4)
        viewModel.answering(0, 5)

        viewModel.getResult()

        assertEquals(
            PorosityTypes.NON_POROUS,
            viewModel.result.value
        )
    }

    @Test
    fun `saveResult сохраняет результат успешно`() = runTest {
        val userId = "123"

        viewModel.getResult()

        coEvery { authDataStore.getUserId() } returns userId
        coEvery { repository.updateHairType(eq(userId), any()) } returns Unit

        viewModel.saveResult()
        advanceUntilIdle()

        assertEquals(true, viewModel.saved.value)
    }

    @Test
    fun `saveResult устанавливает false если repository кидает исключение`() = runTest {
        val userId = "123"

        viewModel.getResult()

        coEvery { authDataStore.getUserId() } returns userId
        coEvery { repository.updateHairType(any(), any()) } throws RuntimeException()

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