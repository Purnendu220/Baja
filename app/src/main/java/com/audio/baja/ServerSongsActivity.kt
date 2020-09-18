package com.audio.baja

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.audio.baja.Interface.CustomItemServerClickListener
import com.audio.baja.adapter.SongListServerAdapter
import com.audio.baja.model.SongServerModel
import com.audio.baja.utils.FileDir
import com.audio.baja.utils.PrefrenceManager
import com.audio.baja.utils.RestApiInstance
import com.downloader.*
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.model.JcAudio
import com.github.abdularis.buttonprogress.DownloadButtonProgress
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException


class ServerSongsActivity : AppCompatActivity(), JcPlayerManagerListener,
    CustomItemServerClickListener {
    var apiInstance : RestApiInstance? = null
    lateinit var mContext:Context
    var songModel1Data:ArrayList<SongServerModel> = ArrayList()
    var songListAdapter: SongListServerAdapter?=null
    var dirPath :String? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_songs)
        mContext = this
        dirPath = FileDir.getRootDirPath(applicationContext);
        PRDownloader.initialize(applicationContext);


        apiInstance= RestApiInstance.getInstance(mContext)
        if(ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                MainActivity.PERMISSION_REQUEST_CODE
            )
        }else{
            getSongsFromServer()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == MainActivity.PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                getSongsFromServer()
            }

        }
    }
    fun getSongsFromServer(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Processing...")
        progressDialog.show()
        VolleyLog.DEBUG=true;
        var url ="https://storage.googleapis.com/uamp/catalog.json"
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    progressDialog.dismiss()
                    val jsonArray = response.getJSONArray("music")
                    for (i in 0 until jsonArray.length()) {
                        val song = jsonArray.getJSONObject(i)
                        val id = song.getString("id")

                        val title = song.getString("title")
                        val album = song.getString("album")
                        val artist = song.getString("artist")
                        val genre = song.getString("genre")

                        val source = song.getString("source")
                        val image = song.getString("image")
                        val trackNumber = song.getInt("trackNumber")

                        val totalTrackCount = song.getInt("totalTrackCount")
                        val duration = song.getInt("duration")
                        val site = song.getString("site")

                        songModel1Data.add(
                            SongServerModel(
                                id,
                                title,
                                album,
                                artist,
                                genre,
                                source,
                                image,
                                trackNumber,
                                totalTrackCount,
                                duration,
                                site
                            )
                        )
                    }




                    songListAdapter = SongListServerAdapter(
                        songModel1Data,
                        applicationContext,
                        this
                    )
                    var layoutManager = LinearLayoutManager(applicationContext)
                    recycler_view.layoutManager = layoutManager
                    recycler_view.adapter = songListAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                error.printStackTrace()
                progressDialog.dismiss()

            })
        apiInstance?.addToRequest(request);
    }
    override fun onCustomItemClick(view: View?, pos: Int, model: SongServerModel) {
        if(view!=null){
            downloadFile(view,pos,model);

        }else{
         startPlayer(pos);
        }

    }

    fun downloadFile(view: View, pos: Int, model: SongServerModel){
     var btn: DownloadButtonProgress = view as DownloadButtonProgress;
        btn.setDeterminate();
         var downloadId = PRDownloader.download(model.source, dirPath, model.title+".mp3")
            .build()
            .setOnStartOrResumeListener {
                btn.setIndeterminate()
                PrefrenceManager.instance?.setDownloadStatus(model.id,2);

                 updateRecyclerView(2,pos,model)
            }
             .setOnPauseListener(object : OnPauseListener {
                override fun onPause() {
                    btn.setIdle()
                    PrefrenceManager.instance?.setDownloadStatus(model.id,0);

                    updateRecyclerView(0,pos,model)

                }
            })
            .setOnCancelListener {
                btn.setIdle()

                PrefrenceManager.instance?.setDownloadStatus(model.id,0);


            }
            .setOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Progress?) {
                    val progressPercent = progress!!.currentBytes * 100 / progress.totalBytes
                    btn.currentProgress = progressPercent.toInt()
                }
            })
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    btn.setFinish();
                    PrefrenceManager.instance?.setDownloadStatus(model.id,3);

                    updateRecyclerView(3,pos,model)
                }

                override fun onError(error: com.downloader.Error?) {
                    btn.setIdle();
                    PrefrenceManager.instance?.setDownloadStatus(model.id,0);

                    updateRecyclerView(0,pos,model)

                }

            })
        model.downloadId = downloadId;
        PrefrenceManager.instance?.setDownloadStatus(model.id,0);

        updateRecyclerView(1,pos,model);


    }

    fun updateRecyclerView(status:Int,pos: Int, model: SongServerModel){
        model.status = status;
        songListAdapter!!?.notifyItemChanged(pos)
        when (status) {
            0 -> {

                true
            }
            1 -> {

                true
            }
            2 -> {

                true
            }
            3 -> {

                true
            }

        }

    }

    fun startPlayer(pos: Int){
        jcplayer.kill()
        jcplayer.visibility = View.VISIBLE
        val jcAudios = java.util.ArrayList<JcAudio>()
        val jcAudio: JcAudio =
            JcAudio.createFromURL(songModel1Data[pos].title, songModel1Data[pos].source)
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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}