package com.walfud.puyl

import android.util.Log

interface Logger {

    fun e(msg: String)

    companion object : Logger {
        override fun e(msg: String) {
            Log.d("", msg)
        }
    }
}