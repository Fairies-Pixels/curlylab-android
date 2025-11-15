package fairies.pixels.curlyLabAndroid.data.remote.api

import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/users")
    suspend fun createUser(@Body request: UserRequest): Map<String, String>

    @GET("/users/{id}")
    suspend fun getUser(@Path("id") userId: String): UserResponse

    @PUT("/users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body request: UserRequest
    )

    @DELETE("/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)

    @GET("/hairtypes")
    suspend fun getAllHairTypes(): List<HairTypeResponse>

    @GET("/hairtypes/{id}")
    suspend fun getHairType(@Path("id") id: String): HairTypeResponse

    @PUT("/hairtypes/{userId}")
    suspend fun updateHairType(
        @Path("userId") userId: String,
        @Body request: HairTypeRequest
    )

    @DELETE("/hairtypes/{userId}")
    suspend fun deleteHairType(@Path("userId") userId: String)
}