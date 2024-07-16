package com.example.todolist.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.todolist.GlobalVariable
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.TaskUpdateListener
import com.example.todolist.activity.TaskActivity
import com.example.todolist.adapter.TaskAdapter
import kotlin.io.path.fileVisitor

class AllTasksFragment(fragmentActivity: FragmentActivity) : Fragment(), TaskUpdateListener {

    private var listView: ListView? = null

    private val fragmentActivity = fragmentActivity
    private var adapter = TaskAdapter(fragmentActivity,
        R.layout.task_item,
        GlobalVariable.allTaskList
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_tasks, container, false)
        listView = view.findViewById(R.id.taskListView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView?.adapter = adapter
        adapter.notifyDataSetChanged()

        listView?.setOnItemClickListener { _, _, position, _ ->
            val task = if (GlobalVariable.searchMod) {
                GlobalVariable.filteredList[GlobalVariable.viewPagerPosition][position]
            } else {
                GlobalVariable.allTaskList[position]
            }
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("title", task.title)
            intent.putExtra("description", task.description)
            intent.putExtra("priority", task.priority)
            intent.putExtra("date", task.date)
            intent.putExtra("taskClass", task.taskClass)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    override fun onTaskAdded() {
        adapter.notifyDataSetChanged()
    }

    override fun onTaskSeach(list: List<Task>) {
        if (listView != null) {
            adapter = TaskAdapter(fragmentActivity,
                R.layout.task_item,
                list
            )
            listView!!.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}