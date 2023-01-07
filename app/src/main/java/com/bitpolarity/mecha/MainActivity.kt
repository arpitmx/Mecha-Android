package com.bitpolarity.mecha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bitpolarity.mecha.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

   lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener{
            startActivity(Intent(this,QRScannerActivity::class.java))
        }



    }
}