package com.example.musictest.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.musictest.R
import com.example.musictest.model.MusicList
import kotlinx.android.synthetic.main.item_music.view.*

class MusicListAdapter : RecyclerView.Adapter<MusicListAdapter.ViewHodel> {

    var context:Context? = null
    var musicLists:MutableList<MusicList> = ArrayList()

    constructor(context: Context?, musicLists: MutableList<MusicList>) : super() {
        this.context = context
        this.musicLists = musicLists
    }

    fun reList(musicLists: MutableList<MusicList>){
        this.musicLists = musicLists
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHodel {
        var v:View = LayoutInflater.from(context).inflate(R.layout.item_music,p0,false)
        var hodel:ViewHodel = ViewHodel(v)
        return hodel
    }

    override fun getItemCount(): Int {
        return musicLists?.size
    }

    override fun onBindViewHolder(p0: ViewHodel, p1: Int) {
        with(p0.itemView){
            item_music_name.text = musicLists.get(p1).musicName
            item_song_name.text = musicLists.get(p1).songName
            item_music_uri.text = musicLists.get(p1).musicUri
        }
    }

    class ViewHodel(itemView: View) : RecyclerView.ViewHolder(itemView)
}