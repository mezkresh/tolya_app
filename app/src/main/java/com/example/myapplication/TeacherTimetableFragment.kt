package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.TeacherSubjectFragmentLayoutBinding
import com.example.myapplication.databinding.TeacherTimetableFragmentLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class TeacherTimetableFragment: Fragment(R.layout.teacher_timetable_fragment_layout) {

    private lateinit var binding: TeacherTimetableFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherTimetableFragmentLayoutBinding.bind(view)
        val adapter = TeacherSubjectGroupAdapter({
            val fragment = TeacherCurrentLectureFragment()
            parentFragmentManager.commit {
                addToBackStack("TeacherTimetableFragment")
                replace(id, fragment)
            }
        }, Calendar.getInstance().time)
        binding.subjectList.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = format.parse("${dayOfMonth}.${month}.${year}")
            date?.let {
                adapter.submitList(viewModel.getTeacherSubjectGroupList(it))
                adapter.chosenDate = it
            }
        }
        adapter.submitList(viewModel.getTeacherSubjectGroupList(Calendar.getInstance().time))
    }
}