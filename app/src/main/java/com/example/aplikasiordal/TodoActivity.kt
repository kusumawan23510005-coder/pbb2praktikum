package com.example.aplikasiordal

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasiordal.adapter.TodoAdapter
import com.example.aplikasiordal.databinding.ActivityTodoBinding
import com.example.aplikasiordal.usecases.TodoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var adapter: TodoAdapter
    private val todoUseCase = TodoUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = TodoAdapter(mutableListOf())
        binding.container.layoutManager = LinearLayoutManager(this)
        binding.container.adapter = adapter


        loadTodos()

        registerEvents()
    }

    private fun registerEvents() {
        binding.TombolCreateTodo.setOnClickListener{
            val intent = Intent(this, CreateTodoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadTodos() {
        binding.uiLoading.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val todos = todoUseCase.getTodo()
                withContext(Dispatchers.Main) {
                    adapter.updateData(todos)
                    binding.uiLoading.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.uiLoading.visibility = View.GONE
                }
            }
        }
    }
}