package com.example.todolist.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.example.todolist.GlobalVariable
import com.example.todolist.R
import com.example.todolist.Task
import java.util.zip.GZIPOutputStream

class TaskActivity : AppCompatActivity() {

    private lateinit var edtTittle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var back: ImageButton
    private lateinit var save: ImageButton
    private lateinit var delete: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        initObject()
        val taskTitle = intent.getStringExtra("title")
        val taskDescription = intent.getStringExtra("description")
        val taskPriority = intent.getIntExtra("priority", 0)
        val taskDate = intent.getStringExtra("date")
        val taskTaskClass = intent.getIntExtra("taskClass", 0)
        val position = intent.getIntExtra("position", -1)

        edtTittle.setText(taskTitle)
        edtDescription.setText(taskDescription)

        save.setOnClickListener {
            val title = edtTittle.text.toString()
            val description = edtDescription.text.toString()
            val listId = if (GlobalVariable.searchMod) GlobalVariable.filteredList[GlobalVariable.viewPagerPosition][position].id
                        else GlobalVariable.taskArrayList[GlobalVariable.viewPagerPosition].list[position].id
            val listTaskClass = GlobalVariable.allTaskList.find { it.id == listId }?.taskClass
            Log.d("TaskClass", listTaskClass.toString())
            val listItem = Task(listId, title, description, taskPriority, taskDate!!, taskTaskClass)
            var listIndex = 0

            if (listTaskClass != null) {
                if (listTaskClass == 0) {
                    if (GlobalVariable.searchMod) {
                        listIndex = GlobalVariable.filteredList[0].indexOfFirst { it.id == listId }
                        GlobalVariable.filteredList[0][listIndex] = listItem
                    }
                    listIndex = GlobalVariable.allTaskList.indexOfFirst { it.id == listId }
                    GlobalVariable.allTaskList[listIndex] = listItem
                } else {
                    if (GlobalVariable.searchMod) {
                        listIndex = GlobalVariable.filteredList[0].indexOfFirst { it.id == listId }
                        GlobalVariable.filteredList[0][listIndex] = listItem
                        listIndex = GlobalVariable.filteredList[listTaskClass].indexOfFirst { it.id == listId }
                        GlobalVariable.filteredList[listTaskClass][listIndex] = listItem
                    }
                    listIndex = GlobalVariable.allTaskList.indexOfFirst { it.id == listId }
                    GlobalVariable.allTaskList[listIndex] = listItem
                    listIndex = GlobalVariable.taskArrayList[listTaskClass].list.indexOfFirst { it.id == listId }
                    GlobalVariable.taskArrayList[listTaskClass].list[listIndex] = listItem
                }
            }

            GlobalVariable.updateNote(Task(
                listId, title, description, taskPriority, taskDate, taskTaskClass
            ))
            GlobalVariable.updateListView(GlobalVariable.taskArrayList)
            if (GlobalVariable.searchMod)
                GlobalVariable.updateListView(GlobalVariable.filteredList)
            finish()
        }

        delete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("刪除任務")
                setMessage("確定要將此任務刪除?")
                setPositiveButton("確認") { _, _ ->
                    deleteTask(position)
                }
                setNegativeButton("取消") { _, _ ->

                }
                show()
            }
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun initObject() {
        edtTittle = findViewById(R.id.edtTittle)
        edtDescription = findViewById(R.id.edtDescription)
        back = findViewById(R.id.back)
        save = findViewById(R.id.save)
        delete = findViewById(R.id.delete)
    }

    private fun deleteTask(position: Int) {
        val listId = if (GlobalVariable.searchMod) GlobalVariable.filteredList[GlobalVariable.viewPagerPosition][position].id
        else GlobalVariable.taskArrayList[GlobalVariable.viewPagerPosition].list[position].id
        val listTaskClass = GlobalVariable.allTaskList.find { it.id == listId }?.taskClass
        if (listTaskClass != null) {
            if (GlobalVariable.searchMod) {
                if (listTaskClass == 0) {
                    GlobalVariable.filteredList[0].removeIf { it.id == listId }
                    GlobalVariable.allTaskList.removeIf { it.id == listId }
                } else {
                    GlobalVariable.filteredList[0].removeIf { it.id == listId }
                    GlobalVariable.filteredList[listTaskClass].removeIf { it.id == listId }
                    GlobalVariable.allTaskList.removeIf { it.id == listId }
                    GlobalVariable.taskArrayList[listTaskClass].list.removeIf { it.id == listId }
                }
            } else {
                if (listTaskClass == 0) {
                    GlobalVariable.allTaskList.removeIf { it.id == listId }
                } else {
                    GlobalVariable.allTaskList.removeIf { it.id == listId }
                    GlobalVariable.taskArrayList[listTaskClass].list.removeIf { it.id == listId }
                }
            }
            GlobalVariable.deleteNote(listId, listTaskClass)
        }
        GlobalVariable.updateListView(GlobalVariable.taskArrayList)
        if (GlobalVariable.searchMod)
            GlobalVariable.updateListView(GlobalVariable.filteredList)
        finish()
    }
}