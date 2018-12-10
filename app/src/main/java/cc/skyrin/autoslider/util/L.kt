package cc.skyrin.autoslider.util

import android.util.Log

import cc.skyrin.autoslider.BuildConfig

/**
 * Log util
 */
object L {

    private val DEBUG = BuildConfig.DEBUG

    private val DefaultTag = "Debug"
    // 是否是调试阶段
    private val isDebug: Boolean?
        get() = DEBUG

    fun e(TAG: String, msg: String) {
        if (DEBUG) {
            Log.e(TAG, msg + "")
        }
    }

    fun e(msg: String) {
        if (DEBUG) {
            Log.e(DefaultTag, msg + "")
        }
    }

    fun i(TAG: String, msg: String) {
        if (DEBUG) {
            Log.i(TAG, msg + "")
        }
    }

    fun i(msg: String) {
        if (DEBUG) {
            Log.i(DefaultTag, msg + "")
        }
    }

    fun d(TAG: String, msg: String) {
        if (DEBUG) {
            Log.d(TAG, msg + "")
        }
    }

    fun d(msg: String) {
        if (DEBUG) {
            Log.d(DefaultTag, msg + "")
        }
    }

    fun w(TAG: String, msg: String) {
        if (DEBUG) {
            Log.w(TAG, msg + "")
        }
    }

    fun w(msg: String) {
        if (DEBUG) {
            Log.w(DefaultTag, msg + "")
        }
    }

    fun v(TAG: String, msg: String) {
        if (DEBUG) {
            Log.v(TAG, msg + "")
        }
    }

    fun v(msg: String) {
        if (DEBUG) {
            Log.v(DefaultTag, msg + "")
        }
    }
}
