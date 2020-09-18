package com.audio.baja.utils

import com.google.gson.Gson
import com.preference.PowerPreference
import com.preference.Preference


class PrefrenceManager private constructor() {
    private val prefrence: Preference
    private val gson: Gson
    fun setDownloadStatus(k: String?, s: Int) {
        prefrence.putInt(k, s)
    }

    fun getDownloadStatus(k: String?): Int {
        return prefrence.getInt(k, 0)
    }

    companion object {
        private var sInstance: PrefrenceManager? = null
        val instance: PrefrenceManager?
            get() {
                if (sInstance == null) {
                    sInstance = PrefrenceManager()
                }
                return sInstance
            }
    }

    init {
        gson = Gson()
        prefrence = PowerPreference.getDefaultFile()
    }
}