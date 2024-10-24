package com.uniandes.abcall.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.abcall.R
import com.uniandes.abcall.data.model.ChatMessage
import com.uniandes.abcall.view.adapters.ChatAdapter


class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val editTextMessage = view.findViewById<EditText>(R.id.editTextMessage)
        val buttonSend = view.findViewById<Button>(R.id.buttonSend)

        buttonSend.setOnClickListener {
            val userMessage = editTextMessage.text.toString()

            if (userMessage.isNotEmpty()) {
                // Añadir el mensaje del usuario a la lista y refrescar el RecyclerView
                chatMessages.add(ChatMessage(userMessage, true))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)

                // Limpiar el EditText
                editTextMessage.text.clear()

                // Enviar el mensaje al chatbot y manejar la respuesta
                sendMessageToChatbot(userMessage)
            }
        }
    }

    private fun sendMessageToChatbot(message: String) {
        // Aquí iría la llamada al servicio del chatbot para obtener una respuesta
        // Simulación de una respuesta
        val chatbotResponse = "Esta es una respuesta del chatbot a: $message"

        // Actualizar el RecyclerView con la respuesta del chatbot
        requireActivity().runOnUiThread {
            chatMessages.add(ChatMessage(chatbotResponse, false))
            chatAdapter.notifyItemInserted(chatMessages.size - 1)
            recyclerView.scrollToPosition(chatMessages.size - 1)
        }
    }
}
