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
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mSendEditText: EditText
    private lateinit var mSendButton: Button
    private lateinit var currentUserID: String
    private lateinit var matchId: String
    private var chatId: String? = null

    private lateinit var mDatabaseUser: DatabaseReference
    private lateinit var mDatabaseChat: DatabaseReference

    private val resultsChat = ArrayList<ChatObject>()
    private val dataSetChat: List<ChatObject>
        get() = resultsChat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        matchId = intent.extras?.getString("matchId") ?: ""
        currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        mDatabaseUser = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(currentUserID)
            .child("connections")
            .child("matches")
            .child(matchId)
            .child("ChatId")

        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = ChatAdapter(dataSetChat, this@ChatActivity)
        }

        mSendEditText = findViewById(R.id.message)
        mSendButton = findViewById(R.id.send)

        mSendButton.setOnClickListener { sendMessage() }

        setupChat()
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText.text.toString().trim()
        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat.push()
            val newMessage = mapOf(
                "createdByUser" to currentUserID,
                "text" to sendMessageText
            )
            newMessageDb.setValue(newMessage)
            mSendEditText.setText("") // Limpa o campo depois de enviar
        }
    }

    private fun setupChat() {
        // Carregar ChatId primeiro, e só depois as mensagens
        mDatabaseUser.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    chatId = snapshot.value.toString()
                    mDatabaseChat = mDatabaseChat.child(chatId ?: "")
                    loadChatMessages()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadChatMessages() {
        mDatabaseChat.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val message = dataSnapshot.child("text").value?.toString()
                val createdByUser = dataSnapshot.child("createdByUser").value?.toString()

                if (message != null && createdByUser != null) {
                    val isCurrentUser = createdByUser == currentUserID
                    resultsChat.add(ChatObject(message, isCurrentUser))
                    mRecyclerView.adapter?.notifyItemInserted(resultsChat.size - 1)
                    mRecyclerView.scrollToPosition(resultsChat.size - 1)  // Scroll para a última mensagem
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
