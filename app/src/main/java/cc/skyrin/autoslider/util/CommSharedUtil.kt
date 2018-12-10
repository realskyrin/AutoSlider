package cc.skyrin.autoslider.util

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


/**
 * CommSharedUtils
 * 通用的SharedPreferences
 */
class CommSharedUtil private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE)
    }

    fun putInt(key: String, value: Int) {
        val edit = sharedPreferences.edit()
        edit.putInt(key, value)
        edit.apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }


    fun putString(key: String, value: String) {
        val edit = sharedPreferences.edit()
        edit.putString(key, value)
        edit.apply()
    }


    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }


    fun putBoolean(key: String, value: Boolean) {
        val edit = sharedPreferences.edit()
        edit.putBoolean(key, value)
        edit.apply()
    }


    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    fun remove(key: String) {
        val edit = sharedPreferences.edit()
        edit.remove(key)
        edit.apply()
    }

    companion object {

        private val SHARED_PATH = "app_auto_slider_info"
        val FLAG_IS_OPEN_LONG_LIGHT = "longlight"
        private var helper: CommSharedUtil? = null

        fun getInstance(context: Context): CommSharedUtil {
            if (helper == null) {
                helper = CommSharedUtil(context)
            }
            return helper as CommSharedUtil
        }
    }

}
