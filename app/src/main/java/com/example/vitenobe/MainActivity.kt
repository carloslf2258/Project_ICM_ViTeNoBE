package com.example.vitenobe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vitenobe.Cards.arrayAdapter
import com.example.vitenobe.Cards.cards
import com.example.vitenobe.Matches.MatchesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class MainActivity : AppCompatActivity() {

    private lateinit var arrayAdapter: arrayAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var usersDb: DatabaseReference

    private lateinit var currentUId: String
    private lateinit var rowItems: MutableList<cards>
    private var userSex: String? = null
    private var oppositeUserSex: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa os objetos Firebase
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        // Verifica se o currentUser é nulo
        currentUId = mAuth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        // Inicializa outros elementos da UI
        rowItems = mutableListOf()
        arrayAdapter = arrayAdapter(this, R.layout.item, rowItems)

        val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.frame)
        flingContainer.adapter = arrayAdapter

        checkUserSex()

        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                rowItems.removeAt(0)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                val userId = (dataObject as cards).userId
                usersDb.child(userId)
                    .child("connections").child("nope").child(currentUId)
                    .setValue(true)
                Toast.makeText(this@MainActivity, "Left", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                val userId = (dataObject as cards).userId
                usersDb.child(userId)
                    .child("connections").child("yeps").child(currentUId)
                    .setValue(true)
                isConnectionMatch(userId)
                Toast.makeText(this@MainActivity, "Right", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(scrollProgressPercent: Float) {}
        })

        flingContainer.setOnItemClickListener { _, _ ->
            Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // Functions
    fun onBackPressed(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun isConnectionMatch(userId: String) {
        val connectionPath = usersDb.child(currentUId)
            .child("connections").child("yeps").child(userId)

        connectionPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@MainActivity, "New Connection", Toast.LENGTH_LONG).show()
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    key?.let {
                        usersDb.child(userId)
                            .child("connections").child("matches")
                            .child(currentUId).child("ChatId").setValue(it)

                        usersDb.child(currentUId)
                            .child("connections").child("matches")
                            .child(userId).child("ChatId").setValue(it)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun checkUserSex() {
        val userDb = usersDb.child(currentUId)

        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userSex = dataSnapshot.child("sex").value?.toString()
                    oppositeUserSex = when (userSex) {
                        "Male" -> "Female"
                        "Female" -> "Male"
                        else -> null
                    }
                    loadOppositeSexUsers()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadOppositeSexUsers() {
        usersDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val sex = dataSnapshot.child("sex").value?.toString()
                val profileImageUrl = dataSnapshot.child("profileImageUrl").value?.toString() ?: "default"

                if (
                    dataSnapshot.exists() &&
                    sex == oppositeUserSex &&
                    !dataSnapshot.child("connections").child("nope").hasChild(currentUId) &&
                    !dataSnapshot.child("connections").child("yeps").hasChild(currentUId)
                ) {
                    val item = cards(
                        userId = dataSnapshot.key ?: return,
                        name = dataSnapshot.child("name").value?.toString() ?: "No Name",
                        profileImageUrl = profileImageUrl
                    )
                    rowItems.add(item)
                    arrayAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun logoutUser(view: View) {
        mAuth.signOut()
        startActivity(Intent(this, ChooseLoginRegistrationActivity::class.java))
        finish()
    }

    fun goToSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun goToMatches(view: View) {
        startActivity(Intent(this, MatchesActivity::class.java))
    }
}
