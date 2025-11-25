package fairies.pixels.curlyLabAndroid.data.remote.api

import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.FavoriteRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.products.ReviewRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.HairTypeRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.request.profile.UserRequest
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.FavoriteResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ProductResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.products.ReviewResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.HairTypeResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.profile.UserResponse
import fairies.pixels.curlyLabAndroid.data.remote.model.response.composition.AnalysisResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("/products")
    suspend fun getAllProducts(): List<ProductResponse>

    @GET("/products/{id}")
    suspend fun getProduct(@Path("id") id: String): ProductResponse

    @GET("/products/{product_id}/reviews")
    suspend fun getProductReviews(
        @Path("product_id") productId: String
    ): List<ReviewResponse>

    @POST("/products/{product_id}/reviews")
    suspend fun createReview(
        @Path("product_id") productId: String,
        @Body review: ReviewRequest
    ): Response<Unit>

    @PUT("/products/{product_id}/reviews/{review_id}")
    suspend fun updateReview(
        @Path("product_id") productId: String,
        @Path("review_id") reviewId: String,
        @Body review: ReviewRequest
    ): ReviewResponse

    @DELETE("/products/{product_id}/reviews/{review_id}")
    suspend fun deleteReview(
        @Path("product_id") productId: String,
        @Path("review_id") reviewId: String
    ): Response<Unit>

    @GET("/users/{user_id}/favourites")
    suspend fun getUserFavorites(
        @Path("user_id") userId: String
    ): List<FavoriteResponse>?

    @POST("/users/{user_id}/favourites")
    suspend fun addToFavorites(
        @Path("user_id") userId: String,
        @Body favorite: FavoriteRequest
    ): Response<Unit>

    @DELETE("/users/{user_id}/favourites/{product_id}")
    suspend fun removeFromFavorites(
        @Path("user_id") userId: String,
        @Path("product_id") productId: String
    ): Response<Unit>

    @GET("/products/{product_id}/is_favourite/{user_id}")
    suspend fun isProductFavorite(
        @Path("product_id") productId: String,
        @Path("user_id") userId: String
    ): Boolean

    @Multipart
    @POST("/composition/analyze")
    suspend fun analyzeComposition(
        @Part file: MultipartBody.Part?,
        @Part("text") text: RequestBody?
    ): AnalysisResult

}