package cc.skyrin.autoslider.util;

import android.util.Log;

import cc.skyrin.autoslider.BuildConfig;

/**
 * Log util
 */
public class L {

	private static final boolean DEBUG = BuildConfig.DEBUG;
	
	private static final String DefaultTag = "Debug";

	public static void e(String TAG, String msg) {
		if (DEBUG) {
			Log.e(TAG, msg+"");
		}
	}

	public static void e(String msg) {
		if (DEBUG) {
			Log.e(DefaultTag, msg+"");
		}
	}
	public static void i(String TAG, String msg) {
		if (DEBUG) {
			Log.i(TAG, msg+"");
		}
	}
	public static void i(String msg) {
		if (DEBUG) {
			Log.i(DefaultTag, msg+"");
		}
	}
	public static void d(String TAG, String msg) {
		if (DEBUG) {
			Log.d(TAG, msg+"");
		}
	}
	public static void d(String msg) {
		if (DEBUG) {
			Log.d(DefaultTag, msg+"");
		}
	}
	public static void w(String TAG, String msg) {
		if (DEBUG) {
			Log.w(TAG, msg+"");
		}
	}
	public static void w( String msg) {
		if (DEBUG) {
			Log.w(DefaultTag, msg+"");
		}
	}
	public static void v(String TAG, String msg) {
		if (DEBUG) {
			Log.v(TAG, msg+"");
		}
	}
	public static void v(String msg) {
		if (DEBUG) {
			Log.v(DefaultTag, msg+"");
		}
	}
	// 是否是调试阶段
	private static Boolean isDebug() {
		return DEBUG;
	}
}
