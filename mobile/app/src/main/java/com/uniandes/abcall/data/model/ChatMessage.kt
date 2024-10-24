package com.uniandes.abcall.data.model

data class ChatMessage(
    val message: String,
    val incidentId: Int? = null,
    val isUserMessage: Boolean // true si el mensaje es del usuario, false si es del chatbot
)