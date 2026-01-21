package com.example.chatdemo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatdemo.adapters.ConversationAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMConversationListener
import com.tencent.imsdk.v2.V2TIMConversationResult
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMValueCallback

class ConversationListActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etTargetUserId: TextInputEditText
    private lateinit var btnStartChat: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyView: TextView
    private lateinit var conversationAdapter: ConversationAdapter

    private val conversations = mutableListOf<V2TIMConversation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        loadConversations()
        setupConversationListener()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etTargetUserId = findViewById(R.id.etTargetUserId)
        btnStartChat = findViewById(R.id.btnStartChat)
        recyclerView = findViewById(R.id.recyclerViewConversations)
        tvEmptyView = findViewById(R.id.tvEmptyView)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.conversations)
    }

    private fun setupRecyclerView() {
        conversationAdapter = ConversationAdapter(conversations) { conversation ->
            openChat(conversation.userID)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = conversationAdapter
    }

    private fun setupListeners() {
        btnStartChat.setOnClickListener {
            val targetUserId = etTargetUserId.text.toString().trim()
            if (targetUserId.isEmpty()) {
                Toast.makeText(this, "Please enter a user ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            openChat(targetUserId)
        }
    }

    private fun loadConversations() {
        V2TIMManager.getConversationManager().getConversationList(
            0,
            100,
            object : V2TIMValueCallback<V2TIMConversationResult> {
                override fun onSuccess(result: V2TIMConversationResult?) {
                    runOnUiThread {
                        result?.conversationList?.let { list ->
                            conversations.clear()
                            conversations.addAll(list)
                            conversationAdapter.notifyDataSetChanged()
                            updateEmptyView()
                        }
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ConversationListActivity,
                            "Failed to load conversations: $desc",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    private fun setupConversationListener() {
        V2TIMManager.getConversationManager().addConversationListener(object :
            V2TIMConversationListener() {
            override fun onNewConversation(conversationList: MutableList<V2TIMConversation>?) {
                runOnUiThread {
                    conversationList?.let {
                        conversations.addAll(0, it)
                        conversationAdapter.notifyDataSetChanged()
                        updateEmptyView()
                    }
                }
            }

            override fun onConversationChanged(conversationList: MutableList<V2TIMConversation>?) {
                runOnUiThread {
                    loadConversations()
                }
            }
        })
    }

    private fun updateEmptyView() {
        if (conversations.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvEmptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvEmptyView.visibility = View.GONE
        }
    }

    private fun openChat(userId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conversations, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        V2TIMManager.getInstance().logout(object : com.tencent.imsdk.v2.V2TIMCallback {
            override fun onSuccess() {
                runOnUiThread {
                    val intent = Intent(this@ConversationListActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            override fun onError(code: Int, desc: String?) {
                runOnUiThread {
                    Toast.makeText(
                        this@ConversationListActivity,
                        "Logout failed: $desc",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadConversations()
    }
}
