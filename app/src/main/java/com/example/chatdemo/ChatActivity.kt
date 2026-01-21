package com.example.chatdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatdemo.adapters.MessageAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.tencent.imsdk.v2.*

class ChatActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var btnAttachImage: ImageButton
    private lateinit var btnAttachFile: ImageButton

    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<V2TIMMessage>()

    private var targetUserId: String? = null
    private var currentUserId: String? = null

    // Permission launchers
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    // Image picker launcher
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                sendImageMessage(uri)
            }
        }
    }

    // File picker launcher
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                sendFileMessage(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        targetUserId = intent.getStringExtra("USER_ID")
        currentUserId = V2TIMManager.getInstance().loginUser

        if (targetUserId == null) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        loadMessageHistory()
        setupMessageListener()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerViewMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        btnAttachImage = findViewById(R.id.btnAttachImage)
        btnAttachFile = findViewById(R.id.btnAttachFile)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = targetUserId
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(messages, currentUserId ?: "")
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = messageAdapter
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isEmpty()) {
                return@setOnClickListener
            }

            sendMessage(messageText)
        }

        btnAttachImage.setOnClickListener {
            checkPermissionAndPickImage()
        }

        btnAttachFile.setOnClickListener {
            checkPermissionAndPickFile()
        }
    }

    private fun checkPermissionAndPickImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun checkPermissionAndPickFile() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openFilePicker()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        pickFileLauncher.launch(intent)
    }

    private fun sendMessage(text: String) {
        targetUserId?.let { userId ->
            val message = V2TIMManager.getMessageManager().createTextMessage(text)

            V2TIMManager.getMessageManager().sendMessage(
                message,
                userId,
                null,
                V2TIMMessage.V2TIM_PRIORITY_NORMAL,
                false,
                null,
                object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onProgress(progress: Int) {
                        // Not needed for text messages
                    }

                    override fun onSuccess(msg: V2TIMMessage?) {
                        runOnUiThread {
                            etMessage.text.clear()
                            msg?.let {
                                messages.add(it)
                                messageAdapter.notifyItemInserted(messages.size - 1)
                                recyclerView.scrollToPosition(messages.size - 1)
                            }
                        }
                    }

                    override fun onError(code: Int, desc: String?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ChatActivity,
                                getString(R.string.message_sent_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }

    private fun sendImageMessage(uri: Uri) {
        targetUserId?.let { userId ->
            // Get real file path from URI
            val filePath = getRealPathFromURI(uri) ?: return

            val message = V2TIMManager.getMessageManager().createImageMessage(filePath)

            V2TIMManager.getMessageManager().sendMessage(
                message,
                userId,
                null,
                V2TIMMessage.V2TIM_PRIORITY_NORMAL,
                false,
                null,
                object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onProgress(progress: Int) {
                        // Can show upload progress if needed
                    }

                    override fun onSuccess(msg: V2TIMMessage?) {
                        runOnUiThread {
                            msg?.let {
                                messages.add(it)
                                messageAdapter.notifyItemInserted(messages.size - 1)
                                recyclerView.scrollToPosition(messages.size - 1)
                            }
                        }
                    }

                    override fun onError(code: Int, desc: String?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ChatActivity,
                                getString(R.string.image_sent_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }

    private fun sendFileMessage(uri: Uri) {
        targetUserId?.let { userId ->
            // Get real file path from URI
            val filePath = getRealPathFromURI(uri) ?: return

            val message = V2TIMManager.getMessageManager().createFileMessage(filePath, "")

            V2TIMManager.getMessageManager().sendMessage(
                message,
                userId,
                null,
                V2TIMMessage.V2TIM_PRIORITY_NORMAL,
                false,
                null,
                object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onProgress(progress: Int) {
                        // Can show upload progress if needed
                    }

                    override fun onSuccess(msg: V2TIMMessage?) {
                        runOnUiThread {
                            msg?.let {
                                messages.add(it)
                                messageAdapter.notifyItemInserted(messages.size - 1)
                                recyclerView.scrollToPosition(messages.size - 1)
                            }
                        }
                    }

                    override fun onError(code: Int, desc: String?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ChatActivity,
                                getString(R.string.file_sent_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var result: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                if (columnIndex != -1) {
                    result = it.getString(columnIndex)
                }
            }
        }
        // If we couldn't get the path, try using the URI directly
        if (result == null) {
            result = uri.path
        }
        return result
    }

    private fun loadMessageHistory() {
        targetUserId?.let { userId ->
            V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                userId,
                20,
                null,
                object : V2TIMValueCallback<List<V2TIMMessage>> {
                    override fun onSuccess(messageList: List<V2TIMMessage>?) {
                        runOnUiThread {
                            messageList?.let {
                                messages.clear()
                                messages.addAll(it.reversed())
                                messageAdapter.notifyDataSetChanged()
                                if (messages.isNotEmpty()) {
                                    recyclerView.scrollToPosition(messages.size - 1)
                                }
                            }
                        }
                    }

                    override fun onError(code: Int, desc: String?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ChatActivity,
                                "Failed to load messages: $desc",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }

    private fun setupMessageListener() {
        V2TIMManager.getInstance().addSimpleMsgListener(object : V2TIMSimpleMsgListener() {
            override fun onRecvC2CTextMessage(msgID: String?, sender: V2TIMUserInfo?, text: String?) {
                if (sender?.userID == targetUserId) {
                    runOnUiThread {
                        // Reload messages to get the proper V2TIMMessage object
                        loadMessageHistory()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        V2TIMManager.getInstance().removeSimpleMsgListener(null)
    }
}
