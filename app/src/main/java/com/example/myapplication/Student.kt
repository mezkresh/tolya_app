package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Student(
    val userId: Int,
    val group: String
)