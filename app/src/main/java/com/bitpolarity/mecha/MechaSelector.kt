package com.bitpolarity.mecha

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.BundleCompat
import com.bitpolarity.mecha.databinding.ActivityMainBinding
import com.bitpolarity.mecha.databinding.ActivityMechaSelectorBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class MechaSelector : AppCompatActivity() {

    lateinit var binding : ActivityMechaSelectorBinding
    val READ_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMechaSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val qrCode = intent.getStringExtra("qrCode")
        binding.connectionTV.text = qrCode.toString()



        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }



        binding.selectBtn.setOnClickListener{

            startActivityForResult(intent, READ_REQUEST_CODE)
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->

                val fileName = uri.lastPathSegment
                binding.fileName.text = fileName
                binding.executeBtn.visibility = View.VISIBLE
                binding.executeBtn.setOnClickListener {
                    execute(uri)
                }

            }
        }
        else {

            binding.executeBtn.visibility = View.GONE

        }
    }

    fun execute(uri : Uri){

        val inputStream = contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = null
        while ({ line = reader.readLine(); line }() != null) {
            stringBuilder.append(line)
        }
        reader.close()
        inputStream!!.close()
        val mechaArray = stringBuilder.toString().split("######-xDONOTEDITx-#####")
        val layoutJsonString = mechaArray[1]
        val pythonScript = mechaArray[0]

        val intent = Intent(this, MechaDynamicActivity::class.java)
        intent.putExtra("jsonLayoutString",layoutJsonString)
        //binding.fileName.text = pythonScript
        startActivity(intent)
    }








}