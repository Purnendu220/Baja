package com.audio.baja.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.audio.baja.adapter.SongListAdapter

class PlayMusicService:Service() {
    var currentaPos:Int =0
    var musicDataList:ArrayList<String> = ArrayList()
    var mp:MediaPlayer?=null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentaPos=intent!!.getIntExtra(SongListAdapter.MUSICITEMPOS,0)
        musicDataList= intent.getStringArrayListExtra(SongListAdapter.MUSICLIST) as ArrayList<String>
        if (mp!=null){
            mp!!.stop()
            mp!!.release()
            mp=null
        }
        mp = MediaPlayer()
        mp!!.setDataSource(musicDataList[currentaPos])
        mp!!.prepare()
        mp!!.setOnPreparedListener {
            mp!!.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
