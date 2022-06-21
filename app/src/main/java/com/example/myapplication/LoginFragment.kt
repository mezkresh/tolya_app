package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.myapplication.databinding.LoginFragmentLayoutBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment: Fragment(R.layout.login_fragment_layout) {

    private lateinit var binding: LoginFragmentLayoutBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentLayoutBinding.bind(view)
        mainViewModel.loginState.onEach {
            when(it){
                MainViewModel.State.LOADING -> {
                    binding.loginProgress.isVisible = true
                }
                MainViewModel.State.AUTHORIZED -> {
                    mainViewModel.currentUser.value?.let{ user->
                        if(user.type == 0){
                            val fragment = StudentFragment()
                            parentFragmentManager.commit {
                                replace(id, fragment)
                            }
                        }else{
                            val fragment = TeacherFragment()
                            parentFragmentManager.commit {
                                replace(id, fragment)
                            }                        }
                    }
                }
                MainViewModel.State.NOT_AUTHORIZED -> {
                    binding.loginProgress.isVisible = false
                }
                MainViewModel.State.BAD_INPUT -> {
                    binding.loginProgress.isVisible = false
                    binding.badInput.isVisible = true
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
        binding.login.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            mainViewModel.login(username,password)
        }

        binding.register.setOnClickListener {
            val fragment = RegisterFragment()
            parentFragmentManager.commit {
                addToBackStack("Register")
                replace(id, fragment)
            }
        }
    }
}