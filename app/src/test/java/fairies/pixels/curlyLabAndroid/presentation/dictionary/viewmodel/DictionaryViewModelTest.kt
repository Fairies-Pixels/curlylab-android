package fairies.pixels.curlyLabAndroid.presentation.dictionary.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModelTest {

    private lateinit var viewModel: DictionaryViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        viewModel = DictionaryViewModel()
    }

    @Test
    fun `проверка инициализации слов`() = testScope.runTest {
        val words = viewModel.words.first()
        assertEquals(9, words.size)
        assertEquals("Афропик", words[0].name)
        assertEquals("Squish to condish", words.last().name)
    }

    @Test
    fun `фильтрация возвращает правильные совпадения`() = testScope.runTest {
        viewModel.filterWords("LOC")
        val filtered = viewModel.words.first()
        assertEquals(1, filtered.size)
        assertEquals("LOC", filtered[0].name)
    }

    @Test
    fun `фильтрация не чувствительна к регистру`() = testScope.runTest {
        viewModel.filterWords("loc")
        val filtered = viewModel.words.first()
        assertEquals(1, filtered.size)
        assertEquals("LOC", filtered[0].name)
    }

    @Test
    fun `фильтрация возвращает несколько совпадений`() = testScope.runTest {
        viewModel.filterWords("L")
        val filtered = viewModel.words.first()
        assertEquals(2, filtered.size)
        val names = filtered.map { it.name }
        assert(names.containsAll(listOf("LOC", "LOG")))
    }

    @Test
    fun `пустой запрос сбрасывает фильтр`() = testScope.runTest {
        viewModel.filterWords("LOC")
        viewModel.filterWords("")
        val words = viewModel.words.first()
        assertEquals(9, words.size)
    }

    @Test
    fun `запрос без совпадений возвращает пустой список`() = testScope.runTest {
        viewModel.filterWords("абракадабра")
        val filtered = viewModel.words.first()
        assertEquals(0, filtered.size)
    }
}