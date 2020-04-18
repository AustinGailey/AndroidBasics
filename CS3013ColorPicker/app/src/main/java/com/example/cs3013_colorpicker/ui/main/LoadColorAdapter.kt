package com.example.cs3013_colorpicker.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs3013_colorpicker.R
import kotlinx.android.synthetic.main.color_list_item.view.*
import org.w3c.dom.Text
import java.util.*

import android.graphics.Color
import android.util.Log
import android.view.View

class LoadColorAdapter(private val myDataset: LinkedList<MyColor>,private val onColorSelectedListener: OnColorSelectedListener) :
    RecyclerView.Adapter<LoadColorAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val colorView: LinearLayout,private var onColorSelectedListener: OnColorSelectedListener) : RecyclerView.ViewHolder(colorView), View.OnClickListener {
        val TAG = "MY_VIEW_HOLDER"
        //TODO https://www.youtube.com/watch?v=69C1ljfDvl0 @3:56

        private lateinit var callback: LoadColorFragment.OnFragmentInteractionListener


        init {
            Log.d(TAG,"Initializing coloView onClickListener")
            colorView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onColorSelectedListener.onColorSelected(adapterPosition)
            //callback.colorLoaded()
        }





    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): LoadColorAdapter.MyViewHolder {
        // create a new view
        val colorView = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_list_item, parent, false) as LinearLayout
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(colorView,onColorSelectedListener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.colorView.colorText.text = "${myDataset[position].name} : ${myDataset[position].colorHex}"
        holder.colorView.colorSurfaceView.setBackgroundColor(Color.parseColor(myDataset[position].colorHex))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    interface OnColorSelectedListener{
        fun onColorSelected(position: Int)
    }
}