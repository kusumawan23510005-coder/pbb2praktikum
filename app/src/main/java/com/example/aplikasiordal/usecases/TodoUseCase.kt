package com.example.aplikasiordal.usecases

import com.example.aplikasiordal.entitiy.Todo
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class TodoUseCase {
    val db = Firebase.firestore

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

            return arrayListOf();
        } catch (exc: Exception) {
            throw Exception(exc.message)
        }
    }

    suspend fun createTodo(todo: Todo): Todo {
        val data = hashMapOf(
            "title" to todo.title,
            "description" to todo.description
         )

        try{
            val docRef = db.collection ("todo")
                .add(data)
                .await()

            return todo.copy(id= docRef.id)
        }catch (exc: Exception){
            throw Exception(exc.message)
        }

    }
}
