package com.example.musictest.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.example.musictest.MainActivity
import com.example.musictest.MusicService
import com.example.musictest.view.MusicNavBottom

class ListenerReceiver : BroadcastReceiver {

    var context1:Context? = null

    var musicBinder = MusicService.MusicBinder()

    constructor(context1: Context?) : super() {
        this.context1 = context1
    }

    override fun onReceive(context: Context, intent: Intent) {

        var musicNavBottom = MusicNavBottom(context1)

        var str = intent.action
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(str)){
            if(musicBinder.isMediaPlaying()){
                musicBinder.pauseMusic()
                musicNavBottom.musicStatus(MusicNavBottom.MUSIC_PAUSE)
                MainActivity.threadStop()
            }
        }
    }
}
