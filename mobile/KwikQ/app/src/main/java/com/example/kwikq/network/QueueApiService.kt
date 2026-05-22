package com.example.kwikq.network

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QueueApiService {
    @POST("api/queues/add/{bookId}")
    fun addToQueue(@Path("bookId") bookId: Long): Call<MessageResponse>

    @DELETE("api/queues/{queueId}")
    fun removeFromQueue(@Path("queueId") queueId: Long): Call<MessageResponse>

    @GET("api/queues/my-queues")
    fun getMyQueues(@Query("page") page: Int = 0, @Query("size") size: Int = 50): Call<PagedResponse<QueueItem>>

    @GET("api/queues/book/{bookId}")
    fun getBookQueue(@Path("bookId") bookId: Long): Call<List<QueueItem>>

    @GET("api/queues/position/{bookId}")
    fun getQueuePosition(@Path("bookId") bookId: Long): Call<MessageResponse>

    // Admin endpoints
    @GET("api/queues/admin/book/{bookId}")
    fun adminGetBookQueue(@Path("bookId") bookId: Long): Call<List<QueueItem>>

    @DELETE("api/queues/admin/{queueId}")
    fun adminRemoveFromQueue(@Path("queueId") queueId: Long): Call<MessageResponse>
}
