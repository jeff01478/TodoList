package com.example.todolist.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.todolist.GlobalVariable
import com.example.todolist.GlobalVariable.Companion.dbHelper
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.TaskDatabaseHelper
import com.example.todolist.TaskUpdateListener
import com.example.todolist.adapter.TaskPagerAdapter
import com.example.todolist.fragment.AllTasksFragment
import com.example.todolist.fragment.PersonalTasksFragment
import com.example.todolist.fragment.WorkTasksFragment
import com.example.todolist.test
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var loadingView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var searchResultLinearLayout: LinearLayout
    private lateinit var menuButton: ImageButton
    private lateinit var btnSearchResultBack: ImageButton
    private lateinit var addTask: FloatingActionButton
    private lateinit var popupWindow: PopupWindow
    private lateinit var viewPager: ViewPager2
    lateinit var datePicker: DatePickerDialog.OnDateSetListener

    private var adapter = TaskPagerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = TaskDatabaseHelper(this, "Task", 1)
        if (!GlobalVariable.launch) {
            GlobalVariable.queryNote()
            GlobalVariable.launch = true
        }
        GlobalVariable.initTaskArrayList()
        //test.test()
        initObject()
        viewPager.adapter = adapter
        initFragments()

        lifecycleScope.launch {
            for (i in GlobalVariable.fragments.size - 1 downTo 0) {
                viewPager.setCurrentItem(i, false)
                delay(500)
            }
            loadingView.visibility = View.GONE
        }

        val menuView = layoutInflater.inflate(R.layout.layout_main_menu, null)
        popupWindow = PopupWindow(menuView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)
        popupWindow.elevation = 50f
        menuButton.setOnClickListener {
            if (!popupWindow.isShowing) {
                popupWindow.showAsDropDown(menuButton)
            }
        }

        // 設置菜單項點擊監聽器
        menuView.findViewById<TextView>(R.id.menuItemSearch).setOnClickListener {
            // 處理搜索操作
            showSearchDialog()
            popupWindow.dismiss()
        }

        menuView.findViewById<TextView>(R.id.menuItemSort).setOnClickListener {
            // 處理排序操作
            showSortingOptions(it)
            popupWindow.dismiss()
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "所有"
                1 -> "個人"
                2 -> "工作"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()

        addTask.setOnClickListener {
            showAddTaskDialog()
            GlobalVariable.updateListView(GlobalVariable.taskArrayList)
        }

        btnSearchResultBack.setOnClickListener {
            for (i in 0..GlobalVariable.fragments.size - 1) {
                val fragment = GlobalVariable.fragments[i]
                if (fragment is TaskUpdateListener) {
                    fragment.onTaskSeach(GlobalVariable.taskArrayList[i].list)
                }
            }
            searchResultLinearLayout.visibility = View.GONE
            GlobalVariable.searchMod = false
        }
    }

    private fun initFragments() {
        adapter.addFragment(AllTasksFragment(this))
        adapter.addFragment(PersonalTasksFragment(this))
        adapter.addFragment(WorkTasksFragment(this))
    }

    private fun initObject() {
        addTask = findViewById(R.id.addTask)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        menuButton = findViewById(R.id.menuButton)
        btnSearchResultBack = findViewById(R.id.btnSearchResultBack)
        searchResultLinearLayout = findViewById(R.id.searchResultLinearLayout)
        loadingView = findViewById(R.id.loadingView)
    }

    private fun showAddTaskDialog() {
        //初始化dialogView
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val title = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val description = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val dateButton = dialogView.findViewById<Button>(R.id.dateButton)
        val dateTextView = dialogView.findViewById<TextView>(R.id.dateTextView)
        dateButton.setOnClickListener {
            showDatePickerDialog { year, mouth, day ->
                showTimePickerDialog { hour, minute ->
                    dateTextView.text = "$year/$mouth/$day\n$hour 點 $minute 分"
                }
            }
        }

        // 初始化Spinner
        val prioritySpinner = dialogView.findViewById<Spinner>(R.id.prioritySpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.priority_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = adapter
        }

        // 初始化Spinner
        val taskClassSpinner = dialogView.findViewById<Spinner>(R.id.taskClassSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.taskClass_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            taskClassSpinner.adapter = adapter
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add Task")
            .setPositiveButton("Add") { dialog, _ ->
                val id = if (GlobalVariable.allTaskList.size == 0) {
                    0
                } else {
                    GlobalVariable.allTaskList[GlobalVariable.allTaskList.size - 1].id + 1
                }
                val priority = when(prioritySpinner.selectedItem.toString()) {
                    "高" -> 1
                    "無" -> 2
                    "低" -> 3
                    else -> 2
                }
                val taskClass = when(taskClassSpinner.selectedItem.toString()) {
                    "個人" -> 1
                    "工作" -> 2
                    else -> 0
                }

                if (taskClass == 0) {
                    GlobalVariable.allTaskList.add(
                        Task(
                            id,
                            title.text.toString(),
                            description.text.toString(),
                            priority,
                            dateTextView.text.toString(),
                            taskClass
                        )
                    )
                } else {
                    GlobalVariable.taskArrayList[taskClass].list.add(
                        Task(
                            id,
                            title.text.toString(),
                            description.text.toString(),
                            priority,
                            dateTextView.text.toString(),
                            taskClass
                        )
                    )
                    GlobalVariable.allTaskList.add(
                        Task(
                            id,
                            title.text.toString(),
                            description.text.toString(),
                            priority,
                            dateTextView.text.toString(),
                            taskClass
                        )
                    )
                }
                updateList()
                GlobalVariable.addTaskSQL(Task(
                    id,
                    title.text.toString(),
                    description.text.toString(),
                    priority,
                    dateTextView.text.toString(),
                    taskClass
                ))
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val edtSearch = dialogView.findViewById<EditText>(R.id.edtSearch)
        val btnSearch = dialogView.findViewById<ImageButton>(R.id.btnSearch)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("搜索")
            .setView(dialogView)
            .create()

        btnSearch.setOnClickListener {
            val searchQuery = edtSearch.text.toString()
            if (searchQuery.isNotEmpty()) {
                performSearch(searchQuery)
                searchResultLinearLayout.visibility = View.VISIBLE
                GlobalVariable.searchMod = true
                alertDialog.dismiss()
            }
        }

        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = edtSearch.text.toString()
                if (searchQuery.isNotEmpty()) {
                    performSearch(searchQuery)
                    alertDialog.dismiss()
                }
                true
            } else {
                false
            }
        }

        alertDialog.show()
    }

    private fun performSearch(query: String) {
        GlobalVariable.filteredList.clear()
        GlobalVariable.taskArrayList.forEach { item ->
            GlobalVariable.filteredList.add(item.list.filter {
                it.title.contains(query, ignoreCase = true)
            } as ArrayList<Task>)
        }

        for (i in 0..GlobalVariable.fragments.size - 1) {
            val fragment = GlobalVariable.fragments[i]
            if (fragment is TaskUpdateListener) {
                fragment.onTaskSeach(GlobalVariable.filteredList[i])
            }
        }
    }

    private fun showDatePickerDialog(onDateSelected: (Int, Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            onDateSelected(selectedYear, selectedMonth + 1, selectedDay)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog(onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute, ->
            onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    fun addNewTaskFragment(fragment: Fragment) {
        adapter.addFragment(fragment)
        viewPager.currentItem = adapter.itemCount - 1
    }

    private fun showSortingOptions(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_sort, popupMenu.menu)
        popupMenu.apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.sort_name -> {
                        // 按名稱排序
                        val listId = viewPager.currentItem
                        GlobalVariable.taskArrayList[listId].list.sortBy {
                            it.title
                        }
                        updateList()
                        true
                    }
                    R.id.sort_priority -> {
                        // 按優先級排序
                        val listId = viewPager.currentItem
                        GlobalVariable.taskArrayList[listId].list.sortBy {
                            it.priority
                        }
                        updateList()
                        true
                    }
                    R.id.sort_date -> {
                        // 按日期排序
                        val listId = viewPager.currentItem
                        GlobalVariable.taskArrayList[listId].list.sortBy {
                            it.date
                        }
                        updateList()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun updateList() {
        GlobalVariable.fragments.forEach {
            if (it is TaskUpdateListener) {
                it.onTaskAdded()
            }
        }
    }
}