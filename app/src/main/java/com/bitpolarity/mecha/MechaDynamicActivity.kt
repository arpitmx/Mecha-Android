package com.bitpolarity.mecha

import com.bitpolarity.mecha.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bitpolarity.mecha.databinding.ActivityMechaDynamicBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.EnumSet.range


class MechaDynamicActivity : AppCompatActivity() {

    lateinit var binding : ActivityMechaDynamicBinding
    val editBox = "EDIT_BOX"
    val button = "BUTTON"
    val type = "type"
    lateinit var qrCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMechaDynamicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrCode = intent.getStringExtra("qrCode").toString()


        val stringJson =  intent.getStringExtra("jsonLayoutString")
        if(stringJson!==null){
            setUpViews(stringJson)
        }else {
            Toast.makeText(this, "Execution failure", Toast.LENGTH_SHORT).show()
        }

    }

    val height = "height"
    val width = "width"
    val title = "title"
    val orientation = "orientation"
    val specs = "specs"
    //var idList = mutableMapOf<String,Int>()
    var viewList = mutableMapOf<String,View>()

//            "title": "Calculator",
//            "height": "mp",
//            "width": "mp"


    fun setUpViews(layoutJsonString : String)
    {

        //Get
        val jsonBody = JSONObject(layoutJsonString)
        Log.d("DEBUG123", "JSON LAYOUT STRING: "+layoutJsonString)
        val linearLayoutObj = jsonBody!!.getJSONObject("linearLayout")
        val linearLayoutSpecs = linearLayoutObj.getJSONObject(specs)
        val orientation = linearLayoutSpecs.getString(orientation)
        val windowTitle = linearLayoutSpecs.getString("title")
        val linearLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        supportActionBar!!.title = windowTitle

        val linearLayout = LinearLayout(this)
        val id = View.generateViewId()
        linearLayout.id = id
        linearLayout.layoutParams = linearLayoutParams

        //Orientation
        if (orientation == "Vertical"){
            linearLayout.orientation = LinearLayout.VERTICAL
        }else {
            linearLayout.orientation = LinearLayout.HORIZONTAL
        }






        val childrenView = linearLayoutObj.getJSONObject("children")

        val keys = childrenView.keys()

        while (keys.hasNext()){
            val key = keys.next()
            val obj = childrenView.getJSONObject(key).getJSONObject(key)
            if (obj.has("type") && obj.getString(type)==editBox){

                val id = View.generateViewId()
                val specs = obj.getJSONObject(specs)
                val hint = specs.getString("hint")
                val provides = specs.getString("provides")
                val editBoxLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                editBoxLayoutParams.leftMargin = 15
                editBoxLayoutParams.topMargin = 15
                editBoxLayoutParams.rightMargin = 15
                editBoxLayoutParams.bottomMargin = 15


                val editText = EditText(this)
                editText.setPadding(15,20,15,20)
                editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25f)
                editText.id = id
                editText.hint = hint
                editText.layoutParams = editBoxLayoutParams
                editText.setBackgroundResource(R.drawable.edit_textbg)
                viewList.put(provides,editText)
                linearLayout.addView(editText)

                Log.d("ID", "setUpViews: Edit text id"+id)

            }
            else if (obj.has("type") && obj.getString(type)==button){

                val id = View.generateViewId()
                val specs = obj.getJSONObject(specs)
                val hint = specs.getString("text")
                val onClick = specs.getJSONObject("onClick")
                val params = onClick.getJSONObject("params")

                val buttonLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                buttonLayoutParams.leftMargin = 15
                buttonLayoutParams.topMargin = 15
                buttonLayoutParams.rightMargin = 15
                buttonLayoutParams.bottomMargin = 15


                val button = Button(this)
                button.setPadding(15,20,15,20)
                button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25f)
                button.id = id
                button.text = hint
                button.setTextColor(Color.WHITE)
                button.layoutParams = buttonLayoutParams
                button.setBackgroundResource(R.drawable.buttonbg)
                button.setOnClickListener{
                    val keys = params.keys()
                    for (key in keys) {
                        val text = (viewList[key] as EditText).text.toString()
                        params.getJSONObject(key).put("value",text )
                    }

                    sendReq(onClick.toString())

                }


                linearLayout.addView(button)
                Log.d("ID", "setUpViews: Edit text id"+id)
            }


        }

        Log.d("Debug123", "jsonObject: "+jsonBody)
        val rootLayout = findViewById<ScrollView>(R.id.root_layout)
        rootLayout.addView(linearLayout)

//
//        "children": {
//        "inputX": {
//        "inputX": {
//        "class": "view",
//        "type": "EDIT_BOX",
//        "specs": {
//        "provides": "x"
//        "hint": "Value of X",
//        "height": "wp",
//        "width": "mp"
//    }
//    }
//    },
//        "inputY": {
//        "inputY": {
//        "class": "view",
//        "type": "EDIT_BOX",
//        "specs": {
//        "provides":"y"
//        "hint": "Value of Y",
//        "height": "wp",
//        "width": "mp"
//    }
//    }
//    },

//
//        {
//            "linearLayout": {
//            "class": "layout",
//            "type": "LinearLayout",
//            "specs": {
//            "orientation": "Vertical",
//            "title": "Calculator",
//            "height": "mp",
//            "width": "mp"
//        },
//            "children": {
//            "inputX": {
//            "inputX": {
//            "class": "view",
//            "type": "EDIT_BOX",
//            "specs": {
//            "hint": "Value of X",
//            "height": "wp",
//            "width": "mp"
//        }
//        }
//        },
//            "inputY": {
//            "inputY": {
//            "class": "view",
//            "type": "EDIT_BOX",
//            "specs": {
//            "hint": "Value of Y",
//            "height": "wp",
//            "width": "mp"
//        }
//        }
//        },
//            "btnAdd": {
//            "btnAdd": {
//            "class": "view",
//            "type": "BUTTON",
//            "specs": {
//            "text": "Add",
//            "height": "wp",
//            "width": "mp",
//            "onClick": {
//            "func": "sum",
//            "params": {
//            "x": {
//            "value": null,
//            "dataType": "int",
//            "dataSourceID": "inputX"
//        },
//            "y": {
//            "value": null,
//            "dataType": "int",
//            "dataSourceID": "inputY"
//        }
//        }
//        }
//        }
//        }
//        }
//        }
//        }
//        }

    }


    fun sendReq(body : String) {

        Thread {
            val client = OkHttpClient()
            val url = "http://${qrCode}.ngrok.io/call"
            val requestBody = body.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()
            val responseCode = response.code
            val responseBody = response.body?.string()
            runOnUiThread{
                if (responseCode == 200){
                    Toast.makeText(this, "Executed", Toast.LENGTH_SHORT).show()

                }else {
                    Toast.makeText(this, "Failure ${responseCode}", Toast.LENGTH_SHORT).show()

                }
            }
            Log.d("Upload123", "responsecode : ${responseCode}  sendScript: " + responseBody)
        }.start()



    }

    private fun getlayoutJson(jsonString : String ): JSONObject? {
        try {
            return JSONObject(jsonString)
        } catch (e : java.lang.Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return null
        }

    }


}

