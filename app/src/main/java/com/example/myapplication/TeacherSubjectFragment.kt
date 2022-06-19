package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.myapplication.databinding.TeacherSubjectFragmentLayoutBinding

class TeacherSubjectFragment:Fragment(R.layout.teacher_subject_fragment_layout) {

    private lateinit var binding: TeacherSubjectFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()

    private var subjectId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherSubjectFragmentLayoutBinding.bind(view)
        
        binding.fab.setOnClickListener {
            val fragment = AddSubjectGroupFragment()
            fragment.arguments = bundleOf(
                "subject_id" to subjectId
            )
            parentFragmentManager.commit {
                addToBackStack("TeacherSubjectFragment")
                replace(id, fragment)
            }
        }

    }
}