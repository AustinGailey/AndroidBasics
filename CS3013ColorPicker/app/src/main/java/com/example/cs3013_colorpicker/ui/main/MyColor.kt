package com.example.cs3013_colorpicker.ui.main


class MyColor constructor(var colorHex:String, var name:String){

    fun colorToInt():Int {
        return "${this.colorHex.substring(1,this.colorHex.length)}".toInt()
    }

    override fun toString(): String {
        return "name: $name Hex Value: $colorHex"
    }
}