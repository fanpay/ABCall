package com.uniandes.abcall.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.abcall.R
import com.uniandes.abcall.data.model.ChatMessage

class ChatAdapter(private val chatMessages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userMessage: TextView = itemView.findViewById(R.id.userMessage)
        val chatbotMessage: TextView = itemView.findViewById(R.id.chatbotMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatMessages[position]

        if (message.isUserMessage) {
            holder.userMessage.visibility = View.VISIBLE
            holder.userMessage.text = message.message
            holder.chatbotMessage.visibility = View.GONE
        } else {
            holder.chatbotMessage.visibility = View.VISIBLE
            holder.chatbotMessage.text = message.message
            holder.userMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }
}
