package com.aragold.app

import android.app.Application
import com.aragold.app.data.AppContext
import com.aragold.app.data.FirebaseBridge
import com.aragold.app.data.MidnightScheduler
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class AraGoldApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        if (FirebaseBridge.isConfigured) {
            FirebaseApp.initializeApp(this, FirebaseOptions.fromResource(this))
        }
        MidnightScheduler.scheduleIfNeeded()
    }
}
