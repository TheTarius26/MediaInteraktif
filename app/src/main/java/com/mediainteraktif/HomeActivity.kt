package com.mediainteraktif

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        mAuth = FirebaseAuth.getInstance()

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_latihan,
                R.id.navigation_quiz, R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.lightBlue))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_3dot_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.daftar_pustaka -> {
                intent = Intent(this, PustakaActivity::class.java)
                startActivity(intent)
            }
            R.id.kompetensi -> {
                intent = Intent(this, KompetensiActivity::class.java)
                startActivity(intent)
            }
            R.id.help -> {
                intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> signingOut()
        }

        return super.onOptionsItemSelected(item)
    }

    fun signingOut() {
        mAuth.signOut()
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}