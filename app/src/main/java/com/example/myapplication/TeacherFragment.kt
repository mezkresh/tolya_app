package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.myapplication.databinding.TeacherFragmentLayoutBinding

class TeacherFragment : Fragment(R.layout.teacher_fragment_layout) {

    private lateinit var binding: TeacherFragmentLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherFragmentLayoutBinding.bind(view)
        binding.mySubjects.setOnClickListener {
            val fragment = TeacherSubjectListFragment()
            parentFragmentManager.commit {
                addToBackStack("TeacherFragment")
                replace(id, fragment)
            }
        }

    }

}