package com.example.vitenobe.Matches

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vitenobe.ChooseLoginRegistrationActivity
import com.example.vitenobe.MainActivity
import com.example.vitenobe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MatchesActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mMatchesAdapter: RecyclerView.Adapter<*>? = null
    private var mMatchesLayoutManager: RecyclerView.LayoutManager? = null
    private var cusrrentUserID: String? = null
    private val resultsMatches = ArrayList<MatchesObject>()
    private val dataSetMatches: List<MatchesObject> get() = resultsMatches

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        cusrrentUserID = FirebaseAuth.getInstance().currentUser!!.uid

        // RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.setHasFixedSize(true)
        mMatchesLayoutManager = LinearLayoutManager(this@MatchesActivity)
        mRecyclerView!!.layoutManager = mMatchesLayoutManager
        mMatchesAdapter = MatchesAdapter(dataSetMatches, this@MatchesActivity)
        mRecyclerView!!.adapter = mMatchesAdapter

        // Botão de Voltar
        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }  // Volta para a tela anterior

        // Carregar os Matches
        userMatchId()
    }

    private fun userMatchId() {
        val matchDb = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(cusrrentUserID.toString())
            .child("connections")
            .child("matches")

        matchDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (match in dataSnapshot.children) {
                        FetchMatchInformation(match.key.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun FetchMatchInformation(key: String) {
        val userDb = FirebaseDatabase.getInstance().reference.child("Users").child(key)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userId = dataSnapshot.key
                    var name = ""
                    var profileImageUrl = ""
                    if (dataSnapshot.child("name").value != null) {
                        name = dataSnapshot.child("name").value.toString()
                    }
                    if (dataSnapshot.child("profileImageUrl").value != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").value.toString()
                    }

                    val obj = MatchesObject(userId.toString(), name, profileImageUrl)
                    resultsMatches.add(obj)

                    // Atualiza a UI com a lista de matches
                    mMatchesAdapter!!.notifyDataSetChanged()

                    // Se não houver matches, exibe uma mensagem
                    if (resultsMatches.isEmpty()) {
                        Toast.makeText(this@MatchesActivity, "Você não tem matches ainda!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun OnBackPressed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
