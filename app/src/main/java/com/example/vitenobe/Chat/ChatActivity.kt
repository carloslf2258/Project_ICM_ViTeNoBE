package com.example.vitenobe.Chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vitenobe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mChatAdapter: RecyclerView.Adapter<*>? = null
    private var mChatLayoutManager: RecyclerView.LayoutManager? = null
    private var mSendEditText: EditText? = null
    private var mSendButton: Button? = null
    private var currentUserID: String? = null
    private var matchId: String? = null
    private var chatId: String? = null

    private var mDatabaseUser: DatabaseReference? = null
    private var mDatabaseChat: DatabaseReference? = null

    private val resultsChat = ArrayList<ChatObject>()
    private val dataSetChat: List<ChatObject>
        get() = resultsChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        matchId = intent.extras?.getString("matchId")
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid

        mDatabaseUser = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUserID ?: "")
            .child("connections")
            .child("matches")
            .child(matchId ?: "")
            .child("ChatId")

        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView?.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@ChatActivity).also { mChatLayoutManager = it }
            adapter = ChatAdapter(dataSetChat, this@ChatActivity).also { mChatAdapter = it }
        }

        mSendEditText = findViewById(R.id.message)
        mSendButton = findViewById(R.id.send)

        mSendButton?.setOnClickListener { sendMessage() }

        setupChat()
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText?.text.toString().trim()
        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat?.push()
            val newMessage = mapOf(
                "createdByUser" to currentUserID,
                "text" to sendMessageText
            )
            newMessageDb?.setValue(newMessage)
            mSendEditText?.setText("") // limpa o campo depois de enviar
        }
    }

    private fun setupChat() {
        // Carregar ChatId primeiro, e só depois os messages
        mDatabaseUser?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.value.toString()
                    mDatabaseChat = mDatabaseChat?.child(chatId!!)
                    loadChatMessages()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadChatMessages() {
        mDatabaseChat?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val message = dataSnapshot.child("text").value?.toString()
                val createdByUser = dataSnapshot.child("createdByUser").value?.toString()

                if (message != null && createdByUser != null) {
                    val isCurrentUser = createdByUser == currentUserID
                    resultsChat.add(ChatObject(message, isCurrentUser))
                    mChatAdapter?.notifyItemInserted(resultsChat.size - 1)
                    mRecyclerView?.scrollToPosition(resultsChat.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
