package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Student(
    val userId: Int,
    val group: String
)

data class StudentSubjectGroup(
    val subjectName: String?,
    val teacherName: String?,
    val startTime: String?,
    val endTime: String?
)

data class TeacherSubjectGroup(
    val subjectName: String?,
    val startTime: String?,
    val endTime: String?
)