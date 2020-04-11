package com.example.cs3013_colorpicker.ui.main

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import com.example.cs3013_colorpicker.R
import kotlinx.android.synthetic.main.fragment_save_color.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SaveColorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SaveColorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SaveColorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    val TAG = "SAVE_COLOR_FRAGMENT"
    val FILE_NAME = "SavedColors.txt"

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var callback: OnFragmentInteractionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_color, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()

        val colorPickerFragment = activity?.supportFragmentManager?.findFragmentByTag("COLOR_PICKER_FRAGMENT") as ColorPickerFragment?
        if(colorPickerFragment != null){
            colorPreview.text = colorPickerFragment.getColorString()
        }
        saveBtn.setOnClickListener {
            save()
            this.hideKeyboard()
            callback.colorSaved()
        }
    }

    fun setOnFragmentInteractionListener(callback: SaveColorFragment.OnFragmentInteractionListener){
        this.callback = callback
    }

    /**
     *
     *  Non Standard Functions
     *
     */

    private fun save(){
        val colorPickerFragment = activity?.supportFragmentManager?.findFragmentByTag("COLOR_PICKER_FRAGMENT") as ColorPickerFragment?
        if(colorPickerFragment != null) {
            val color = colorPickerFragment.getColorString()
            Log.d(TAG, "In save -- ")
            try {
                val fos: FileOutputStream? =
                    FileOutputStream((context?.getFileStreamPath(FILE_NAME)),true)
                Log.d(TAG, "In Save -- ${Context.MODE_PRIVATE} -- fos = $fos")
                val out = OutputStreamWriter(fos)
                out.appendln("${colorNameInput.text},$color")
                out.close()
                Log.d(TAG, "In Save -- Successful Write to File --${colorNameInput.text},$color")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(TAG, "In Save -- IOException: $e")
            } catch (e: Exception) {
                Log.d(TAG, "In Save -- Exception: $e")
            }
        }else{
            Log.d(TAG,"ERROR - colorPickerFragment was null")
        }
    }

    /**
     * Utility Functions
     */
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
        fun colorSaved()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SaveColorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SaveColorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
