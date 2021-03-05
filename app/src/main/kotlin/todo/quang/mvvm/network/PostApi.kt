package todo.quang.mvvm.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import todo.quang.mvvm.network.model.AppInfoData

interface PostApi {
    @GET("api")
    suspend fun getGenre(@Query("id") id: String): Response<AppInfoData?>?
}