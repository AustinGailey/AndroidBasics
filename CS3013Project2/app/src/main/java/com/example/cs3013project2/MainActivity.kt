package com.example.cs3013project2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
                     SettingsFragment.OnFragmentInteractionListener,
                     DisplayFragment.OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val settingsFrag = SettingsFragment()
        val displayFrag = DisplayFragment()
        val fragManager: FragmentManager = supportFragmentManager
        fragManager.beginTransaction()
            .replace(R.id.contentMain2Layout, settingsFrag, settingsFrag.tag)
            .replace(R.id.contentMain1Layout, displayFrag, "DISPLAY_FRAG")
            .commit()
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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is SettingsFragment) {
            fragment.setOnFragmentInteractionListener(this)
        }
    }

     override fun onFragmentInteraction(button: Button) {
        val image: ImageView = findViewById(R.id.imageView)
        image.visibility = ImageView.INVISIBLE
    }

    override fun onButtonInteraction(button: Button) {
        //val request_code = 0
        //startActivityForResult(intent, request_code)
        //val displayFragment: DisplayFragment? = supportFragmentManager.findFragmentById(R.id.displayFragLayout) as DisplayFragment?
        val x = supportFragmentManager.findFragmentByTag("DISPLAY_FRAG")
        val displayFragment = x as DisplayFragment?
        if(displayFragment != null){
            displayFragment.toggleImage()
        }else{
            Log.d("ACTIVITY_MAIN","Display Fragment: $displayFragment")
        }
            Log.d("ACTIVITY_MAIN","Display Fragment: $displayFragment")

    }

    override fun onDisplayFragmentInteraction(button: Button) {
        
    }

}
