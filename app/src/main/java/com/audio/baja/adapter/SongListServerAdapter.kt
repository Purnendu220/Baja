package com.audio.baja.adapter

import android.content.Context
import android.opengl.Visibility

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.audio.baja.Interface.CustomItemClickListener
import com.audio.baja.Interface.CustomItemServerClickListener
import com.audio.baja.R
import com.audio.baja.model.SongServerModel
import com.audio.baja.utils.PrefrenceManager
import com.downloader.PRDownloader
import com.github.abdularis.buttonprogress.DownloadButtonProgress
import com.squareup.picasso.Picasso

import java.util.concurrent.TimeUnit

class SongListServerAdapter(
    SongModel: ArrayList<SongServerModel>,
    context: Context,
    callback: CustomItemServerClickListener
): RecyclerView.Adapter<SongListServerAdapter.SongListViewHolder>() {
    var mContext=context
    var msongModel= SongModel
    var allMusicList:ArrayList<String> = ArrayList()
    var mCallback = callback;
    companion object{
        val MUSICLIST="musiclist"
        val MUSICITEMPOS ="pos"
    }

    override fun getItemCount(): Int {
        return msongModel.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListViewHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.music_row, parent, false)
        return SongListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongListViewHolder, position: Int) {
        var model = msongModel[position]
        var songName = model.title
        var songDuration = toMandS((model.duration * 1000).toLong())
        holder!!.songTV.text= songName
        holder.durationTV.text=songDuration
        holder.downLoadButton.visibility = View.VISIBLE;
        when (PrefrenceManager.instance?.getDownloadStatus(model.id)) {
            0 -> {
                holder.downLoadButton.setIdle()
                true
            }
            1 -> {
                holder.downLoadButton.setDeterminate()

                true
            }
            2 -> {
                holder.downLoadButton.setIndeterminate()

                true
            }
            3 -> {
                holder.downLoadButton.setFinish()

                true
            }

        }

        holder.setOnCustomItemClickListener(object : CustomItemClickListener {
            override fun onCustomItemClick(view: View, pos: Int) {
                if(PrefrenceManager.instance?.getDownloadStatus(model.id)==3){
                    mCallback.onCustomItemClick(null, position, model);

                }else{
                    mCallback.onCustomItemClick(holder.downLoadButton, pos, model);

                }

            }

        })
        holder.downLoadButton.addOnClickListener(object : DownloadButtonProgress.OnClickListener {
            override fun onIdleButtonClick(view: View) {
                mCallback.onCustomItemClick(holder.downLoadButton, position, model);

            }

            override fun onCancelButtonClick(view: View) {
                mCallback.onCustomItemClick(holder.downLoadButton, position, model);

                PRDownloader.cancel(model.downloadId);


            }

            override fun onFinishButtonClick(view: View) {
                mCallback.onCustomItemClick(null, position, model);
            }
        })
        Picasso.get()
            .load(model.image)
            .resize(50, 50)
            .centerCrop()
            .placeholder(R.drawable.music_24)
            .into(holder.albumArt)

    }

    fun toMandS(millis: Long):String{
        var duration = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millis)
            )
        )
        return duration
    }

    class SongListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var songTV:TextView
        var durationTV:TextView
        var albumArt: ImageView
        var downLoadButton:DownloadButtonProgress
        var mCustomItemClickListener:CustomItemClickListener?=null
        init {
            songTV = itemView.findViewById(R.id.song_name_tv)
            durationTV = itemView.findViewById(R.id.song_duration_tv)
            albumArt = itemView.findViewById(R.id.al_img_view)
            downLoadButton = itemView.findViewById(R.id.downloadButton);
            itemView.setOnClickListener(this)
        }
        fun setOnCustomItemClickListener(customItemClickListener: CustomItemClickListener){
            this.mCustomItemClickListener=customItemClickListener
        }

        override fun onClick(view: View?) {
            this.mCustomItemClickListener!!.onCustomItemClick(view!!, adapterPosition)
        }
    }
}