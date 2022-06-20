package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.databinding.TeacherFragmentLayoutBinding

class TeacherFragment : Fragment(R.layout.teacher_fragment_layout) {

    private lateinit var binding: TeacherFragmentLayoutBinding
    private lateinit var tabsAdapter: TeacherTabsAdapter
    private lateinit var viewPager: ViewPager2



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherFragmentLayoutBinding.bind(view)
        tabsAdapter = TeacherTabsAdapter(this) { fragment ->
            parentFragmentManager.commit {
                addToBackStack("TeacherFragment")
                replace(id, fragment)
            }
        }
        viewPager = binding.pager
        viewPager.adapter = tabsAdapter
    }

}

class TeacherTabsAdapter(fragment: Fragment, private val navigate: (fragment: Fragment)-> Unit) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> {
                TeacherTimetableFragment()
            }
            1 ->{
                TeacherSubjectListFragment(navigate)
            }
            else -> {
                //Never happens
                throw Exception()
            }
        }
}
