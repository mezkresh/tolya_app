package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.myapplication.databinding.AddSubjectDialogBinding

class AddSubjectDialogFragment : DialogFragment(R.layout.add_subject_dialog) {
    private lateinit var binding: AddSubjectDialogBinding
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddSubjectDialogBinding.bind(view)
        binding.ok.setOnClickListener {
            viewModel.addSubject(binding.subjectTitle.text.toString())
            dismiss()
        }
    }
}