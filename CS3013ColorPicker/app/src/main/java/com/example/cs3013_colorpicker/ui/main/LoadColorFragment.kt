package com.example.cs3013_colorpicker.ui.main

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.cs3013_colorpicker.R
import kotlinx.android.synthetic.main.color_list_item.*
import kotlinx.android.synthetic.main.fragment_color_picker.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "LOAD_COLOR_FRAGMENT"
private const val FILE_NAME = "SavedColors.txt"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LoadColorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LoadColorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoadColorFragment : Fragment(), LoadColorAdapter.OnColorSelectedListener {

    val TAG = "LOAD_COLOR_FRAGMENT"
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var callback: OnFragmentInteractionListener


    val FILE_NAME = "SavedColors.txt"

    private var myColorList: LinkedList<MyColor> = LinkedList()

    //For RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    var selectedColorInt: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"In onCreate")
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d(TAG,"In onCreate")

        Log.d(TAG,"End onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.i(TAG,"In onCreateView")
        val view = inflater.inflate(R.layout.fragment_load_color, container, false)
        loadColors()
        viewManager = LinearLayoutManager(context)
        //val dataArray: Array<String> = myColorList.toArray()
        viewAdapter = LoadColorAdapter(myColorList,this)

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewColors).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG,"In onAttach")
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
        Log.i(TAG,"In onAttach Finished")
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     *  Non-LifeCycle Functions
     */


    private fun loadColors(){
        Log.i(TAG, "In loadColors ---")
        try {
            val fis = context?.openFileInput(FILE_NAME)
            val r = BufferedReader(InputStreamReader(fis))
            val text:List<String> = r.readLines()
            for(line in text){
                var  parseLine:List<String> = line.split(",")
                myColorList.add(MyColor(colorHex = parseLine[1],name = parseLine[0]))
                Log.d(TAG,"Reading $FILE_NAME -- Line: $line")
                Log.d(TAG,"In myColorList: ${myColorList.last}")
            }
            r.close()
            //Log.d(TAG,"Line =$line, $line2")
        } catch(e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "Load Failed - Exception: $e")
        } catch(e: Exception){
            Log.d(TAG, "Load Failed - Exception: $e")
        }
    }

    override fun onColorSelected(position: Int) {
        myColorList[position]
        Log.d(TAG,"in onColorSelected -- $position")
        Log.d(TAG, "in onColorSelected -- new color: ${Color.parseColor(myColorList[position].colorHex)}")
        //Log.d(TAG,"in onColorSelected -- colorSurface: $colorSurface")
        //colorPickerFragmentSurfaceView.setBackgroundColor(Color.parseColor(myColorList[position].colorHex))
        selectedColorInt = Color.parseColor(myColorList[position].colorHex)
        callback.colorLoaded()
    }

    fun setOnFragmentInteractionListener(callback: LoadColorFragment.OnFragmentInteractionListener){
        this.callback = callback
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
        fun colorLoaded()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoadColorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoadColorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
