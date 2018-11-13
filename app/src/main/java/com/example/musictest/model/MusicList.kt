package com.example.musictest.model

import org.litepal.crud.LitePalSupport

data class MusicList(var id:Int,
                     var musicName:String,
                     var songName:String,
                     var albumName:String,
                     var musicUri:String,
                     var duration:Int,
                     var musicSize:Long,
                     var albumId:Int): LitePalSupport() {
}