package com.example.homework4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.homework4.databinding.ActivityMainMealBinding

class MainMealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}