package com.example.todolist

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.todolist.activity.MainActivity

class TaskDatabaseHelper(val context: MainActivity, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createAllTask = "create table AllTask (" +
            "id integer primary key, " +
            "title text, " +
            "description text, " +
            "priority int, " +
            "date String, " +
            "taskClass int)"

    private val createPersonalTask = "create table PersonalTask (" +
            "id integer primary key, " +
            "title text, " +
            "description text, " +
            "priority int, " +
            "date String, " +
            "taskClass int)"


    private val createWorkTask = "create table WorkTask (" +
            "id integer primary key, " +
            "title text, " +
            "description text, " +
            "priority int, " +
            "date String, " +
            "taskClass int)"

    private val createCompleteTask = "create table CompleteTask (" +
            "id integer primary key, " +
            "title text, " +
            "description text, " +
            "priority int, " +
            "date String, " +
            "taskClass int)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createAllTask)
        db?.execSQL(createPersonalTask)
        db?.execSQL(createWorkTask)
        db?.execSQL(createCompleteTask)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){}
}