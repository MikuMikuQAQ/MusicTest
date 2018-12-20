package com.example.musictest.presenter

import android.content.Context
import com.example.musictest.IMainActivity
import com.example.musictest.model.MusicList
import org.litepal.LitePal

class MainPresenter : IMainPresenter {

    private var context:Context
    private var mainActivity:IMainActivity

    constructor(context: Context?, mainActivity: IMainActivity?) {
        this.context = context!!
        this.mainActivity = mainActivity!!
    }

    override fun readList(): MutableList<MusicList>{
        var musicList:MutableList<MusicList> = LitePal.findAll(MusicList::class.java)
        return musicList
    }

    override fun saveMusicList(musicList: MutableList<MusicList>) {
        LitePal.deleteAll(MusicList::class.java)
        LitePal.saveAll(musicList)
    }

}