package fairies.pixels.curlyLabAndroid.domain.repository.profile

import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import okhttp3.MultipartBody
import java.io.File

interface UsersRepository {
    suspend fun createUser(request: UserRequest): String
    suspend fun getUser(id: String): UserResponse
    suspend fun updateUser(id: String, request: UserRequest)
    suspend fun deleteUser(id: String)
    suspend fun uploadUserAvatar(userId: String, file: File, part: MultipartBody.Part): String
    suspend fun deleteUserAvatar(userId: String)
}