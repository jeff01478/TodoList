package com.example.todolist.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.todolist.GlobalVariable
import com.example.todolist.R
import com.example.todolist.Task
import kotlin.math.log

class TaskAdapter(fragmentActivity: FragmentActivity, val resourceId: Int, private var data: List<Task>) :
        ArrayAdapter<Task>(fragmentActivity, resourceId, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, parent, false)
        val taskTitle: TextView = view.findViewById(R.id.itemTaskTitle)
        val taskDate: TextView = view.findViewById(R.id.itemTaskDate)
        val taskCheckBox: CheckBox = view.findViewById(R.id.itemTaskCheckBox)
        val task = getItem(position)
        if (task != null) {
            taskTitle.text = task.title
            taskDate.text = task.date
        }

        taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            completeTask(isChecked, position)
            GlobalVariable.updateListView(GlobalVariable.taskArrayList)
            if (GlobalVariable.searchMod)
                GlobalVariable.updateListView(GlobalVariable.filteredList)
        }

        // 禁用CheckBox的聚焦能力，保留點擊能力
        taskCheckBox.isFocusable = false
        taskCheckBox.isFocusableInTouchMode = false
        return view
    }

    private fun completeTask(isChecked: Boolean, position: Int) {
        if (isChecked) {
            GlobalVariable.apply {
                if (searchMod) {
                    if (viewPagerPosition == 0) {
                        val currentItemId = filteredList[0][position].id
                        val currentItemClass = filteredList[0][position].taskClass
                        val currentItem = filteredList[currentItemClass]
                            .find { it.id == currentItemId }
                        filteredList[0][position].isComplete = true
                        currentItem?.isComplete = true
                        if (currentItem != null) {
                            completeList.add(currentItem)
                        }
                        if (currentItemClass != 0){
                            filteredList[currentItemClass].removeIf { it.id == currentItemId }
                            taskArrayList[currentItemClass].list.removeIf { it.id == currentItemId }
                        }
                        allTaskList.remove(filteredList[0][position])
                        filteredList[0].remove(filteredList[0][position])
                    } else {
                        val currentItem = taskArrayList[viewPagerPosition].list[position]
                        val currentItemId = currentItem.id
                        completeList.add(currentItem)
                        filteredList[0].removeIf { it.id == currentItemId }
                        filteredList[viewPagerPosition].removeIf { it.id == currentItemId }
                        allTaskList.removeIf { it.id == currentItemId }
                        taskArrayList[viewPagerPosition].list.removeIf { it.id == currentItemId }
                    }
                } else {
                    if (viewPagerPosition == 0) {
                        val currentItemId = allTaskList[position].id
                        val currentItemClass = allTaskList[position].taskClass
                        val currentItem = taskArrayList[currentItemClass].list
                            .find { it.id == currentItemId }
                        allTaskList[position].isComplete = true
                        currentItem?.isComplete = true
                        if (currentItem != null) {
                            completeList.add(currentItem)
                        }
                        if (currentItemClass != 0)
                            taskArrayList[currentItemClass].list.removeIf { it.id == currentItemId }
                        allTaskList.removeIf { it.id == currentItemId }
                    } else {
                        val currentItem = taskArrayList[viewPagerPosition].list[position]
                        val currentItemId = currentItem.id
                        completeList.add(currentItem)
                        allTaskList.removeIf { it.id == currentItemId }
                        taskArrayList[viewPagerPosition].list.removeIf { it.id == currentItemId }
                    }
                }
            }
        }
    }

}