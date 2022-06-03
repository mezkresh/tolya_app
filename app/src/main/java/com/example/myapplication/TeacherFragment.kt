package com.example.myapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.TeacherFragmentLayoutBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TeacherFragment : Fragment(R.layout.teacher_fragment_layout) {

    private lateinit var binding: TeacherFragmentLayoutBinding

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherFragmentLayoutBinding.bind(view)
        val adapter = CustomAdapter()
        var currentLectureId = -1
        binding.users.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        binding.start.setOnClickListener {
            currentLectureId = viewModel.startNewLecture()
            adapter.submitList(listOf())
            generateBarcode(currentLectureId.toString())
            binding.start.isVisible = false
            binding.end.isVisible = true
        }

        binding.end.setOnClickListener {
            binding.end.isVisible = false
            binding.qrcode.isVisible = false
            binding.start.isVisible = true
        }
        viewModel.lecture.onEach {
            val lectures = it.filter { lecture ->
                lecture.id == currentLectureId
            }.map { it.userId }
            val list = viewModel.users.value.filter {
                it.id?.let {
                    lectures.contains(it.toString())
                } ?: false
            }.map {
                it.lastname + " " + it.firstname + " " + it.patronymic
            }
            adapter.submitList(list)
        }.launchIn(viewLifecycleOwner.lifecycleScope)


    }

    fun generateBarcode(lectureId: String) {
        val multiFormatWriter = MultiFormatWriter()

        val bitMatrix: BitMatrix =
            multiFormatWriter.encode(lectureId, BarcodeFormat.QR_CODE, 1000, 1000)
        val barcodeEncoder = BarcodeEncoder()
        val myBitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        binding.qrcode.isVisible = true
        binding.qrcode.setImageBitmap(myBitmap)
    }
}