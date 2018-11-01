package com.example.musictest.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.example.musictest.R
import kotlinx.android.synthetic.main.music_nav_bottom.view.*

class MusicNavBottom : ConstraintLayout, View.OnClickListener {

    val MUSIC_PAUSE:Int = 0
    val MUSIC_PLAYING:Int = 1

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.image_play -> {
                musicStatus(MUSIC_PLAYING)
            }
            R.id.image_pause -> {
                musicStatus(MUSIC_PAUSE)
            }
            R.id.big_image_play -> {
                musicStatus(MUSIC_PLAYING)
            }
            R.id.big_image_pause -> {
                musicStatus(MUSIC_PAUSE)
            }
            R.id.little_controler -> {
                little_controler.visibility = View.GONE
                big_controler.visibility = View.VISIBLE
            }
            R.id.gone_view -> {
                big_controler.visibility = View.GONE
                little_controler.visibility = View.VISIBLE
            }
            else -> {

            }
        }
    }

    constructor(context: Context?) : super(context){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        initView(view)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        initView(view)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        var view:View = View.inflate(getContext(), R.layout.music_nav_bottom,this)
        initView(view)
    }

    private fun initView(v:View){
        image_play.setOnClickListener(this)
        image_pause.setOnClickListener(this)
        big_image_pause.setOnClickListener(this)
        big_image_play.setOnClickListener(this)
        little_controler.setOnClickListener(this)
        gone_view.setOnClickListener(this)
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