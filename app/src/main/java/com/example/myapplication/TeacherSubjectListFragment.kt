package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.TeacherSubjectListFragmentLayoutBinding
import kotlinx.coroutines.flow.onEach

class TeacherSubjectListFragment(private val navigate: (fragment: Fragment) -> Unit) : Fragment(R.layout.teacher_subject_list_fragment_layout) {

    private lateinit var binding: TeacherSubjectListFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherSubjectListFragmentLayoutBinding.bind(view)

        binding.fab.setOnClickListener {
            AddSubjectDialogFragment().show(childFragmentManager, "ADD_SUBJECT")
        }

        val adapter = TeacherSubjectAdapter {
            val fragment = TeacherSubjectFragment().apply {
                arguments = bundleOf(
                    "subject_id" to it.id
                )
            }
            navigate(fragment)
        }
        binding.subjectList.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        viewModel.subjects.onEach {
            adapter.submitList(it)
        }
    }
}