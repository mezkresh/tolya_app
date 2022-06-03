package com.example.myapplication

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FirebaseRepository() {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference =
        firebaseDatabase.reference
    private val _studentsState = MutableStateFlow<List<Student>>(arrayListOf())
    val studentsState: StateFlow<List<Student>> = _studentsState

    private val _teachersState = MutableStateFlow<List<Teacher>>(arrayListOf())
    val teachersState: StateFlow<List<Teacher>> = _teachersState

    private val _lecturesDataState = MutableStateFlow<List<Lecture>>(arrayListOf())
    val lectureDataState: StateFlow<List<Lecture>> = _lecturesDataState

    val _usersDataSate = MutableStateFlow<List<User>>(arrayListOf())

    init {

        val studentData: DatabaseReference = databaseReference.child("Student Data")
        val teacherData: DatabaseReference = databaseReference.child("Teacher Data")
        val lectures: DatabaseReference = databaseReference.child("Lectures")
        val attendanceRegister: DatabaseReference = databaseReference.child("Attendance Register")
        val userData: DatabaseReference = databaseReference.child("User Data")

        studentData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _studentsState.value = snapshot.children.mapNotNull {
                    it.getValue(Student::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("students:onCancelled", error.toException())
            }
        })

        teacherData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _teachersState.value = snapshot.children.mapNotNull {
                    it.getValue(Teacher::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("teachers:onCancelled", error.toException())
            }
        })
        lectures.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _lecturesDataState.value = snapshot.children.mapNotNull {
                    it.getValue(Lecture::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        attendanceRegister.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        userData.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _usersDataSate.value = snapshot.children.mapNotNull {
                    it.getValue(User::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun login(username: String, password: String): User? {
        return _usersDataSate.value.firstOrNull {
            it.username == username && it.password == password
        }
    }

    fun isUsernameExists(username: String): Boolean {
        return !_usersDataSate.value.none { it.username == username }
    }

    fun register(user: User): User {
        val firebaseDatabase: FirebaseDatabase = Firebase.database
        val databaseReference: DatabaseReference =
            firebaseDatabase.reference
        val userData: DatabaseReference = databaseReference.child("User Data")
        val newUserId = (_usersDataSate.value.lastOrNull()?.id ?: 0) + 1
        val newUser = user.copy(id = newUserId)
        userData.child(newUserId.toString()).setValue(newUser)
        return  newUser
    }

    fun attachUserToGroup(user: User, group: String) {
        val userId = user.id ?: _usersDataSate.value.findLast { it.username == user.username }!!.id!!
        val studentData = databaseReference.child("Student Data")
        studentData.child(userId.toString()).setValue(Student(userId, group))
    }

    fun addLecture(lecture: Lecture){
        val firebaseDatabase: FirebaseDatabase = Firebase.database
        val databaseReference: DatabaseReference =
            firebaseDatabase.reference
        val lectureData: DatabaseReference = databaseReference.child("Lectures")
        val newUserId = (_lecturesDataState.value.size) + 1
        lectureData.child(newUserId.toString()).setValue(lecture)
    }


}