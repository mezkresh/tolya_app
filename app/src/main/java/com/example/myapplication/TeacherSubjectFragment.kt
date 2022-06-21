package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.TeacherSubjectFragmentLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class TeacherSubjectFragment : Fragment(R.layout.teacher_subject_fragment_layout) {

    private lateinit var binding: TeacherSubjectFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    private var subjectId: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        subjectId = requireArguments().getInt("subject_id")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherSubjectFragmentLayoutBinding.bind(view)
        val adapter = TeacherSubjectGroupAdapter({
            val fragment = TeacherCurrentLectureFragment()
            parentFragmentManager.commit {
                addToBackStack("TeacherSubjectFragment")
                replace(id, fragment)
            }
        }, Calendar.getInstance().time)
        binding.subjectList.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = format.parse("${dayOfMonth}.${month+1}.${year}")
            date?.let {
                adapter.submitList(viewModel.getTeacherSubjectGroupList(it, subjectId))
                adapter.chosenDate = it
            }
        }
        adapter.submitList(
            viewModel.getTeacherSubjectGroupList(
                Calendar.getInstance().time,
                subjectId
            )
        )
        binding.fab.setOnClickListener {
            val fragment = AddSubjectGroupFragment().apply {
                arguments = bundleOf(
                    "subject_id" to subjectId
                )
            }
            parentFragmentManager.commit {
                addToBackStack("TeacherSubjectFragment")
                replace(id, fragment)
            }
        }
    }
}