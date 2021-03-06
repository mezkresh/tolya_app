package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.StudentAttendanceFragmentLayoutBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.runBlocking
import java.io.IOException

class StudentAttendanceFragment : Fragment(R.layout.student_attendance_fragment_layout) {

    lateinit var binding: StudentAttendanceFragmentLayoutBinding
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 201

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = StudentAttendanceFragmentLayoutBinding.bind(view)
        val ssid = getWifiSSID()
        if (ssid != null) {

            val barcodeDetector = BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()

            val cameraSource = CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build()

            binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraSource.start(binding.surfaceView.holder)
                        } else {
                            ActivityCompat.requestPermissions(
                                requireActivity(),
                                arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_CAMERA_PERMISSION
                            )
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraSource.stop()
                }
            })

            barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
                override fun release() {
                }

                override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                    runBlocking(viewLifecycleOwner.lifecycleScope.coroutineContext) {
                        val barcodes = detections.detectedItems
                        if (barcodes.size() != 0) {
                            val scanned = barcodes.valueAt(0).displayValue
                            if (viewModel.addVisit(scanned, ssid)) {
                                Toast.makeText(
                                    requireContext(),
                                    "???? ?????????????? ????????????????????",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "???? ?????? ???????????????? ???? ?????? ???????????? ?????? ???????????????????? ?? ???????????? WIFI ????????",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            })
        } else {
            Toast.makeText(
                requireContext(),
                "???????????? ???? ???? ???????????????????????? ?? ???????? WIFI",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getWifiSSID(): String? {
        val wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?

        val wifiInfo: WifiInfo = wifiManager!!.connectionInfo
        return if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            wifiInfo.ssid
        } else null

    }
}