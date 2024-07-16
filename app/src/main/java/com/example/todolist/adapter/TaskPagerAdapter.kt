package com.example.todolist.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.todolist.fragment.AllTasksFragment
import com.example.todolist.GlobalVariable
import com.example.todolist.fragment.PersonalTasksFragment
import com.example.todolist.fragment.WorkTasksFragment

class TaskPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentActivity = fragmentActivity

//    override fun createFragment(position: Int): Fragment {
//        return when (position) {
//            0 -> AllTasksFragment(fragmentActivity)
//            1 -> PersonalTasksFragment(fragmentActivity)
//            2 -> WorkTasksFragment(fragmentActivity)
//            else -> throw IllegalStateException("Unexpected position $position")
//        }
//    }

    override fun createFragment(position: Int): Fragment {
        return GlobalVariable.fragments[position]
    }

    override fun getItemId(position: Int): Long {
        GlobalVariable.viewPagerPosition = position
        return super.getItemId(position)
    }

    override fun getItemCount(): Int = GlobalVariable.fragments.size

    fun addFragment(fragment: Fragment) {
        GlobalVariable.fragments.add(fragment)
        notifyItemInserted(GlobalVariable.fragments.size - 1)
    }
}