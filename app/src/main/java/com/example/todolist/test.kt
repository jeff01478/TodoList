package com.example.todolist

class test {
    companion object {
        fun test( ){
            GlobalVariable.allTaskList.apply {
                add(Task(0, "a", "", 0, "2024", 0))
                add(Task(1, "b", "", 1, "2025", 0))
                add(Task(2, "c", "", 2, "2026", 0))
                add(Task(3, "aa", "", 0, "2027", 0))
                add(Task(4, "bb", "", 1, "2028", 0))
                add(Task(5, "cc", "", 2, "2029", 0))
            }

            GlobalVariable.personalTaskList.apply {
                add(Task(6, "d", "", 0, "2020", 1))
                add(Task(7, "e", "", 1, "2021", 1))
                add(Task(8, "f", "", 2, "2022", 1))
                add(Task(9, "dd", "", 0, "2023", 1))
                add(Task(10, "ee", "", 1, "2022", 1))
                add(Task(11, "ff", "", 2, "2024", 1))
            }

            GlobalVariable.workTaskList.apply {
                add(Task(12, "g", "", 0, "2020", 2))
                add(Task(13, "h", "", 1, "2021", 2))
                add(Task(14, "i", "", 2, "2022", 2))
                add(Task(15, "gg", "", 0, "2023", 2))
                add(Task(16, "hh", "", 1, "2022", 2))
                add(Task(17, "ii", "", 2, "2024", 2))
            }

            GlobalVariable.personalTaskList.forEach {
                GlobalVariable.allTaskList.add(it)
            }
            GlobalVariable.workTaskList.forEach {
                GlobalVariable.allTaskList.add(it)
            }
        }
    }
}