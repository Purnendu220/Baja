package com.audio.baja

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.audio.baja.Interface.CustomItemClickListener
import com.audio.baja.adapter.SongListAdapter
import com.audio.baja.model.SongModel
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.model.JcAudio
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() , CustomItemClickListener, JcPlayerManagerListener {

    var songModel1Data:ArrayList<SongModel> = ArrayList()
    var songListAdapter:SongListAdapter?=null
    companion object{
        val PERMISSION_REQUEST_CODE =12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        }else{
            loadData()
        }
    }

    fun loadData(){

        var songCursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null, null, null, null
        )
        while (songCursor!=null && songCursor.moveToNext()){
            var songName= songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            var songDuration =songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            var songPath = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

            if(songName!=null&&songDuration!=null&&songPath!=null){
            songModel1Data.add(
                SongModel(
                    songName,
                    songDuration,
                    songPath
                )
            )

            }
        }
        songListAdapter = SongListAdapter(songModel1Data, applicationContext, this)

        var layoutManager= LinearLayoutManager(applicationContext)
        recycler_view.layoutManager=layoutManager
        recycler_view.adapter= songListAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            loadData()
            }

        }
    }
    override fun onCustomItemClick(view: View, pos: Int) {
        startPlayer(pos)

    }

    fun startPlayer(pos: Int){
        jcplayer.kill()
        jcplayer.visibility = View.VISIBLE
        val jcAudios = java.util.ArrayList<JcAudio>()
        val jcAudio: JcAudio =
            JcAudio.createFromFilePath(
                songModel1Data[pos].mSongName,
                songModel1Data[pos].mSongPath
            )
        jcAudios.add(jcAudio)
        jcplayer.initPlaylist(jcAudios, null)
        jcplayer.playAudio(jcAudio)
        jcplayer.jcPlayerManagerListener = this
    }

    override fun onStop() {
        super.onStop()
        jcplayer.createNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        jcplayer.kill()

    }

    override fun onCompletedAudio() {
        jcplayer.isActivated = false
        jcplayer.visibility = View.INVISIBLE
        jcplayer.kill()
    }

    override fun onContinueAudio(status: JcStatus) {
        jcplayer.isActivated = false
        jcplayer.visibility = View.INVISIBLE
        jcplayer.kill()
    }

    override fun onJcpError(throwable: Throwable) {
    }

    override fun onPaused(status: JcStatus) {
    }

    override fun onPlaying(status: JcStatus) {
    }

    override fun onPreparedAudio(status: JcStatus) {
    }

    override fun onStopped(status: JcStatus) {
    }

    override fun onTimeChanged(status: JcStatus) {
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_open -> {
                val intent = Intent(this, ServerSongsActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}