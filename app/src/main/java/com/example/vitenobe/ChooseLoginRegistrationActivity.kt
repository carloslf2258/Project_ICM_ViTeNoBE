package com.example.vitenobe


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vitenobe.Matches.MatchesActivity

class ChooseLoginRegistrationActivity : AppCompatActivity() {
    private var mLogin: Button? = null
    private var mRegister: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.choose_login_signup_activity)
        mLogin = findViewById<View>(R.id.login) as Button
        mRegister = findViewById<View>(R.id.signup) as Button
        mLogin!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
        mRegister!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginRegistrationActivity, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
    }

    fun OnBackPressed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}