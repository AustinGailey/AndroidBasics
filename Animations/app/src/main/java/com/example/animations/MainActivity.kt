//This app demonstrates various ways of animating movement and color change
//Written by Aaron Gordon - September, 2016 - Kotlin March, 2018

package com.example.animations


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.animations.R.id.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception

const val ACTION_COLOR = "com.example.cs3010_colorpicker.ACTION_COLOR"
private const val COLOR_REQUEST_CODE = 0
const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    private var cb: ChalkBoard? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        cb = ChalkBoard(this)      //Attach ChalkBoard to the Activity
        backgroundLayout.addView(cb)
        fab.setOnClickListener {
            cb!!.wander()                    //when button clicked, do animation in ChalkBoard
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Set which animation to perform on next Button click
        val id = item.itemId

        when (id) {
            new_action_1 -> {
                cb!!.setStyle(ChalkBoard.ACTION_1)
                return true
            }

            new_action_2 -> {
                cb!!.setStyle(ChalkBoard.ACTION_2)
                return true
            }

            raw_animation -> {
                cb!!.setStyle(ChalkBoard.RAW)
                return true
            }
            obj_animation -> {
                cb!!.setStyle(ChalkBoard.ANIMATOR)
                return true
            }
            accelorate_animation -> {
                cb!!.setStyle(ChalkBoard.ACCELERATOR)
                return true
            }
            decelorate_animation -> {
                cb!!.setStyle(ChalkBoard.DECELERATE)
                return true
            }
            bounce_animation -> {
                cb!!.setStyle(ChalkBoard.BOUNCE)
                return true
            }
            rotate_animation -> {
                cb!!.setStyle(ChalkBoard.ROTATE)
                return true
            }
            moverotate_animation -> {
                cb!!.setStyle(ChalkBoard.MOVE_ROTATE)
                return true
            }
            color_animation -> {
                cb!!.setStyle(ChalkBoard.COLOR_ACC)
                return true
            }
            movecolor_animation -> {
                cb!!.setStyle(ChalkBoard.MOVE_RECOLOR)
                return true
            }
            moverotatecolor_animation -> {
                cb!!.setStyle(ChalkBoard.MOVE_ROTATE_RECOLOR)
                return true
            }
            get_background_color -> {
                val sendIntent = Intent().apply {
                    action = ACTION_COLOR
                    type = "text/plain"
                    this.putExtra("Request Code", COLOR_REQUEST_CODE)
                }
                if(sendIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(sendIntent, COLOR_REQUEST_CODE)
                    onActivityResult(COLOR_REQUEST_CODE, Activity.RESULT_OK, sendIntent)
                }
                return true
            }
            action_settings -> {
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == COLOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.getStringExtra("color") != null) {
                        val tempString = data.getStringExtra("color")
                        Log.d(TAG, "Color read is: $tempString")
                        Toast.makeText(this, tempString, Toast.LENGTH_LONG).show()
                        val tempArray = tempString?.split(" ")
                        Toast.makeText(this, tempArray.toString(), Toast.LENGTH_SHORT).show()
                        backgroundLayout.setBackgroundColor(Color.parseColor(tempString))
                    }
                }
            }
        }catch (e:Exception){
            Log.d(TAG,"Error: $e")
        }
    }

    override fun finish() {
        val sendIntent = Intent().apply{
            type = "text/plain"
        }
        setResult(Activity.RESULT_OK, sendIntent)
        super.finish()
    }
}