package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.AddSubjectGroupFragmentLayoutBinding
import java.util.*

class AddSubjectGroupFragment : Fragment(R.layout.add_subject_group_fragment_layout) {

    private lateinit var binding: AddSubjectGroupFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()
    private var subjectId: Int = -1
    private val dayOfWeeks = mutableListOf<Int>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        subjectId = requireArguments().getInt("subject_id")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddSubjectGroupFragmentLayoutBinding.bind(view)
        bindPicker(binding.startTime, ::showTimePicker)
        bindPicker(binding.endTime, ::showTimePicker)
        bindPicker(binding.startDate, ::showDatePicker)
        bindPicker(binding.endDate, ::showDatePicker)

        binding.saveButton.setOnClickListener {
            viewModel.addSubjectGroup(SubjectGroup(
                -1,
                subjectId,
                binding.group.text.toString(),
                binding.startTime.text.toString(),
                binding.endTime.text.toString(),
                dayOfWeeks.joinToString(),
                binding.startDate.text.toString(),
                binding.endDate.text.toString(),
                if(binding.oneWeek.isChecked) 0 else if (binding.twoWeek.isChecked) 1 else -1
                ))
            parentFragmentManager.popBackStack()
        }

    }

    private fun bindPicker(editText: EditText, func: (v: EditText)->Unit){
        editText.setOnClickListener {
            func(it as EditText)
        }
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePicker(v as EditText)
            }
        }
    }

    private fun showTimePicker(view: EditText) {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute -> view.setText("$hourOfDay:$minute") },
            0,
            0,
            true
        ).show()
    }

    private fun showDatePicker(view: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                view.setText("${dayOfMonth + 1}.${month + 1}.${year}")
            }, year, month, day
        ).show()
    }

}