package com.example.musictest.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log.e
import com.example.musictest.MainActivity
import com.example.musictest.MusicService
import com.example.musictest.view.MusicNavBottom

class PlayReceiver : BroadcastReceiver {

    var context1:Context? = null

    var musicBinder = MusicService.MusicBinder()

    constructor(context1: Context?) : super() {
        this.context1 = context1
    }

    override fun onReceive(context: Context, intent: Intent) {

        var musicNavBottom = MusicNavBottom(context1)

        var num = intent.getStringExtra("playNum")
        when(num.toInt()){
            0 -> {
                //e("onReceive","0")
                if(!musicBinder.isMediaPlaying()){
                    musicBinder.playMusic()
                    musicNavBottom.musicStatus(MusicNavBottom.MUSIC_PLAYING)
                    MainActivity.threadStart()
                }
            }
            1 -> {
                //e("onReceive","1")
                if(musicBinder.isMediaPlaying()){
                    musicBinder.pauseMusic()
                    musicNavBottom.musicStatus(MusicNavBottom.MUSIC_PAUSE)
                    MainActivity.threadStop()
                }
            }
            2 -> {
                //e("onReceive","2")
                musicBinder.nextMusic(true)
            }
            /*3 -> {
                e("onReceive","3")
                musicBinder.previousMusic()
            }*/
            else -> {

            }
        }
    }
}
