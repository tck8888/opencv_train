package com.tck.opecv.base

import android.util.Log

/**
 *
 * description:

 * @date 2020/12/5 20:12

 * @author tck88
 *
 * @version v1.0.0
 *
 */
object MyLog {

    const val TAG = "my_opencv"

    fun d(msg: String) {
        Log.d(TAG, msg)
    }
}