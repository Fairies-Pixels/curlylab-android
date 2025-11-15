package fairies.pixels.curlyLabAndroid.domain.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse

interface UsersRepository {
    suspend fun createUser(request: UserRequest): String
    suspend fun getUser(id: String): UserResponse
    suspend fun updateUser(id: String, request: UserRequest)
    suspend fun deleteUser(id: String)
}