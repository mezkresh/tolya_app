package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Lecture(
    val id: Int? = null,
    val userId: String? = null
)

@IgnoreExtraProperties
data class Subject(
    val id: Int? = null,
    val name: String? = null,
    val teacherId: Int? = null,
)

@IgnoreExtraProperties
data class SubjectGroup(
    val id: Int? = null,
    val timeStart: String? = null,
    val timeEnd: String? = null
)