package com.audio.baja.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class RestApiInstance private constructor(private var mCtx: Context) {
    private var requestQueue: RequestQueue?
    val requestQue: RequestQueue?
        get() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext())
            }
            return requestQueue
        }

    fun <T> addToRequest(request: Request<T>?) {
        requestQueue!!.add(request)
    }

    companion object {
        private var mInstance: RestApiInstance? = null
        @Synchronized
        fun getInstance(context: Context): RestApiInstance? {
            if (mInstance == null) {
                mInstance = RestApiInstance(context)
            }
            return mInstance
        }
    }

    init {
        requestQueue = requestQue
    }
}