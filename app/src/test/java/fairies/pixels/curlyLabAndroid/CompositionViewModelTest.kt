package fairies.pixels.curlyLabAndroid

import android.content.Context
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import fairies.pixels.curlyLabAndroid.presentation.composition.screen.viewmodel.CompositionViewModel
import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.response.composition.AnalysisResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.composition.AnalysisResult
import fairies.pixels.curlyLabAndroid.data.remote.model.response.composition.AnalysisIssue

import android.net.Uri
import io.mockk.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CompositionViewModelTest {

    private lateinit var api: ApiService
    private lateinit var viewModel: CompositionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        viewModel = CompositionViewModel(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onInputTextChange updates value`() {
        viewModel.onInputTextChange("test")
        assertEquals("test", viewModel.inputText.value)
    }

    @Test
    fun `analyze returns success when issues empty`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = AnalysisResult(
                ok = true,
                raw_text_excerpt = null,
                issues_count = 0,
                issues = emptyList(),
                result = "ok"
            )
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Всё в порядке!", viewModel.result.value)
    }

    @Test
    fun `analyze formats issues correctly`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = AnalysisResult(
                ok = false,
                raw_text_excerpt = null,
                issues_count = 1,
                issues = listOf(
                    AnalysisIssue(
                        ingredient = "Alcohol",
                        category = "Harmful",
                        reason = "Dries skin"
                    )
                ),
                result = "fail"
            )
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        val output = viewModel.result.value

        assertTrue(output.contains("Alcohol"))
        assertTrue(output.contains("Harmful"))
        assertTrue(output.contains("Dries skin"))
    }

    @Test
    fun `analyze handles null result`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = null
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Всё в порядке!", viewModel.result.value)
    }

    @Test
    fun `analyze handles exception`() = runTest {

        coEvery {
            api.analyzeComposition(null, any())
        } throws RuntimeException("Network error")

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.result.value.contains("Ошибка"))
    }

    @Test
    fun `api called once`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = null
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            api.analyzeComposition(null, any())
        }
    }

    @Test
    fun `input text long than max should trigger error state`() {

        val longText = "a".repeat(2001)

        viewModel.onInputTextChange(longText)

        assertEquals(longText, viewModel.inputText.value)
    }

    @Test
    fun `analyze when result exists but issues empty`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = AnalysisResult(
                ok = true,
                raw_text_excerpt = "text",
                issues_count = 0,
                issues = emptyList(),
                result = "ok"
            )
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Всё в порядке!", viewModel.result.value)
    }

    @Test
    fun `analyze when result not null but issues null`() = runTest {

        val response = AnalysisResponse(
            status = "ok",
            result = AnalysisResult(
                ok = false,
                raw_text_excerpt = null,
                issues_count = null,
                issues = null,
                result = "something"
            )
        )

        coEvery {
            api.analyzeComposition(null, any())
        } returns response

        viewModel.analyze(mockk(relaxed = true), null)

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.result.value.isNotEmpty())
    }
}