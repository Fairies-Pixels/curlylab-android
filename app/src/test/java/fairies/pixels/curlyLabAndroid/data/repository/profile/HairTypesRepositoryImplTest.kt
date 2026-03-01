package fairies.pixels.curlyLabAndroid.data.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.PorosityTypes
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.ThicknessTypes
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HairTypesRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: HairTypesRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = HairTypesRepositoryImpl(apiService)
    }

    @Test
    fun `getAllHairTypes возвращает список типов волос`() = runTest {
        val response = listOf(
            HairTypeResponse(
                porosity = PorosityTypes.SEMI_POROUS.dbCode,
                isColored = false,
                thickness = ThicknessTypes.BOLD.dbCode
            ),
            HairTypeResponse(
                porosity = PorosityTypes.POROUS.dbCode,
                isColored = true,
                thickness = ThicknessTypes.THIN.dbCode
            )
        )

        coEvery { apiService.getAllHairTypes() } returns response

        val result = repository.getAllHairTypes()

        assertEquals(response, result)
        coVerify(exactly = 1) { apiService.getAllHairTypes() }
    }

    @Test
    fun `getHairType возвращает конкретный тип волос`() = runTest {
        val id = "1"

        val response = HairTypeResponse(
            porosity = PorosityTypes.SEMI_POROUS.dbCode,
            isColored = false,
            thickness = ThicknessTypes.BOLD.dbCode
        )

        coEvery { apiService.getHairType(id) } returns response

        val result = repository.getHairType(id)

        assertEquals(response, result)
        coVerify(exactly = 1) { apiService.getHairType(id) }
    }

    @Test
    fun `updateHairType вызывает apiService`() = runTest {
        val userId = "123"
        val request = HairTypeRequest(
            userId = userId,
            porosity = PorosityTypes.SEMI_POROUS.dbCode,
            isColored = false,
            thickness = ThicknessTypes.BOLD.dbCode
        )

        coEvery { apiService.updateHairType(userId, request) } returns Unit

        repository.updateHairType(userId, request)

        coVerify(exactly = 1) {
            apiService.updateHairType(userId, request)
        }
    }

    @Test
    fun `deleteHairType вызывает apiService`() = runTest {
        val userId = "123"

        coEvery { apiService.deleteHairType(userId) } returns Unit

        repository.deleteHairType(userId)

        coVerify(exactly = 1) {
            apiService.deleteHairType(userId)
        }
    }
}