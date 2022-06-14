package com.example.myapplication

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
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

    private val _lectureVisitsDataState = MutableStateFlow<List<LectureVisit>>(arrayListOf())
    val lectureVisitsDataState: StateFlow<List<LectureVisit>> = _lectureVisitsDataState

    private val _usersDataSate = MutableStateFlow<List<User>>(arrayListOf())
    val userDataState: StateFlow<List<User>> = _usersDataSate

    private val _subjectsDataState = MutableStateFlow<List<Subject>>(arrayListOf())
    val subjectDataState: StateFlow<List<Subject>> = _subjectsDataState

    init {

        val studentData: DatabaseReference = databaseReference.child("Student Data")
        val teacherData: DatabaseReference = databaseReference.child("Teacher Data")
        val lectures: DatabaseReference = databaseReference.child("Lectures")
        val attendanceRegister: DatabaseReference = databaseReference.child("Attendance Register")
        val userData: DatabaseReference = databaseReference.child("User Data")
        val visits: DatabaseReference = databaseReference.child("LectureVisits")
        val subjects: DatabaseReference = databaseReference.child("Subjects")
        val subjectGroup: DatabaseReference = databaseReference.child("SubjectsGroup")
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
        visits.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _lectureVisitsDataState.value = snapshot.children.mapNotNull {
                    it.getValue(LectureVisit::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        subjects.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _subjectsDataState.value = snapshot.children.mapNotNull {
                    it.getValue(Subject::class.java)
                }.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        subjectGroup.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
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
        return newUser
    }

    fun attachUserToGroup(user: User, group: String) {
        val userId =
            user.id ?: _usersDataSate.value.findLast { it.username == user.username }!!.id!!
        val studentData = databaseReference.child("Student Data")
        studentData.child(userId.toString()).setValue(Student(userId, group))
    }

    fun startLecture(lecture: Lecture) {
        val firebaseDatabase = Firebase.database
        val databaseReference = firebaseDatabase.reference
        val lectureData: DatabaseReference = databaseReference.child("Lectures")
        lectureData.child(lecture.id.toString()).setValue(lecture)
    }

    fun addLectureVisit(lecture: LectureVisit) {
        val firebaseDatabase: FirebaseDatabase = Firebase.database
        val databaseReference: DatabaseReference =
            firebaseDatabase.reference
        val lectureData: DatabaseReference = databaseReference.child("LectureVisits")
        val newEntryId = (_lectureVisitsDataState.value.size) + 1
        lectureData.child(newEntryId.toString()).setValue(lecture)
    }

    fun addSubject(subject: Subject) {
        val firebaseDatabase: FirebaseDatabase = Firebase.database
        val databaseReference: DatabaseReference =
            firebaseDatabase.reference
        val lectureData: DatabaseReference = databaseReference.child("Subjects")
        val newEntryId = (_subjectsDataState.value.size) + 1
        lectureData.child(newEntryId.toString()).setValue(subject)
    }


}