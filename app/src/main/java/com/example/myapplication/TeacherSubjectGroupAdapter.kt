package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TeacherSubjectGroup>() {
    override fun areItemsTheSame(
        oldItem: TeacherSubjectGroup,
        newItem: TeacherSubjectGroup
    ): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(
        oldItem: TeacherSubjectGroup,
        newItem: TeacherSubjectGroup
    ): Boolean {
        return oldItem == newItem
    }
}

class TeacherSubjectGroupAdapter(private val onClick: () -> Unit, var chosenDate: Date) :
    ListAdapter<TeacherSubjectGroup, TeacherSubjectGroupAdapter.ViewHolder>(DIFF_CALLBACK) {
    private val format = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonItemView: Button = view.findViewById(R.id.subject_name)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.student_subject_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.buttonItemView.text =
            "${item.subjectName} : ${item.startTime}-${item.endTime}"
        val chosenDate = Calendar.getInstance().apply {
            time = chosenDate
        }
        val startTime = Calendar.getInstance().apply {
            time = item.startTime?.let { format.parse(it) }!!
        }
        val endTime = Calendar.getInstance().apply {
            time = item.endTime?.let { format.parse(it) }!!
        }
        val currentDate = Calendar.getInstance()
        val currentMinutes = currentDate.get(Calendar.HOUR) * 60 + currentDate.get(Calendar.MINUTE)
        val startTimeMinutes = startTime.get(Calendar.HOUR) * 60 + startTime.get(Calendar.MINUTE)
        val endTimeMinutes = endTime.get(Calendar.HOUR) * 60 + endTime.get(Calendar.MINUTE)
        if (chosenDate.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR)
            && chosenDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)
            && currentMinutes >= startTimeMinutes - 10 && currentMinutes < endTimeMinutes
        ) {
            viewHolder.buttonItemView.setBackgroundColor(
                viewHolder.itemView.context.resources.getColor(
                    R.color.green
                )
            )
            viewHolder.buttonItemView.setOnClickListener {
                onClick()
            }
        } else {
            viewHolder.buttonItemView.setBackgroundColor(
                viewHolder.itemView.context.resources.getColor(
                    com.google.android.material.R.color.mtrl_on_primary_text_btn_text_color_selector
                )
            )

        }
    }
}
