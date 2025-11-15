package fairies.pixels.curlyLabAndroid.data.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.api.ApiService
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.domain.repository.profile.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) :
    UsersRepository {
    override suspend fun getUser(id: String) = apiService.getUser(id)
    override suspend fun updateUser(id: String, request: UserRequest) = apiService.updateUser(id, request)
    override suspend fun deleteUser(id: String) = apiService.deleteUser(id)
    override suspend fun createUser(request: UserRequest): String =
        apiService.createUser(request)["id"] ?: throw IllegalStateException("No ID returned")
}