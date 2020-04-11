package com.example.cs3013_colorpicker

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cs3013_colorpicker.ui.main.ColorPickerFragment
import com.example.cs3013_colorpicker.ui.main.LoadColorFragment
import com.example.cs3013_colorpicker.ui.main.MainFragment
import com.example.cs3013_colorpicker.ui.main.SaveColorFragment
import kotlinx.android.synthetic.main.fragment_color_picker.*

class MainActivity : AppCompatActivity(),
    ColorPickerFragment.OnFragmentInteractionListener,
    SaveColorFragment.OnFragmentInteractionListener,
    LoadColorFragment.OnFragmentInteractionListener {

    val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainLayout, MainFragment.newInstance())
                .commitNow()
        }

        //DEFAULT TO COLOR_PICKER_FRAGMENT
        val colorPickerFragment = ColorPickerFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainLayout, colorPickerFragment, "COLOR_PICKER_FRAGMENT")
            .commit()

    }

    //
    //
    //REQUIRED FUNCTION OVERRIDES
    //
    //
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onSaveBtnClick() {
        val saveColorFragment = SaveColorFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.colorPickerFragmentLayout, saveColorFragment, "SAVE_COLOR_FRAGMENT")
            .addToBackStack(null)
            .commit()
        Log.d(TAG,"In onSaveBtnClick --- $saveColorFragment")
    }

    override fun onLoadBtnClick() {
        Log.i(TAG,"In on LoadBtnClick")
        val loadColorFragment = LoadColorFragment()
        Log.i(TAG,"In on LoadBtnClick -- Starting Transaction ${R.id.loadColorFragmentLayout}")
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        Log.i(TAG,"In on LoadBtnClick -- transaction created -- starting replace")
        transaction.replace(R.id.mainLayout,loadColorFragment, "LOAD_COLOR_FRAGMENT")
        Log.i(TAG,"In on LoadBtnClick -- transaction created -- replace finished -- starting addToBackStack")
        transaction.addToBackStack(null)
        Log.i(TAG,"In on LoadBtnClick -- addToBackStack finished -- starting commit")
        transaction.commit()
        Log.d(TAG, "In onLoadBtnClick --- Transaction Finished")
    }

    override fun colorSaved() {
        val colorPickerFragment = ColorPickerFragment()
       // val saveColorPickerFragment = supportFragmentManager.findFragmentByTag("SAVE_COLOR_FRAGMENT") as SaveColorFragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.saveColorFragmentLayout, colorPickerFragment, "COLOR_PICKER_FRAGMENT")
           // .remove(saveColorPickerFragment)
            .commit()
        val saveColorFragment = supportFragmentManager.findFragmentByTag("SAVE_COLOR_FRAGMENT") as SaveColorFragment
        saveColorFragment.onDestroyView()

        Log.d(TAG,"In colorSaved --- $saveColorFragment")
    }

    override fun colorLoaded() {
        Log.d(TAG,"in colorLoaded")
        val loadColorFragment = supportFragmentManager.findFragmentByTag("LOAD_COLOR_FRAGMENT") as LoadColorFragment
        val newColor = loadColorFragment.selectedColorInt
        val colorPickerFragment = ColorPickerFragment()
        val arguments = Bundle()
        arguments.putInt("surfaceViewColor",newColor)
        colorPickerFragment.arguments = arguments
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainLayout, colorPickerFragment, "COLOR_PICKER_FRAGMENT")
            //.remove(loadColorFragment)
            .commit()

        //colorPickerFragment.colorPickerFragmentSurfaceView.setBackgroundColor(newColor)
        loadColorFragment.onDestroyView()
        Log.d(TAG,"in colorLoaded - loadColorFragment destroyed: $loadColorFragment")
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is ColorPickerFragment) {
            fragment.setOnFragmentInteractionListener(this)
        }
        if (fragment is SaveColorFragment) {
            fragment.setOnFragmentInteractionListener(this)
        }
        if(fragment is LoadColorFragment) {
            fragment.setOnFragmentInteractionListener(this)
        }
    }

}

