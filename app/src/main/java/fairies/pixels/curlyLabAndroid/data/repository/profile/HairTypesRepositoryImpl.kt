package fairies.pixels.curlyLabAndroid.data.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.domain.repository.profile.HairTypesRepository
import javax.inject.Inject

class HairTypesRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : HairTypesRepository {

    override suspend fun getAllHairTypes(): List<HairTypeResponse> {
        return apiService.getAllHairTypes()
    }

    override suspend fun getHairType(id: String): HairTypeResponse {
        return apiService.getHairType(id)
    }

    override suspend fun updateHairType(userId: String, request: HairTypeRequest) {
        apiService.updateHairType(userId, request)
    }

    override suspend fun deleteHairType(userId: String) {
        apiService.deleteHairType(userId)
    }
}