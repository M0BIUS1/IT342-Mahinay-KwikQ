package com.example.kwikq.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    @GET("api/books")
    fun getBooks(@Query("page") page: Int = 0, @Query("size") size: Int = 20): Call<PagedResponse<Book>>

    @GET("api/books/{id}")
    fun getBook(@Path("id") id: Long): Call<Book>

    @GET("api/books/categories")
    fun getCategories(): Call<List<String>>
}
