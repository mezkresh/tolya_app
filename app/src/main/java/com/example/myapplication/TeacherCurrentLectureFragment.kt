package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.TeacherCurrentLectureFragmentLayoutBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.IOException


class TeacherCurrentLectureFragment : Fragment(R.layout.teacher_current_lecture_fragment_layout) {

    private lateinit var binding: TeacherCurrentLectureFragmentLayoutBinding

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TeacherCurrentLectureFragmentLayoutBinding.bind(view)
        val adapter = LectureVisitAdapter()
        var currentLectureId = -1
        binding.users.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        binding.start.setOnClickListener {
            try {
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val ssid = getWifiSSID()
                    ssid?.let {
                        currentLectureId = viewModel.startNewLecture(it)
                        adapter.submitList(listOf())
                        generateBarcode(currentLectureId.toString())
                        binding.start.isVisible = false
                        binding.end.isVisible = true
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            "???????????? ???? ???? ???????????????????? ?? ?????????? ?????????????? WIFI",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        202
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

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

    private fun getWifiSSID(): String? {
        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

        val wifiInfo: WifiInfo = wifiManager!!.connectionInfo
        return if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            wifiInfo.ssid
        } else null

    }

    private fun generateBarcode(lectureId: String) {
        val multiFormatWriter = MultiFormatWriter()

        val bitMatrix: BitMatrix =
            multiFormatWriter.encode(lectureId, BarcodeFormat.QR_CODE, 1000, 1000)
        val barcodeEncoder = BarcodeEncoder()
        val myBitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        binding.qrcode.isVisible = true
        binding.qrcode.setImageBitmap(myBitmap)
    }
}