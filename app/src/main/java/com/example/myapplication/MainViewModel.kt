package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepository = FirebaseRepository()
    private val localRepository = LocalRepository(application)

    private val _loginState = MutableStateFlow(State.LOADING)
    val loginState: StateFlow<State> = _loginState

    private val _currentUserState = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUserState

    val users: StateFlow<List<User>> = firebaseRepository._usersDataSate
    val lecture: StateFlow<List<Lecture>> = firebaseRepository.lectureDataState
    private val flowStartPolicy = SharingStarted.WhileSubscribed(5000L)
//
//    val groups = firebaseRepository.lectureDataState.mapLatest { lectures ->
//        lectures.map { it.group }.toSortedSet().toList()
//    }.stateIn(viewModelScope, flowStartPolicy, listOf())

    init {
        viewModelScope.launch {
            val user = null //localRepository.getUserData()
            user?.let {
                _currentUserState.value = it
                _loginState.value = State.AUTHORIZED
            } ?: run {
                _loginState.value = State.NOT_AUTHORIZED
            }
        }
    }

    fun register(user: User): Boolean {
        val userNameExists = firebaseRepository.isUsernameExists(user.username!!)
        if (!userNameExists) {
            viewModelScope.launch {
                val registeredUser = firebaseRepository.register(user)
                localRepository.saveUserData(registeredUser)
            }
        }
        return !userNameExists
    }

    fun attachUserToGroup(user: User, group: String) {
        viewModelScope.launch {
            firebaseRepository.attachUserToGroup(user, group)
        }
    }

    fun login(username: String, password: String) {
        _loginState.value = State.LOADING
        val user = firebaseRepository.login(username, password)
        user?.let {
            _currentUserState.value = it
            _loginState.value = State.AUTHORIZED
            viewModelScope.launch {
                localRepository.saveUserData(it)
            }
        } ?: run {
            _loginState.value = State.BAD_INPUT
        }
    }

    fun startNewLecture(): Int {
        var maxId = 0
        firebaseRepository.lectureDataState.value.forEach {
            if (it.id != null && maxId < it.id) {
                maxId = it.id
            }
        }
        return (maxId + 1)
    }

    fun addLecture(lectureId: String): Boolean {
        val exists = firebaseRepository.lectureDataState.value.any {
            it.id == lectureId.toInt() && it.userId == _currentUserState.value?.id?.toString()
        }
        if (!exists) {
            firebaseRepository.addLecture(
                Lecture(
                    lectureId.toInt(),
                    _currentUserState.value?.id?.toString()
                )
            )
        }
        return !exists
    }

    enum class State {
        LOADING, NOT_AUTHORIZED, AUTHORIZED, BAD_INPUT
    }
}