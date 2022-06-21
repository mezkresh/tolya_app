package com.example.myapplication

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog


class MultiSpinner : androidx.appcompat.widget.AppCompatSpinner, OnMultiChoiceClickListener,
    DialogInterface.OnCancelListener {
    private var items: ArrayList<String> = arrayListOf()
    private var selected: BooleanArray = BooleanArray(0)
    private var defaultText: String = ""
    private var allText: String = "Все выбрано"
    private var listener: MultiSpinnerListener? = null

    constructor(context: Context) : super(context)
    constructor(arg0: Context, arg1: AttributeSet?) : super(arg0, arg1)
    constructor(arg0: Context, arg1: AttributeSet?, arg2: Int) : super(arg0, arg1, arg2)

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        selected[which] = isChecked
    }

    override fun onCancel(dialog: DialogInterface?) {
        // refresh text on spinner
        val spinnerBuffer = StringBuffer()
        var someSelected = false
        var countSelected = 0
        for (i in items.indices) {
            if (selected[i]) {
                spinnerBuffer.append(items[i])
                spinnerBuffer.append(", ")
                someSelected = true
                countSelected++
            }
        }
        var spinnerText: String?
        if (someSelected) {
            if (countSelected == items.size) {
                spinnerText = allText
            } else {
                spinnerText = spinnerBuffer.toString()
                if (spinnerText.length > 2) spinnerText =
                    spinnerText.substring(0, spinnerText.length - 2)
            }
        } else {
            spinnerText = defaultText
        }
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item, arrayOf(spinnerText)
        )
        setAdapter(adapter)
        listener?.onItemsSelected(selected)
    }

    override fun performClick(): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(
            items.toTypedArray(), selected, this
        )
        builder.setPositiveButton(
            android.R.string.ok
        ) { dialog, which ->
            dialog.cancel()
        }
        builder.setOnCancelListener(this)
        builder.show()
        return true
    }

    fun setItems(
        items: List<String>, allText: String,
        listener: MultiSpinnerListener
    ) {
        this.items = ArrayList(items)
        this.allText = allText
        this.listener = listener

        // all selected by default
        selected = BooleanArray(items.size)
        for (i in selected.indices) selected[i] = false

        // all text on the spinner
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item, arrayOf(defaultText)
        )
        setAdapter(adapter)
    }

    interface MultiSpinnerListener {
        fun onItemsSelected(selected: BooleanArray)
    }
}