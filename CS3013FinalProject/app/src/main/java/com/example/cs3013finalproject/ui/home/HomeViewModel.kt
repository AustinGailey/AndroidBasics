package com.example.cs3013finalproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This app holds all the projects we have worked on this semester." +
                "  Use the drawers by swiping left to navigate to different projects."
    }
    val text: LiveData<String> = _text
}