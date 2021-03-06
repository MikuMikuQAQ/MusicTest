package com.example.musictest.view

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log.e
import android.view.View
import android.widget.SeekBar
import com.example.musictest.MainActivity
import com.example.musictest.MusicService
import com.example.musictest.MusicService.MusicBinder.Companion.LIST_CYCLE
import com.example.musictest.MusicService.MusicBinder.Companion.RANDOM_PLAY
import com.example.musictest.MusicService.MusicBinder.Companion.SINGLE_CYCLE
import com.example.musictest.MusicService.MusicBinder.Companion.playBack
import com.example.musictest.R
import kotlinx.android.synthetic.main.music_nav_bottom.view.*
import java.lang.Thread.interrupted
import java.lang.Thread.sleep
import java.text.SimpleDateFormat

class MusicNavBottom : ConstraintLayout, View.OnClickListener {

    private var time = SimpleDateFormat("mm:ss")

    companion object {
        lateinit var musicBinder: MusicService.MusicBinder
        lateinit var serviceConnection: ServiceConnection
        const val MUSIC_PAUSE: Int = 0
        const val MUSIC_PLAYING: Int = 1
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.image_play -> {
                //e("musiclist",MusicService.musicLists.size.toString())
                if(MusicService.musicLists.size > 0){
                    musicStatus(MUSIC_PLAYING)
                    musicBinder?.playMusic()
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.image_pause -> {
                musicStatus(MUSIC_PAUSE)
                musicBinder?.pauseMusic()
                MainActivity.threadStop()
            }
            R.id.big_image_play -> {
                if(MusicService.musicLists.size > 0){
                    musicStatus(MUSIC_PLAYING)
                    musicBinder?.playMusic()
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.big_image_pause -> {
                musicStatus(MUSIC_PAUSE)
                musicBinder?.pauseMusic()
                MainActivity.threadStop()
            }
            R.id.little_controler -> {
                little_controler.visibility = View.GONE
                big_controler.visibility = View.VISIBLE
            }
            R.id.gone_view -> {
                big_controler.visibility = View.GONE
                little_controler.visibility = View.VISIBLE
            }
            R.id.image_next_track -> {
                if(MusicService.musicLists.size > 0) {
                    MainActivity.threadStop()
                    musicBinder?.nextMusic(true)
                    musicStatus(MUSIC_PLAYING)
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.big_image_next_track -> {
                if(MusicService.musicLists.size > 0) {
                    MainActivity.threadStop()
                    musicBinder?.nextMusic(true)
                    musicStatus(MUSIC_PLAYING)
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.image_previous -> {
                if(MusicService.musicLists.size > 0) {
                    MainActivity.threadStop()
                    musicBinder?.previousMusic()
                    musicStatus(MUSIC_PLAYING)
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.big_image_previous -> {
                if(MusicService.musicLists.size > 0) {
                    MainActivity.threadStop()
                    musicBinder?.previousMusic()
                    musicStatus(MUSIC_PLAYING)
                    MainActivity.threadStart()
                    showMusicDetails()
                }
            }
            R.id.big_list_cycle -> {
                playBack = SINGLE_CYCLE
                big_list_cycle.visibility = View.GONE
                big_single_cycle.visibility = View.VISIBLE
            }
            R.id.big_random_play -> {
                playBack = LIST_CYCLE
                big_random_play.visibility = View.GONE
                big_list_cycle.visibility = View.VISIBLE
            }
            R.id.big_single_cycle -> {
                playBack = RANDOM_PLAY
                big_single_cycle.visibility = View.GONE
                big_random_play.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    private var seekBarChange = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                if (MusicService.musicLists.size > 0) {
                    //e("progres", (progress * musicBinder?.getDuration()!! / 100).toString())
                    musicBinder?.moveToPosition(progress * musicBinder?.getDuration()!! / 100)
                    music_use_time.setText(time.format(progress * musicBinder?.getDuration()!! / 100))
                }
            }
            if (progress == 0) {
                if (MusicService.musicLists.size > 0) {
                    showMusicDetails()
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            if (MusicService.musicLists.size > 0) {
                if (musicBinder?.isMediaPlaying()!!) MainActivity.threadStop()
                musicStatus(MUSIC_PAUSE)
                musicBinder?.pauseMusic()
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            if (MusicService.musicLists.size > 0) {
                musicStatus(MUSIC_PLAYING)
                musicBinder?.playMusic()
                MainActivity.threadStart()
                showMusicDetails()
            } else {
                seekBar?.progress = 0
            }
        }

    }

    constructor(context: Context?) : super(context) {
        var view: View = View.inflate(getContext(), R.layout.music_nav_bottom, this)
        getPermission()
        initView(view)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        var view: View = View.inflate(getContext(), R.layout.music_nav_bottom, this)
        getPermission()
        initView(view)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        var view: View = View.inflate(getContext(), R.layout.music_nav_bottom, this)
        getPermission()
        initView(view)
    }

    private fun getPermission() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicBinder = service as MusicService.MusicBinder
                getBackgroundMedia()
            }

        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val manifest: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(context as MainActivity, manifest, 1)
        } else {
            val intent = Intent(context, MusicService::class.java)
            context.startService(intent)
            context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun initView(v: View) {
        image_play.setOnClickListener(this)
        image_pause.setOnClickListener(this)
        big_image_pause.setOnClickListener(this)
        big_image_play.setOnClickListener(this)
        little_controler.setOnClickListener(this)
        gone_view.setOnClickListener(this)
        image_next_track.setOnClickListener(this)
        big_image_next_track.setOnClickListener(this)
        image_previous.setOnClickListener(this)
        big_image_previous.setOnClickListener(this)
        big_list_cycle.setOnClickListener(this)
        big_random_play.setOnClickListener(this)
        big_single_cycle.setOnClickListener(this)
        seekBar.setOnSeekBarChangeListener(seekBarChange)
    }

    fun musicStatus(i: Int) {
        when (i) {
            MUSIC_PAUSE -> {
                image_pause.visibility = View.GONE
                big_image_pause.visibility = View.GONE
                image_play.visibility = View.VISIBLE
                big_image_play.visibility = View.VISIBLE
            }
            MUSIC_PLAYING -> {
                image_play.visibility = View.GONE
                big_image_play.visibility = View.GONE
                image_pause.visibility = View.VISIBLE
                big_image_pause.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    fun showMusicDetails() {
        var musicDetail = musicBinder?.getMusicDetails()!!
        music_name.setText(musicDetail.musicName)
        music_songname.setText(musicDetail.songName)
        big_music_name.setText(musicDetail.musicName)
        big_music_song.setText(musicDetail.songName)
        getAlubmImage(musicDetail.albumId)
    }

    fun getAlubmImage(i: Int) {
        var uri = "content://media/external/audio/albums"
        var strings: Array<String> = arrayOf("album_art")
        var cursor = context.contentResolver.query(Uri.parse(uri + "/" + i.toString()), strings, null, null, null)
        if (cursor.count > 0 && cursor.columnCount > 0) {
            cursor.moveToNext()
            if (cursor.getString(0) == null) {

            } else {
                var bitmap = BitmapFactory.decodeFile(cursor.getString(0))
                var bitmapDrawable = BitmapDrawable(Resources.getSystem(), bitmap)
                image_music.setImageDrawable(bitmapDrawable)
                big_image_music.setImageDrawable(bitmapDrawable)
            }
        }
        cursor.close()
    }

    fun getBackgroundMedia() {
        //e("getBackgroundMedia","start")
        if (musicBinder != null) {
            if (musicBinder?.isMediaPlaying()!!) {
                //e("getBackgroundMedia","ok")
                musicStatus(MUSIC_PLAYING)
                MainActivity.threadStart()
                showMusicDetails()
            }
        }
    }
}