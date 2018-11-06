package com.example.musictest.view

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.example.musictest.MainActivity
import com.example.musictest.MusicService
import com.example.musictest.R
import kotlinx.android.synthetic.main.music_nav_bottom.view.*

class MusicNavBottom : ConstraintLayout, View.OnClickListener {

    companion object {
        var musicBinder:MusicService.MusicBinder? = null
        var serviceConnection:ServiceConnection = object : ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                musicBinder = service as MusicService.MusicBinder?
            }

        }
        const val MUSIC_PAUSE:Int = 0
        const val MUSIC_PLAYING:Int = 1
        fun sendMusicId(){
            
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.image_play -> {
                musicStatus(MUSIC_PLAYING)
                musicBinder?.playMusic()
            }
            R.id.image_pause -> {
                musicStatus(MUSIC_PAUSE)
                musicBinder?.pauseMusic()
            }
            R.id.big_image_play -> {
                musicStatus(MUSIC_PLAYING)
                musicBinder?.playMusic()
            }
            R.id.big_image_pause -> {
                musicStatus(MUSIC_PAUSE)
                musicBinder?.pauseMusic()
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
                musicBinder?.nextMusic()
                musicStatus(MUSIC_PLAYING)
            }
            R.id.big_image_next_track -> {
                musicBinder?.nextMusic()
                musicStatus(MUSIC_PLAYING)
            }
            R.id.image_previous -> {
                musicBinder?.previousMusic()
                musicStatus(MUSIC_PLAYING)
            }
            R.id.big_image_previous -> {
                musicBinder?.previousMusic()
                musicStatus(MUSIC_PLAYING)
            }
            else -> {

            }
        }
    }

    constructor(context: Context?) : super(context){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        getPermission()
        initView(view)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        getPermission()
        initView(view)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        getPermission()
        initView(view)
    }

    private fun getPermission(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            val manifest:Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(context as MainActivity,manifest,1)
        } else {
            val intent = Intent(context,MusicService::class.java)
            context.bindService(intent,serviceConnection,BIND_AUTO_CREATE)
        }
    }

    private fun initView(v:View){
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
    }

    fun musicStatus(i:Int){
        when(i){
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
}