package com.example.vitenobe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vitenobe.Cards.cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainViewModel : ViewModel() {

    private val _cardList = MutableLiveData<List<cards>>(emptyList())
    val cardList: LiveData<List<cards>> = _cardList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val rowItems = mutableListOf<cards>()

    private var currentUId: String = ""
    private var userSex: String? = null
    private var oppositeUserSex: String? = null

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val usersDb: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")


    fun initializeUser() {
        currentUId = mAuth.currentUser?.uid ?: return
        checkUserSex()
    }

    fun removeFirstCard() {
        if (rowItems.isNotEmpty()) {
            rowItems.removeAt(0)
            _cardList.value = ArrayList(rowItems)
        }
    }

    fun handleSwipeLeft(userId: String) {
        usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true)
        _toastMessage.value = "Left"
    }

    fun handleSwipeRight(userId: String) {
        usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true)
        isConnectionMatch(userId)
        _toastMessage.value = "Right"
    }

    private fun isConnectionMatch(userId: String) {
        val connectionPath = usersDb.child(currentUId).child("connections").child("yeps").child(userId)

        connectionPath.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _toastMessage.postValue("New Connection")
                    val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    key?.let {
                        usersDb.child(userId).child("connections").child("matches")
                            .child(currentUId).child("ChatId").setValue(it)

                        usersDb.child(currentUId).child("connections").child("matches")
                            .child(userId).child("ChatId").setValue(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun checkUserSex() {
        val userDb = usersDb.child(currentUId)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userSex = snapshot.child("sex").value?.toString()
                oppositeUserSex = when (userSex) {
                    "Male" -> "Female"
                    "Female" -> "Male"
                    else -> null
                }
                loadOppositeSexUsers()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadOppositeSexUsers() {
        usersDb.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (isValidUser(snapshot)) {
                    val item = cards(
                        userId = snapshot.key ?: return,
                        name = snapshot.child("name").value?.toString() ?: "No Name",
                        profileImageUrl = snapshot.child("profileImageUrl").value?.toString() ?: "default"
                    )
                    rowItems.add(item)
                    _cardList.postValue(ArrayList(rowItems))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun isValidUser(snapshot: DataSnapshot): Boolean {
        val sex = snapshot.child("sex").value?.toString()
        return sex == oppositeUserSex &&
                !snapshot.child("connections").child("nope").hasChild(currentUId) &&
                !snapshot.child("connections").child("yeps").hasChild(currentUId)
    }

    fun logout() {
        mAuth.signOut()
    }
}

