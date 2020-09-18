package com.audio.baja.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.audio.baja.Interface.CustomItemClickListener
import com.audio.baja.R
import com.audio.baja.model.SongModel
import com.audio.baja.service.PlayMusicService
import java.util.concurrent.TimeUnit

class SongListAdapter(SongModel:ArrayList<SongModel>,context:Context,callback:CustomItemClickListener): RecyclerView.Adapter<SongListAdapter.SongListViewHolder>() {
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
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.music_row,parent,false)
        return SongListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongListViewHolder, position: Int) {
        var model = msongModel[position]
        var songName = model.mSongName
        var songDuration = toMandS( model.mSongDuration.toLong())
        holder!!.songTV.text= songName
        holder.durationTV.text=songDuration
        holder.setOnCustomItemClickListener(object:CustomItemClickListener{
            override fun onCustomItemClick(view: View, pos: Int) {
                for (i in 0 until  msongModel.size){
                    allMusicList.add(msongModel[i].mSongPath)
                }
                Log.i("allMusicList",allMusicList.toString())
//            var musicDataIntent = Intent(mContext,PlayMusicService::class.java)
//                musicDataIntent.putStringArrayListExtra(MUSICLIST,allMusicList)
//                musicDataIntent.putExtra(MUSICITEMPOS,pos)
//                mContext.startService(musicDataIntent)
                mCallback.onCustomItemClick(view,pos);
            }

        })
    }

    fun toMandS(millis:Long):String{
        var duration = String.format("%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(millis),
        TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millis)
        ))
        return duration
    }

    class SongListViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var songTV:TextView
        var durationTV:TextView
        var albumArt: ImageView
        var mCustomItemClickListener:CustomItemClickListener?=null
        init {
            songTV = itemView.findViewById(R.id.song_name_tv)
            durationTV = itemView.findViewById(R.id.song_duration_tv)
            albumArt = itemView.findViewById(R.id.al_img_view)
            itemView.setOnClickListener(this)
        }
        fun setOnCustomItemClickListener(customItemClickListener: CustomItemClickListener){
            this.mCustomItemClickListener=customItemClickListener
        }

        override fun onClick(view: View?) {
            this.mCustomItemClickListener!!.onCustomItemClick(view!!,adapterPosition)
        }
    }
}