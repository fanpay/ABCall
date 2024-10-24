package com.uniandes.abcall.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.uniandes.abcall.R
import com.uniandes.abcall.data.model.ChatMessage
import com.uniandes.abcall.data.repository.AuthRepository
import com.uniandes.abcall.data.service.RetrofitBroker
import com.uniandes.abcall.view.adapters.ChatAdapter
import com.uniandes.abcall.viewmodel.UserViewModel
import com.uniandes.abcall.viewmodel.UserViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private lateinit var userViewModel: UserViewModel
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getString("USER_ID")
        Log.e("ChatFragment", "UserId recibido: $userId")

        // Configurar el RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val editTextMessage = view.findViewById<EditText>(R.id.editTextMessage)
        val buttonSend = view.findViewById<Button>(R.id.buttonSend)

        val authRepository = AuthRepository(requireActivity().application)
        val factory = UserViewModelFactory(requireActivity().application, authRepository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        sendInitialMessage(view)

        buttonSend.setOnClickListener {
            val userMessage = editTextMessage.text.toString()

            if (userMessage.isNotEmpty()) {
                // Añadir el mensaje del usuario a la lista y refrescar el RecyclerView
                chatMessages.add(ChatMessage(message = userMessage, isUserMessage = true))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)

                // Limpiar el EditText
                editTextMessage.text.clear()

                // Observar los cambios en el token
                userViewModel.getUserToken(userId!!).observe(viewLifecycleOwner) { token ->
                    token?.let {
                        sendMessageToChatbot(userMessage, it, view)
                    } ?: run {
                        Snackbar.make(view, "Error: Token no disponible", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun sendInitialMessage(view: View) {
        val currentLanguage = Locale.getDefault().language

        val initialMessage = when (currentLanguage) {
            "es" -> "hola"
            "en" -> "hello"
            else -> "hola"
        }

        userViewModel.getUserToken(userId!!).observe(viewLifecycleOwner) { token ->
            token?.let {
                sendMessageToChatbot(initialMessage, it, view)
            } ?: run {
                Snackbar.make(view, "Error: Token no disponible", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun sendMessageToChatbot(message: String, token: String, view: View) {
        val currentLanguage = Locale.getDefault().language

        CoroutineScope(Dispatchers.IO).launch {
            RetrofitBroker.sendMessageToChatbot(
                token = token,
                message = message,
                lang=currentLanguage,
                onComplete = { chatResponse ->

                    val incidentId = chatResponse.incidentId

                    // Actualizar el RecyclerView con la respuesta del chatbot
                    requireActivity().runOnUiThread {
                        chatMessages.add(ChatMessage(message = chatResponse.message, isUserMessage = false))
                        chatAdapter.notifyItemInserted(chatMessages.size - 1)
                        recyclerView.scrollToPosition(chatMessages.size - 1)

                        if (incidentId != null) {
                            val formattedMessage = getString(R.string.incident_created_msg, incidentId)

                            Snackbar.make(view, formattedMessage, Snackbar.LENGTH_LONG).show()
                        }
                    }
                },
                onError = { error ->
                    requireActivity().runOnUiThread {
                        Snackbar.make(view, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

}
