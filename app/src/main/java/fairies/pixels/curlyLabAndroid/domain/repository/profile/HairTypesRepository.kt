package fairies.pixels.curlyLabAndroid.domain.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse

interface HairTypesRepository {
    suspend fun getAllHairTypes(): List<HairTypeResponse>
    suspend fun getHairType(id: String): HairTypeResponse
    suspend fun updateHairType(userId: String, request: HairTypeRequest)
    suspend fun deleteHairType(userId: String)
}