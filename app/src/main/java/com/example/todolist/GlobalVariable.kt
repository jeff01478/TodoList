package com.example.todolist

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.fragment.app.Fragment

class GlobalVariable {
    companion object {
        val allTaskList = ArrayList<Task>()
        val personalTaskList = ArrayList<Task>()
        val workTaskList = ArrayList<Task>()
        val taskArrayList = ArrayList<AllTaskList>()
        val filteredList = ArrayList<ArrayList<Task>>()
        val completeList = ArrayList<Task>()
        val fragments = mutableListOf<Fragment>()
        var viewPagerPosition = 0
        var searchMod = false
        var launch = false
        lateinit var dbHelper: TaskDatabaseHelper
        fun initTaskArrayList() {
            taskArrayList.add(AllTaskList(allTaskList, 0))
            taskArrayList.add(AllTaskList(personalTaskList, 1))
            taskArrayList.add(AllTaskList(workTaskList, 2))
        }

        fun addTaskSQL(task: Task) {
            val db = dbHelper.writableDatabase
            val newTask = ContentValues().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("priority", task.priority)
                put("date", task.date)
                put("taskClass", task.taskClass)
            }
            db.insert("AllTask", null, newTask)

            when (task.taskClass) {
                1 -> db.insert("PersonalTask", null, newTask)
                2 -> db.insert("WorkTask", null, newTask)
            }
        }

        fun deleteNote(id: Int, taskClass: Int) {
            val db = dbHelper.writableDatabase
            db.delete("AllTask", "id = ?", arrayOf(id.toString()))
            when (taskClass) {
                1 -> db.delete("PersonalTask", "id = ?", arrayOf(id.toString()))
                2 -> db.delete("WorkTask", "id = ?", arrayOf(id.toString()))
            }
        }

        fun updateNote(task: Task) {
            val db = dbHelper.writableDatabase
            val updateTask = ContentValues().apply {
                put("title", task.title)
                put("description", task.description)
                put("priority", task.priority)
                put("date", task.date)
                put("taskClass", task.taskClass)
            }
            db.update("AllTask", updateTask, "id = ?", arrayOf(task.id.toString()))
            when (task.taskClass) {
                1 -> db.update("PersonalTask", updateTask, "id = ?", arrayOf(task.id.toString()))
                2 -> db.update("WorkTask", updateTask, "id = ?", arrayOf(task.id.toString()))
            }
        }

        @SuppressLint("Range")
        fun queryNote() {
            val db = dbHelper.writableDatabase
            var cursor = db.query("AllTask", null, null, null,
                null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val priority = cursor.getInt(cursor.getColumnIndex("priority"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val taskClass = cursor.getInt(cursor.getColumnIndex("taskClass"))
                    GlobalVariable.allTaskList.add(Task(id, title, description, priority, date, taskClass))
                } while (cursor.moveToNext())
            }

            cursor = db.query("PersonalTask", null, null, null,
                null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val priority = cursor.getInt(cursor.getColumnIndex("priority"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val taskClass = cursor.getInt(cursor.getColumnIndex("taskClass"))
                    GlobalVariable.personalTaskList.add(Task(id, title, description, priority, date, taskClass))
                } while (cursor.moveToNext())
            }

            cursor = db.query("WorkTask", null, null, null,
                null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val priority = cursor.getInt(cursor.getColumnIndex("priority"))
                    val date = cursor.getString(cursor.getColumnIndex("date"))
                    val taskClass = cursor.getInt(cursor.getColumnIndex("taskClass"))
                    GlobalVariable.workTaskList.add(Task(id, title, description, priority, date, taskClass))
                } while (cursor.moveToNext())
            }
        }

        @JvmName("updateAllTaskList")
        fun updateListView(list: ArrayList<ArrayList<Task>>) {
            for (i in 0..fragments.size - 1) {
                val fragment = fragments[i]
                if (fragment is TaskUpdateListener) {
                    fragment.onTaskSeach(list[i])
                }
            }
        }

        @JvmName("updateFilteredList")
        fun updateListView(list: ArrayList<AllTaskList>) {
            for (i in 0..fragments.size - 1) {
                val fragment = fragments[i]
                if (fragment is TaskUpdateListener) {
                    fragment.onTaskSeach(list[i].list)
                }
            }
        }
    }
}