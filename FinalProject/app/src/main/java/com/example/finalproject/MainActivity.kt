package com.example.finalproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

//TODO Fix this value to match package name
const val DAY_4_ACTIVITY = "com.example.cs3013day4activity.DAY_4_ACTIVITY"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        day4Btn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = DAY_4_ACTIVITY
                type = "text/plain"
            }
                startActivityForResult(sendIntent,0)
                //onActivityResult(0, Activity.RESULT_OK, sendIntent)
        }
    }
}
