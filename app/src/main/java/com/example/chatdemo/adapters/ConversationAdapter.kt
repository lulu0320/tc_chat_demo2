package com.example.chatdemo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatdemo.R
import com.tencent.imsdk.v2.V2TIMConversation
import java.text.SimpleDateFormat
import java.util.*

class ConversationAdapter(
    private val conversations: List<V2TIMConversation>,
    private val onConversationClick: (V2TIMConversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvConversationName: TextView = itemView.findViewById(R.id.tvConversationName)
        val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)

        fun bind(conversation: V2TIMConversation) {
            tvConversationName.text = conversation.showName ?: conversation.userID
            tvLastMessage.text = conversation.lastMessage?.textElem?.text ?: "No messages yet"

            conversation.lastMessage?.timestamp?.let { timestamp ->
                val date = Date(timestamp * 1000)
                val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                tvTimestamp.text = format.format(date)
            }

            itemView.setOnClickListener {
                onConversationClick(conversation)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size
}
