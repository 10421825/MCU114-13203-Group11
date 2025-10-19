package com.example.homework4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.homework4.databinding.ActivityDrinkBinding

class DrinkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}