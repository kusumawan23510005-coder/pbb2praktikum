package com.example.aplikasiordal.usecases

import com.example.aplikasiordal.entitiy.Todo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TodoUseCase {

    private val db = Firebase.firestore

    // Ambil semua data
    suspend fun getTodo(): List<Todo> {
        try {
            val data = db.collection("todo")
                .get()
                .await()

            if (!data.isEmpty) {
                return data.documents.map {
                    Todo(
                        id = it.id,
                        title = it.getString("title").toString(),
                        description = it.getString("description").toString()
                    )
                }
            }

            return arrayListOf()
        } catch (exc: Exception) {
            throw Exception(exc.message)
        }
    }

    // Ambil 1 data berdasarkan id
    suspend fun getTodo(id: String): Todo? {
        val data = db.collection("todo")
            .document(id)
            .get()
            .await()

        return if (data.exists()) {
            Todo(
                id = data.id,
                title = data.getString("title").toString(),
                description = data.getString("description").toString()
            )
        } else {
            null
        }
    }

    // Update data berdasarkan id
    suspend fun updateTodo(todo: Todo) {
        val payload = hashMapOf(
            "title" to todo.title,
            "description" to todo.description
        )

        try {
       val data = db.collection("todo")
                .document(todo.id)
                .set(payload)
                .await()

        } catch (exc: Exception) {
            throw Exception(exc.message)
        }
    }

    // Tambahkan todo baru
    suspend fun createTodo(todo: Todo): Todo {
        val data = hashMapOf(
            "title" to todo.title,
            "description" to todo.description
        )

        try {
            val docRef = db.collection("todo")
                .add(data)
                .await()

            return todo.copy(id = docRef.id)
        } catch (exc: Exception) {
            throw Exception(exc.message)
        }
    }

    // Hapus todo berdasarkan id
    suspend fun deleteTodo(id: String) {
        try {
            db.collection("todo")
                .document(id)
                .delete()
                .await()
        } catch (exc: Exception) {
            throw Exception(exc.message)
        }
    }
}
