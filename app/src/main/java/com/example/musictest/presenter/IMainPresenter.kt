package com.example.musictest.presenter

import com.example.musictest.model.MusicList

interface IMainPresenter {

    fun readList():MutableList<MusicList>

    fun saveMusicList(musicList:MutableList<MusicList>)

}