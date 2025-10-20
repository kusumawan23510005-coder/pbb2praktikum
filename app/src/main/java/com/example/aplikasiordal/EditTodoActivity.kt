package com.example.aplikasiordal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.aplikasiordal.databinding.ActivityEditTodoBinding
import com.example.aplikasiordal.usecases.TodoUseCase
import kotlinx.coroutines.launch

class EditTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTodoBinding
    private lateinit var todoItemId: String
    private lateinit var todoUseCase: TodoUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi ViewBinding
        binding = ActivityEditTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur padding agar tidak tertutup status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil ID todo dari Intent
        todoItemId = intent.getStringExtra("todo.item.id") ?: ""

        // Inisialisasi use case
        todoUseCase = TodoUseCase()
    }

    private fun loadTodo() {
        lifecycleScope.launch {
            val todo = todoUseCase.getTodo(todoItemId)

            if (todo == null) {
                // Jika data tidak ditemukan, kembali ke TodoActivity
                val intent = Intent(this@EditTodoActivity, TodoActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Set data ke input field
                binding.title.setText(todo.title)
                binding.description.setText(todo.description)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadTodo()
    }
}
