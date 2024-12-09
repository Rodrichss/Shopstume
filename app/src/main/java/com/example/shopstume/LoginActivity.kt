package com.example.shopstume

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {
    private lateinit var intent:Intent
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var correo: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()

        correo = findViewById(R.id.editEmail)
        password = findViewById(R.id.editPassword)
        loginButton = findViewById(R.id.buttonLogin)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        loginButton.setOnClickListener{
            val corr = correo.text.toString()
            val pass = password.text.toString()

            if(verifyCredentials(corr, pass)){
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }else{
                Toast.makeText(this, "Lo sentimos, Algo salio mal. :,(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyCredentials(email:String, pass:String):Boolean{
        val usersJSON = sharedPreferences.getString("users", "[]")
        val jsonArray = JSONArray(usersJSON)

        for(i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(i)
            val storedEmail = jsonObject.getString("email")
            val storedPassword = jsonObject.getString("password")
            val role = jsonObject.getString("role")
            val storedName = jsonObject.getString("name")
            val storedLastName = jsonObject.getString("lastName")

            if(email == storedEmail && pass == storedPassword ){
                sharedPreferences.edit()
                    .putString("userRole",role)
                    .putString("userEmail",storedEmail)
                    .putString("userName", storedName)
                    .putString("userLastName", storedLastName)
                    .apply()
                return true
            }
        }
        return false
    }
}