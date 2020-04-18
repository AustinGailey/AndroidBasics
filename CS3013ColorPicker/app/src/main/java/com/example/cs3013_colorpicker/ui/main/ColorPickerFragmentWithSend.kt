package com.example.cs3013_colorpicker.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SeekBar
import androidx.appcompat.widget.Toolbar

import com.example.cs3013_colorpicker.R
import kotlinx.android.synthetic.main.color_list_item.*
import kotlinx.android.synthetic.main.fragment_color_picker.*
import java.io.*
import java.lang.Exception
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ColorPickerFragmentWithSend.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ColorPickerFragmentWithSend.newInstance] factory method to
 * create an instance of this fragment.
 */
class ColorPickerFragmentWithSend : Fragment(), SeekBar.OnSeekBarChangeListener,MenuItem.OnMenuItemClickListener {
    private val TAG = "COLOR_PICK_FRAG_W_SEND"

    var sendColor:String = ""

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var callback:OnFragmentInteractionListener

    private val FILE_NAME = "SavedColors.txt"
    private var myColorList: LinkedList<com.example.cs3013_colorpicker.ui.main.MyColor> = LinkedList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG,"in onCreate")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            Log.d(TAG,"in onCreate - param1: $param1")
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d(TAG,"in onCreate -- arguments: $arguments")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG,"View Inflated -- $alphaSelectorBar")
        val fragmentColorPickerView: View = inflater.inflate(R.layout.fragment_color_picker, container, false)
        Log.d(TAG,"View Inflated -- $alphaSelectorBar")
        return fragmentColorPickerView
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}



    //
    //DEFAULT METHODS
    //
    fun setOnFragmentInteractionListener(callback: OnFragmentInteractionListener){
        this.callback = callback
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"In OnAttach -- $alphaSelectorBar")

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"In OnResume -- $alphaSelectorBar")
        sendBtn.visibility = View.VISIBLE


        val tOnMenuItemSelectedListener = Toolbar.OnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.action_save -> {
                    Log.d(TAG,"Save Button Clicked")
                    callback.onSaveBtnClick()
                }
                R.id.action_load -> {
                    Log.d(TAG,"Load Button Clicked")
                    //load()
                    callback.onLoadBtnClick()
                }
            }
            true
        }
        toolbar.setOnMenuItemClickListener(tOnMenuItemSelectedListener)

        alphaSelectorBar.max = 255
        alphaSelectorBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                colorPickerFragmentSurfaceView.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        redSelectorBar.max = 255
        redSelectorBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                colorPickerFragmentSurfaceView.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        greenSelectorBar.max = 255
        greenSelectorBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                colorPickerFragmentSurfaceView.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        blueSelectorBar.max = 255
        blueSelectorBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                colorPickerFragmentSurfaceView.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        if(arguments?.get("surfaceViewColor") != null && arguments != null){
            colorPickerFragmentSurfaceView.setBackgroundColor(arguments!!.getInt("surfaceViewColor"))
            Log.d(TAG,"in onResume - set colorPickerFragmentSurfaceView ${arguments!!.getInt("surfaceViewColor")}")
        }

        sendBtn.setOnClickListener {
            sendColor = getColorString()
            callback.finish()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG,"In onActivityCreated -- $alphaSelectorBar")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG,"In OnStart -- $alphaSelectorBar")
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        Log.d(TAG, "In onMenuItemClick -- $item")
        return true
    }


    /**
     *
     *     Personalized Functions Below
     *
     */

    fun getColorString(): String {
        var a = Integer.toHexString(((255*alphaSelectorBar.progress)/alphaSelectorBar.max))
        if(a.length==1) a = "0"+a
        var r = Integer.toHexString(((255*redSelectorBar.progress)/redSelectorBar.max))
        if(r.length==1) r = "0"+r
        var g = Integer.toHexString(((255*greenSelectorBar.progress)/greenSelectorBar.max))
        if(g.length==1) g = "0"+g
        var b = Integer.toHexString(((255*blueSelectorBar.progress)/blueSelectorBar.max))
        if(b.length==1) b = "0"+b
        return "#" + a + r + g + b
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
        fun onSaveBtnClick()
        fun onLoadBtnClick()
        fun finish()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ColorPickerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ColorPickerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
