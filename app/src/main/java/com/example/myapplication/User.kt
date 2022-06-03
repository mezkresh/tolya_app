package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    val id: Int? = null,
    val username: String? = null,
    val password: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val patronymic: String? = null,
    //TODO maybe string and cast to enum?
    val type: Int? = null
): Serializable
