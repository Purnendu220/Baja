package com.audio.baja.Interface

import android.view.View
import com.audio.baja.model.SongServerModel

interface CustomItemServerClickListener {
    fun onCustomItemClick(view: View?, pos:Int, model:SongServerModel)
}