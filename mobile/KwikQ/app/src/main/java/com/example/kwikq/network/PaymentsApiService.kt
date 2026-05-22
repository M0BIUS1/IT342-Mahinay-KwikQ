package com.example.kwikq.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentsApiService {
    @POST("api/payments/pay/{paymentId}")
    fun markAsPaid(@Path("paymentId") paymentId: Long): Call<MessageResponse>

    @GET("api/payments/my-payments")
    fun getMyPayments(@Query("page") page: Int = 0, @Query("size") size: Int = 10): Call<PagedResponse<PaymentResponse>>

    @GET("api/payments/pending-amount")
    fun getPendingAmount(): Call<MessageResponse>
}
