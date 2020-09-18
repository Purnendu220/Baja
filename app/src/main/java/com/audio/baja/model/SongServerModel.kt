package com.audio.baja.model

class SongServerModel(id:String,title:String,album:String,artist:String,genre:String,source:String,image:String,trackNumber:Int,totalTrackCount:Int,duration:Int,site:String) {
    val id = id
    val title = title
    val album = album
    val artist = artist
    val genre = genre
    val source = source
    val image = image
    val trackNumber = trackNumber
    val totalTrackCount = totalTrackCount
    val duration = duration
    val site = site
    var status = 0;
    var downloadId= 0;

}
