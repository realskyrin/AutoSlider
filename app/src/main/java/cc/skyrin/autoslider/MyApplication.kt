package cc.skyrin.autoslider

import android.app.Application

class MyApplication : Application() {
    companion object {

        fun activityResumed() {
            isActivityVisible = true
        }

        fun activityPaused() {
            isActivityVisible = false
        }

        var isActivityVisible: Boolean = false
            private set
    }
}
