package com.example.homework4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.homework4.databinding.ActivitySideDishesBinding

class SideDishesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySideDishesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySideDishesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDone.setOnClickListener {
            finish()
        }
    }
}