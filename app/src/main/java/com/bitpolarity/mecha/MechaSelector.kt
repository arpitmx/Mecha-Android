package com.bitpolarity.mecha

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.bitpolarity.mecha.databinding.ActivityMechaSelectorBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.log

class MechaSelector : AppCompatActivity() {

    lateinit var binding : ActivityMechaSelectorBinding
    val READ_REQUEST_CODE = 100
    lateinit var qrCode : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMechaSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrCode = intent.getStringExtra("qrCode").toString()
        //qrCode = "7a60-157-39-227-60"
        binding.connectionTV.text = qrCode.toString()



        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }



        binding.selectBtn.setOnClickListener{

            startActivityForResult(intent, READ_REQUEST_CODE)
        }


    }

    fun getFileName(uri: Uri):String{
        val fileUri = uri
        val fileName: String

        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val cursor = this.contentResolver.query(fileUri, projection, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            fileName = cursor.getString(columnIndex)
            cursor.close()
            return fileName
        } else {
            cursor!!.close()
            return  ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->

                val fileName = getFileName(uri)
                val file = File(uri.path)
                binding.fileName.text = "Mecha blueprint name : "+fileName
                binding.executeBtn.visibility = View.VISIBLE

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



                binding.executeBtn.setOnClickListener {
                    execute(mechaArray)
                }

            }
        }
        else {

            binding.executeBtn.visibility = View.GONE

        }
    }

    fun execute(mechaArray: List<String>){


        val layoutJsonString = mechaArray[1]
        val pythonScript = mechaArray[0]
        sendScript(pythonScript,layoutJsonString)






    }



    fun sendScript(script : String, jsonLayout: String) : Int {
        var fileUploadedSuccess = 0

//
//          Thread {
//
//              val url = URL("http://${qrCode}.ngrok.io/uploadFile")
//              val connection = url.openConnection() as HttpURLConnection
//              connection.requestMethod = "POST"
//              connection.doOutput = true
//
//              val body = script
//              val outputStream = connection.outputStream
//              outputStream.write(body.toByteArray())
//              outputStream.close()
//
//              val responseCode = connection.responseCode
//              if (responseCode == HttpURLConnection.HTTP_OK) {
//                  fileUploadedSuccess = true
//              } else {
//                  Log.d("Upload errror", "sendScript: ${connection.errorStream}")
//                  binding.uploadStatusSuccessTV.text = "Blueprint Upload Failed " + responseCode
//                  fileUploadedSuccess = false
//              }
//          }.start()
//        return fileUploadedSuccess

        Thread {
    val client = OkHttpClient()
    val url = "http://${qrCode}.ngrok.io/uploadFile"
    val body = script
    val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), body)
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()
    val response = client.newCall(request).execute()
    val responseCode = response.code
    val responseBody = response.body?.string()
    runOnUiThread{
        if (responseCode == 200){
            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()

            binding.uploadStatusSuccessTV.text = "Blueprint Upload Success"
            binding.uploadStatusSuccessTV.visibility = View.VISIBLE

            val intent = Intent(this, MechaDynamicActivity::class.java)
            intent.putExtra("jsonLayoutString",jsonLayout)
            Log.d("DEBUG123", "JSON LAYOUT STRING: "+jsonLayout)
            intent.putExtra("qrCode",qrCode)
            //binding.fileName.text = pythonScript
            startActivity(intent)
        }else {
            Toast.makeText(this, "Upload failed ${responseCode}", Toast.LENGTH_SHORT).show()

        }
    }
            Log.d("Upload123", "responsecode : ${responseCode}  sendScript: " + responseBody)
    }.start()


        return fileUploadedSuccess.toInt()
    }





}