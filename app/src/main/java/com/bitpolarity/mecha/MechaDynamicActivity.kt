package com.bitpolarity.mecha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bitpolarity.mecha.databinding.ActivityMechaDynamicBinding
import org.json.JSONObject

class MechaDynamicActivity : AppCompatActivity() {

    lateinit var binding : ActivityMechaDynamicBinding
    val editBox = "EDIT_BOX"
    val button = "BUTTON"
    val type = "type"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMechaDynamicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val stringJson =  intent.getStringExtra("jsonLayoutString")
        if(stringJson!==null){
            setUpViews(stringJson)
        }else {
            Toast.makeText(this, "Execution failure", Toast.LENGTH_SHORT).show()
        }

    }

    fun setUpViews(layoutJsonString : String)
    {

        //Get
        val jsonBody = getlayoutJson(layoutJsonString)
        val linearLayout = jsonBody!!.getJSONObject("linearLayout")

        val childrenView = linearLayout.getJSONObject("children")
        val keys = childrenView.keys()

        while (keys.hasNext()){
            val key = keys.next()
            val obj = childrenView.getJSONObject(key).getJSONObject(key)
            if (obj.has("type") && obj.getString(type)==editBox){

            }
            else if (obj.has("type") && obj.getString(type)==button){

            }else {

            }


        }

        Log.d("Debug123", "jsonObject: "+jsonBody)


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
//            "dataType": "int",
//            "dataSourceID": "inputX"
//        },
//            "y": {
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


    private fun getlayoutJson(jsonString : String ): JSONObject? {
        try {
            return JSONObject(jsonString)
        } catch (e : java.lang.Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return null
        }

    }


}

