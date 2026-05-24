package com.example.kwikq.network

import com.example.kwikq.network.models.Borrowing
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BorrowingApiService {
    @POST("api/borrowings/borrow/{bookCopyId}")
    fun borrowBook(@Path("bookCopyId") bookCopyId: Long): Call<MessageResponse>

    @POST("api/borrowings/return/{borrowingId}")
    fun returnBook(@Path("borrowingId") borrowingId: Long): Call<MessageResponse>

    @GET("api/borrowings/active")
    fun getActiveBorrowings(@Query("page") page: Int = 0, @Query("size") size: Int = 20): Call<PagedResponse<Borrowing>>

    @GET("api/borrowings/history")
    fun getBorrowingHistory(@Query("page") page: Int = 0, @Query("size") size: Int = 20): Call<PagedResponse<Borrowing>>
}
