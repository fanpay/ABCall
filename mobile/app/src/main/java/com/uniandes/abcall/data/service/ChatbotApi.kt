package com.uniandes.abcall.data.service

import com.uniandes.abcall.data.model.ChatMessage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


data class ChatRequest(
    val originType: String,
    val message: String
)

interface ChatbotApi {
    @POST("chat")
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): Response<ChatMessage>
}