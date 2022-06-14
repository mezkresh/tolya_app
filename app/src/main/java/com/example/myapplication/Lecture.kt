package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Lecture(
    val id: Int? = null,
    val userId: String? = null,
    val wifiSSID: String? = null
)

@IgnoreExtraProperties
data class LectureVisit(
    val lectureId: Int? = null,
    val userId: Int? = null
)

@IgnoreExtraProperties
data class Subject(
    val id: Int? = null,
    val title: String? = null,
    val teacherId: Int? = null,
)

@IgnoreExtraProperties
data class SubjectGroup(
    val id: Int? = null,
    val groupId: Int? = null,
    val timeStart: String? = null,
    val timeEnd: String? = null
)