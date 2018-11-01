package com.example.musictest

import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.musictest.adapter.MusicListAdapter
import com.example.musictest.model.MusicList
import com.example.scanmusic.ScanLocalMusic
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.tablemanager.Connector

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_LIST = 0
    }

    private var adapter:MusicListAdapter? = null
    private var musicList:MutableList<MusicList> = ArrayList()
    private var scanLocalMusic:ScanLocalMusic = ScanLocalMusic()

    private var handler:Handler = object:Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){
                ADD_LIST -> {
                    adapter?.reList(musicList)
                    adapter?.notifyDataSetChanged()
                }
                else->{

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Connector.getDatabase()

        setContentView(R.layout.activity_main)

        //Log.e("123",musicList.get(0).toString())

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.add_music -> {
                Thread(Runnable {
                    musicList.clear()
                    musicList = scanLocalMusic.getMusicList(this)
                    var message = Message()
                    message.what = ADD_LIST
                    handler.sendMessage(message)
                }).start()
            }
            else -> {

            }
        }
        return true
    }

    private fun initView(){
        setSupportActionBar(toolbar_main)
        var manager:LinearLayoutManager = LinearLayoutManager(this)
        music_list.layoutManager = manager
        adapter = MusicListAdapter(this, musicList)
        music_list.adapter = adapter
    }
}
