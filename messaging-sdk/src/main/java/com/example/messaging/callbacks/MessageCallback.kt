package com.example.messaging.callbacks

interface MessageCallback {
    fun onSuccess(messageId: String?)
    fun onError(code: Int, message: String?)
    fun onProgress(progress: Int) {}
}
