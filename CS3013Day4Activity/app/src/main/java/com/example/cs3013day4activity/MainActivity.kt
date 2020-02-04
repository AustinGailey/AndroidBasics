package com.example.cs3013day4activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setDefaults()

        val textViewToggleButton:ToggleButton = findViewById(R.id.textViewToggleBtn)
        textViewToggleButton.setOnCheckedChangeListener { _, _ ->
            toggleTextView()
        }
        val backgroundToggleButton:ToggleButton = findViewById(R.id.backgroundToggleBtn)
        backgroundToggleButton.setOnCheckedChangeListener { _, isChecked ->
            val mainLayout:ConstraintLayout = findViewById(R.id.constraintLayout)
            if(isChecked){
                mainLayout.setBackgroundColor(Color.RED)
            }else{
                mainLayout.setBackgroundColor(Color.BLUE)
            }
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            R.id.helpMenu -> {
                toggleTextView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun toggleTextView(){
        val tv = findViewById<TextView>(R.id.textView)
        if(tv.visibility == View.INVISIBLE){
            tv.visibility = View.VISIBLE
        }else{
            tv.visibility = View.INVISIBLE
        }
    }

    private fun setDefaults(){
        val tv = findViewById<TextView>(R.id.textView)
        tv.visibility = View.INVISIBLE

    }


}
