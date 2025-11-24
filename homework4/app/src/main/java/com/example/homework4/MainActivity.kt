package com.example.homework4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.homework4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainMeal.setOnClickListener {
            startActivity(Intent(this, MainMealActivity::class.java))
        }
        binding.btnSideDishes.setOnClickListener {
            startActivity(Intent(this, SideDishesActivity::class.java))
        }
        binding.btnDrink.setOnClickListener {
            startActivity(Intent(this, DrinkActivity::class.java))
        }
        binding.btnConfirm.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to submit your order?")
            .setPositiveButton("Submit") { _, _ ->
                showToast("Order submitted!")
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}