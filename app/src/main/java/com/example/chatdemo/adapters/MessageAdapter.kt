package com.example.chatdemo.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatdemo.R
import com.tencent.imsdk.v2.V2TIMMessage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val messages: List<V2TIMMessage>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TEXT_SENT = 1
        private const val VIEW_TYPE_TEXT_RECEIVED = 2
        private const val VIEW_TYPE_IMAGE_SENT = 3
        private const val VIEW_TYPE_IMAGE_RECEIVED = 4
        private const val VIEW_TYPE_FILE_SENT = 5
        private const val VIEW_TYPE_FILE_RECEIVED = 6
    }

    inner class SentTextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessageText: TextView = itemView.findViewById(R.id.tvMessageText)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            tvMessageText.text = message.textElem?.text ?: ""
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    inner class ReceivedTextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val tvMessageText: TextView = itemView.findViewById(R.id.tvMessageText)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            tvSenderName.text = message.sender ?: "Unknown"
            tvMessageText.text = message.textElem?.text ?: ""
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    inner class SentImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            message.imageElem?.imageList?.firstOrNull()?.let { image ->
                val localPath = image.localUrl
                if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(localPath)
                    ivImage.setImageBitmap(bitmap)
                } else {
                    ivImage.setImageResource(R.drawable.ic_image_placeholder)
                }
            }
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    inner class ReceivedImageMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            tvSenderName.text = message.sender ?: "Unknown"
            message.imageElem?.imageList?.firstOrNull()?.let { image ->
                val localPath = image.localUrl
                if (!localPath.isNullOrEmpty() && File(localPath).exists()) {
                    val bitmap = BitmapFactory.decodeFile(localPath)
                    ivImage.setImageBitmap(bitmap)
                } else {
                    ivImage.setImageResource(R.drawable.ic_image_placeholder)
                }
            }
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    inner class SentFileMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            message.fileElem?.let { fileElem ->
                tvFileName.text = fileElem.fileName ?: "Unknown file"
                tvFileSize.text = formatFileSize(fileElem.fileSize)
            }
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    inner class ReceivedFileMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvFileSize: TextView = itemView.findViewById(R.id.tvFileSize)
        val tvMessageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: V2TIMMessage) {
            tvSenderName.text = message.sender ?: "Unknown"
            message.fileElem?.let { fileElem ->
                tvFileName.text = fileElem.fileName ?: "Unknown file"
                tvFileSize.text = formatFileSize(fileElem.fileSize)
            }
            tvMessageTime.text = formatTimestamp(message.timestamp)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val isSent = message.isSelf

        return when (message.elemType) {
            V2TIMMessage.V2TIM_ELEM_TYPE_TEXT -> {
                if (isSent) VIEW_TYPE_TEXT_SENT else VIEW_TYPE_TEXT_RECEIVED
            }
            V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE -> {
                if (isSent) VIEW_TYPE_IMAGE_SENT else VIEW_TYPE_IMAGE_RECEIVED
            }
            V2TIMMessage.V2TIM_ELEM_TYPE_FILE -> {
                if (isSent) VIEW_TYPE_FILE_SENT else VIEW_TYPE_FILE_RECEIVED
            }
            else -> {
                // Default to text message for unknown types
                if (isSent) VIEW_TYPE_TEXT_SENT else VIEW_TYPE_TEXT_RECEIVED
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TEXT_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentTextMessageViewHolder(view)
            }
            VIEW_TYPE_TEXT_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedTextMessageViewHolder(view)
            }
            VIEW_TYPE_IMAGE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_image_sent, parent, false)
                SentImageMessageViewHolder(view)
            }
            VIEW_TYPE_IMAGE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_image_received, parent, false)
                ReceivedImageMessageViewHolder(view)
            }
            VIEW_TYPE_FILE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_file_sent, parent, false)
                SentFileMessageViewHolder(view)
            }
            VIEW_TYPE_FILE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_file_received, parent, false)
                ReceivedFileMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentTextMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentTextMessageViewHolder -> holder.bind(message)
            is ReceivedTextMessageViewHolder -> holder.bind(message)
            is SentImageMessageViewHolder -> holder.bind(message)
            is ReceivedImageMessageViewHolder -> holder.bind(message)
            is SentFileMessageViewHolder -> holder.bind(message)
            is ReceivedFileMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format("%.1f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.1f MB", mb)
        val gb = mb / 1024.0
        return String.format("%.1f GB", gb)
    }
}
