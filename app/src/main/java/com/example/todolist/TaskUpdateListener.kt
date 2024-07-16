package com.example.todolist

import android.content.Context

interface TaskUpdateListener {
    fun onTaskAdded()
    fun onTaskSeach(list: List<Task>)
}