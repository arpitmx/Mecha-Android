package com.bitpolarity.mecha

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bitpolarity.mecha.databinding.ActivityQrscannerBinding
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

class QRScannerActivity : AppCompatActivity() {
    lateinit var binding : ActivityQrscannerBinding

    val scanQrCodeLauncher = registerForActivityResult(ScanCustomCode(), ::handleResult)


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {

        } else {
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,"android.permission.POST_NOTIFICATIONS") ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else if (shouldShowRequestPermissionRationale("android.permission.POST_NOTIFICATIONS")) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrscannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()


            scanQrCodeLauncher.launch(ScannerConfig.build {
                setOverlayStringRes(R.string.qr_label)
                setShowTorchToggle(true)
                setHapticSuccessFeedback(true)
            })



    }

    fun handleResult(result: QRResult) {

        val response = when (result) {
            is QRResult.QRSuccess -> result.content.rawValue
            QRResult.QRUserCanceled -> "Auth canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
        }

        if (result is QRResult.QRSuccess){
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MechaSelector::class.java)
            intent.putExtra("qrCode",result.content.rawValue)
            startActivity(intent)
        }
        else{
            onBackPressed()
        }

    }

}