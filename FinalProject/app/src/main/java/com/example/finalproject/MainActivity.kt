package com.example.finalproject

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

//TODO Fix this value to match package name
const val FRAG_ACTIVITY = "com.example.cs3013project2.FRAG_ACTIVITY"
const val DAY_4_ACTIVITY = "com.example.cs3013day4activity.DAY_4_ACTIVITY"
const val ACTION_COLOR = "com.example.cs3010_colorpicker.ACTION_COLOR"
const val ANIMATIONS_ACTIVITY = "com.example.animations.ANIMATIONS_ACTIVITY"
const val FLASH_ACTIVITY = "com.example.flashbeepshake.FLASH_ACTIVITY"


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
        }

        fragBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = FRAG_ACTIVITY
                type = "text/plain"
            }
            startActivityForResult(sendIntent,0)
        }

        colorPickerBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = ACTION_COLOR
                type = "text/plain"
                this.putExtra("Request Code", 0)
            }
            if(sendIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(sendIntent, 0)
            }
        }

        animationsBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = ANIMATIONS_ACTIVITY
                type = "text/plain"
            }
            startActivityForResult(sendIntent,0)
        }

        flashBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = FLASH_ACTIVITY
                type = "text/plain"
            }
            startActivityForResult(sendIntent,0)
        }
    }
}
