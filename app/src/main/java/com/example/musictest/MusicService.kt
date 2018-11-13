package com.example.musictest

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.musictest.model.MusicList
import com.example.musictest.view.MusicNavBottom
import org.litepal.LitePal

class MusicService : Service {

    companion object {
        private var musicBinder = MusicBinder()
        var mediaPlayer = MediaPlayer()
        private var musicLists:MutableList<MusicList> = ArrayList()
        private var num = 0

        private var musicStatus = object : MediaPlayer.OnCompletionListener{
            override fun onCompletion(mp: MediaPlayer?) {
                musicBinder.nextMusic()
            }

        }

        fun musicLoading(i:Int){
            mediaPlayer.setDataSource(musicLists.get(i).musicUri)
            mediaPlayer.prepare()
            mediaPlayer.setOnCompletionListener(musicStatus)
        }
    }

    constructor() : super(){
        musicLists = LitePal.findAll(MusicList::class.java)
        musicLoading(num)
    }


    class MusicBinder : Binder(){

        fun playMusic(){
            if(!mediaPlayer.isPlaying){
                //Log.e("playMusic","start")
                mediaPlayer.start()
            }
            //Log.e("playMusic","do not start")
        }

        fun pauseMusic(){
            if (mediaPlayer.isPlaying){
                //Log.e("pauseMusic","pause")
                mediaPlayer.pause()
            }
            //Log.e("pauseMusic","do not pause")
        }

        fun closeMedia(){
            if (mediaPlayer != null){
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }

        fun resetMedia(){
            if (!mediaPlayer.isPlaying){
                mediaPlayer.reset()
                musicLoading(num)
            }
        }

        fun nextMusic(){
            mediaPlayer.stop()
            if (num < musicLists.size - 1)
                num ++
            else if(num == musicLists.size - 1)
                num = 0
            resetMedia()
            playMusic()
        }

        fun previousMusic(){
            mediaPlayer.stop()
            if (num > 0)
                num --
            else if(num == 0)
                num = musicLists.size - 1
            resetMedia()
            playMusic()
        }

        fun getDuration():Int{
            return mediaPlayer.duration
        }

        fun getCurrentPosition():Int{
            return mediaPlayer.currentPosition
        }

        fun moveToPosition(position:Int){
            mediaPlayer.seekTo(position)
        }

        fun isMediaPlaying():Boolean{
            if (mediaPlayer.isPlaying){
                return true
            }
                return false
        }

        fun getMusicDetails():MusicList{
            var i:MusicList = musicLists.get(num)
            return i
        }

        /*fun getmusicLists(list:MutableList<MusicList>){
            MusicService().musicLists = LitePal.findAll(MusicList::class.java)
        }*/

    }

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
