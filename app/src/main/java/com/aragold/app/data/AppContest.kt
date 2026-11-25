package com.aragold.app.data

import android.content.Context

object AppContext {
    lateinit var ctx: Context
    fun init(c: Context) { ctx = c.applicationContext }
}
