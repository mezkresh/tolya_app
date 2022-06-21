package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.RegisterFragmentLayoutBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*

class RegisterFragment : Fragment(R.layout.register_fragment_layout) {

    private lateinit var binding: RegisterFragmentLayoutBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    private val currentGroupList = MutableStateFlow(mutableListOf<String>())
    private var currentGroup = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RegisterFragmentLayoutBinding.bind(view)
        binding.userType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.student -> {
                    binding.studentData.isVisible = true
                    binding.teacherData.isVisible = false
                }
                R.id.teacher -> {
                    binding.studentData.isVisible = false
                    binding.teacherData.isVisible = true
                }
                else -> {
                    binding.studentData.isVisible = false
                    binding.teacherData.isVisible = false
                }
            }
        }
        val adapter = ArrayAdapter<String>(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )
        binding.student.isChecked = true
        binding.group.adapter = adapter
        binding.group.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentGroup = position
                binding.group.getItemAtPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        mainViewModel.groups.onEach {
            val items = listOf("Выбрать группу...") + it
            adapter.clear()
            adapter.addAll(items)
            currentGroupList.value.clear()
            currentGroupList.value.addAll(items)
        }.launchIn(viewLifecycleOwner.lifecycleScope)


        binding.register.setOnClickListener {
            val userType = when (binding.userType.checkedRadioButtonId) {
                R.id.student -> 0
                R.id.teacher -> 1
                else -> -1
            }
            var allDataCorrect = true
            if (binding.username.text.toString().isEmpty()) {
                allDataCorrect = false
            }
            if (binding.password.text.toString().isEmpty()) {
                allDataCorrect = false
            }
            if (binding.firstname.text.toString().isEmpty()) {
                allDataCorrect = false
            }
            if (binding.lastname.text.toString().isEmpty()) {
                allDataCorrect = false
            }
            if (binding.patronymic.text.toString().isEmpty()) {
                allDataCorrect = false
            }
            if (currentGroup == -1 && currentGroup == 0 && userType == 0) {
                allDataCorrect = false
            }
            binding.wrongData.isVisible = !allDataCorrect

            if (allDataCorrect) {

                val user = User(
                    username = binding.username.text.toString(),
                    password = binding.password.text.toString(),
                    firstname = binding.firstname.text.toString(),
                    lastname = binding.lastname.text.toString(),
                    patronymic = binding.lastname.text.toString(),
                    type = userType
                )
                val registered = mainViewModel.register(user)
                if (!registered) {
                    binding.wrongData.isVisible = true
                } else {
                    if (userType == 0) {
                        mainViewModel.attachUserToGroup(
                            user,
                            binding.group.getItemAtPosition(currentGroup) as String
                        )
                        val fragment = StudentFragment()
                        parentFragmentManager.commit {
                            replace(id, fragment)
                        }

                    } else {
                        val fragment = TeacherFragment()
                        parentFragmentManager.commit {
                            replace(id, fragment)
                        }
                    }
                }
            }
        }
    }
}