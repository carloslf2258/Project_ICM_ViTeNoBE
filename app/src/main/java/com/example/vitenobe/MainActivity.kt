package com.example.vitenobe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.vitenobe.Cards.arrayAdapter
import com.example.vitenobe.Cards.cards
import com.example.vitenobe.Matches.MatchesActivity
import com.example.vitenobe.ChooseLoginRegistrationActivity
import com.example.vitenobe.SettingsActivity
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var arrayAdapter: arrayAdapter
    private lateinit var rowItems: MutableList<cards>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        rowItems = mutableListOf()
        arrayAdapter = arrayAdapter(this, R.layout.item, rowItems)

        val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.welcome)
        flingContainer.adapter = arrayAdapter

        viewModel.cardList.observe(this, Observer {
            rowItems.clear()
            rowItems.addAll(it)
            arrayAdapter.notifyDataSetChanged()
        })

        viewModel.toastMessage.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.initializeUser()

        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                viewModel.removeFirstCard()
            }

            override fun onLeftCardExit(dataObject: Any) {
                viewModel.handleSwipeLeft((dataObject as cards).userId)
            }

            override fun onRightCardExit(dataObject: Any) {
                viewModel.handleSwipeRight((dataObject as cards).userId)
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
            override fun onScroll(scrollProgressPercent: Float) {}
        })

        flingContainer.setOnItemClickListener { _, _ ->
            Toast.makeText(this, "Item Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun onBackPressed(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

    fun logoutUser(view: View) {
        viewModel.logout()
        startActivity(Intent(this, ChooseLoginRegistrationActivity::class.java))
        finish()
    }

    // Função que será chamada ao clicar no botão "Aceito"
    fun goToCreateAccount(view: View) {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }


}
