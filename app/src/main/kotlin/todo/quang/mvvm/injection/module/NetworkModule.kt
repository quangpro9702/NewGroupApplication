package todo.quang.mvvm.injection.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import todo.quang.mvvm.BuildConfig
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.utils.BASE_URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about network
 */
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        return OkHttpClient.Builder()
                .connectTimeout(6L, TimeUnit.SECONDS)
                .readTimeout(6L, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    @Provides
    @Singleton
    fun providesRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun providesJsonApi(retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }
}