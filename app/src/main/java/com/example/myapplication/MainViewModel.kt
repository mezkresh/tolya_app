package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRepository = FirebaseRepository()
    private val localRepository = LocalRepository(application)

    private val _loginState = MutableStateFlow(State.LOADING)
    val loginState: StateFlow<State> = _loginState

    private val _currentUserState = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUserState

    val users: StateFlow<List<User>> = firebaseRepository.userDataState
    val lecture: StateFlow<List<Lecture>> = firebaseRepository.lectureDataState
    private val flowStartPolicy = SharingStarted.WhileSubscribed(5000L)
    val subjects: StateFlow<List<Subject>> =
        firebaseRepository.subjectDataState.combine(_currentUserState) { subjects, user ->
            subjects.filter { subject ->
                subject.teacherId == user?.id
            }
        }.stateIn(viewModelScope, flowStartPolicy, listOf())

    val groups = firebaseRepository.subjectGroupDataState.map { subjectGroup ->
        subjectGroup.mapNotNull { it.groupName }.toSortedSet().toList()
    }.stateIn(viewModelScope, flowStartPolicy, listOf())

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

    fun startNewLecture(SSID: String): Int {
        var maxId = 0
        firebaseRepository.lectureDataState.value.forEach {
            if (it.id != null && maxId < it.id) {
                maxId = it.id
            }
        }
        firebaseRepository.startLecture(
            Lecture(
                maxId,
                _currentUserState.value?.id?.toString() ?: throw Exception("UNAUTHORIZED"),
                SSID
            )
        )
        return (maxId + 1)
    }

    fun addVisit(lectureId: String, SSID: String): Boolean {
        val exists = firebaseRepository.lectureVisitsDataState.value.any {
            it.lectureId == lectureId.toInt() && it.userId == _currentUserState.value?.id
        }
        val lectureExist = firebaseRepository.lectureDataState.value.any {
            it.id == lectureId.toInt() && SSID == it.wifiSSID
        }
        if (!exists && lectureExist) {
            firebaseRepository.addLectureVisit(
                LectureVisit(
                    lectureId.toInt(),
                    _currentUserState.value?.id
                )
            )
        }
        return !exists && lectureExist
    }

    fun addSubject(title: String) {
        val newId = (firebaseRepository.subjectDataState.value.lastOrNull()?.id ?: 0) + 1
        firebaseRepository.addSubject(
            Subject(
                newId,
                title,
                currentUser.value?.id
            )
        )
    }

    fun getTeacherSubjectGroupList(date: Date, subjectId: Int): List<TeacherSubjectGroup> {
        return firebaseRepository.subjectGroupDataState.value.filter {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val startDate = format.parse(it.dateStart ?: return@filter false) ?: return@filter false
            val endDate = format.parse(it.dateEnd ?: return@filter false) ?: return@filter false
            date in startDate..endDate && it.subjectId == subjectId
        }.mapNotNull { subjectGroup ->
            firebaseRepository.subjectDataState.value.firstOrNull { subject ->
                subjectGroup.subjectId == subject.id
            }?.let { subject ->
                if (subject.teacherId == currentUser.value?.id) {
                    TeacherSubjectGroup(
                        subject.title,
                        subjectGroup.timeStart,
                        subjectGroup.timeEnd
                    )
                } else null
            }
        }
    }

    fun getTeacherSubjectGroupList(date: Date): List<TeacherSubjectGroup> {
        return firebaseRepository.subjectGroupDataState.value.filter {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val startDate = format.parse(it.dateStart ?: return@filter false) ?: return@filter false
            val endDate = format.parse(it.dateEnd ?: return@filter false) ?: return@filter false
            date in startDate..endDate
        }.mapNotNull { subjectGroup ->
            firebaseRepository.subjectDataState.value.firstOrNull { subject ->
                subjectGroup.subjectId == subject.id
            }?.let { subject ->
                if (subject.teacherId == currentUser.value?.id) {
                    TeacherSubjectGroup(
                        subject.title,
                        subjectGroup.timeStart,
                        subjectGroup.timeEnd
                    )
                } else null
            }
        }
    }

    fun getStudentSubjectGroupList(date: Date): List<StudentSubjectGroup> {
        val groupName = firebaseRepository.studentsState.value.firstOrNull {
            it.userId == currentUser.value?.id
        }?.group ?: return emptyList()
        return firebaseRepository.subjectGroupDataState.value.filter {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val startDate = format.parse(it.dateStart ?: return@filter false) ?: return@filter false
            val endDate = format.parse(it.dateEnd ?: return@filter false) ?: return@filter false
            date in startDate..endDate && it.groupName == groupName
        }.mapNotNull { subjectGroup ->
            firebaseRepository.subjectDataState.value.firstOrNull { subject ->
                subjectGroup.subjectId == subject.id
            }?.let { subject ->
                firebaseRepository.userDataState.value.firstOrNull { user ->
                    user.id == subject.id
                }?.let {
                    StudentSubjectGroup(
                        subject.title,
                        "${it.lastname} ${it.firstname} ${it.patronymic}",
                        subjectGroup.timeStart,
                        subjectGroup.timeEnd
                    )
                }
            }
        }
    }


    fun addSubjectGroup(subjectGroup: SubjectGroup) {
        val newId = (firebaseRepository.subjectGroupDataState.value.lastOrNull()?.id ?: 0) + 1
        firebaseRepository.addSubjectGroup(subjectGroup.copy(id = newId))

    }

    enum class State {
        LOADING, NOT_AUTHORIZED, AUTHORIZED, BAD_INPUT
    }
}