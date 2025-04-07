package com.example.vitenobe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: AuthStateListener

    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var birthdayField: EditText
    private lateinit var interestsField: EditText
    private lateinit var registerButton: Button
    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        mAuth = FirebaseAuth.getInstance()

        nameField = findViewById(R.id.name)
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        birthdayField = findViewById(R.id.birthday)
        interestsField = findViewById(R.id.interests)
        registerButton = findViewById(R.id.register)
        backArrow = findViewById(R.id.arrow_back)

        firebaseAuthStateListener = AuthStateListener {
            val user = mAuth.currentUser
            if (user != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        registerButton.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val birthday = birthdayField.text.toString()
            val interests = interestsField.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerButton.isEnabled = false

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    registerButton.isEnabled = true

                    if (!task.isSuccessful) {
                        val errorMessage = task.exception?.message ?: "Erro desconhecido"
                        Toast.makeText(this, "Erro no registro: $errorMessage", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = mAuth.currentUser!!.uid
                        val currentUserDb = FirebaseDatabase.getInstance()
                            .reference.child("Users").child(userId)

                        val userInfo = mapOf(
                            "name" to name,
                            "birthday" to birthday,
                            "interests" to interests,
                            "profileImageUrl" to "default"
                        )

                        currentUserDb.updateChildren(userInfo)

                        /* test
                        val intent = Intent(this, ChooseLoginRegistrationActivity::class.java)
                        startActivity(intent)
                        finish()*/
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth.removeAuthStateListener(firebaseAuthStateListener)
    }

    fun OnBackPressed(view: View) {
        val intent = Intent(this, ChooseLoginRegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
