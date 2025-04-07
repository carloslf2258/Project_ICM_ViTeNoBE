package com.example.vitenobe.Chat

import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vitenobe.Cards.cards
import com.example.vitenobe.Chat.MessageAdapter
import com.google.firebase.database.*

class ChatProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var profileNameTextView: TextView
    private lateinit var profileAgeTextView: TextView
    private lateinit var profileDescriptionTextView: TextView
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var messagesList: MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var user: cards  // Representando o perfil do usuário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_profiles_activity)

        // Inicializar os elementos da UI
        profileImageView = findViewById(R.id.profileImage)
        profileNameTextView = findViewById(R.id.profileName)
        profileAgeTextView = findViewById(R.id.profileAge)
        profileDescriptionTextView = findViewById(R.id.profileDescription)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Inicializando o Firebase
        database = FirebaseDatabase.getInstance().reference
        messagesList = mutableListOf()
        messageAdapter = MessageAdapter(messagesList)

        // Configurar RecyclerView para mensagens
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter

        // Recuperar dados do perfil (pode ser obtido de uma Intent ou Firebase)
        user = intent.getSerializableExtra("userProfile") as cards

        // Configurar dados do perfil
        loadProfileData()

        // Carregar mensagens
        loadMessages()

        // Enviar mensagem
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadProfileData() {
        // Carregar a imagem do perfil e os dados
        profileNameTextView.text = user.name
        profileAgeTextView.text = "Idade: ${user.age}"  // Supondo que a classe `cards` tem a propriedade `age`
        profileDescriptionTextView.text = user.description  // Supondo que a classe `cards` tem a propriedade `description`

        // Carregar a imagem do perfil
        if (user.profileImageUrl == "default") {
            Glide.with(this).load(R.mipmap.ic_launcher).into(profileImageView)
        } else {
            Glide.with(this).load(user.profileImageUrl).into(profileImageView)
        }
    }

    private fun loadMessages() {
        // Carregar as mensagens do Firebase
        database.child("messages").child(user.userId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messagesList.add(it)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatProfileActivity, "Erro ao carregar mensagens", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()

        if (messageText.isNotEmpty()) {
            val message = Message(messageText, "userId", System.currentTimeMillis().toString())

            // Enviar a mensagem para o Firebase
            database.child("messages").child(user.userId).push().setValue(message)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        messageEditText.text.clear()  // Limpar o campo de entrada
                    } else {
                        Toast.makeText(this, "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
