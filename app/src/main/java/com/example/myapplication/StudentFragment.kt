package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.StudentFragmentLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class StudentFragment : Fragment(R.layout.student_fragment_layout) {

    private lateinit var binding: StudentFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()
    private val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = StudentFragmentLayoutBinding.bind(view)
        val tracker = LocationTracker(requireContext())

        val adapter = StudentSubjectGroupAdapter({
            if (Constants.stayedOnCampus) {
                val fragment = StudentAttendanceFragment()
                parentFragmentManager.commit {
                    addToBackStack("StudentFragment")
                    replace(id, fragment)
                }
            } else {
                Toast.makeText(requireContext(), "Вы находитесь не в универе", Toast.LENGTH_LONG)
                    .show()
            }
        }, Calendar.getInstance().time)
        binding.subjectList.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = format.parse("${dayOfMonth}.${month + 1}.${year}")
            date?.let {
                adapter.submitList(viewModel.getStudentSubjectGroupList(it))
                adapter.chosenDate = it
            }
        }
        adapter.submitList(viewModel.getStudentSubjectGroupList(Calendar.getInstance().time))
    }
}