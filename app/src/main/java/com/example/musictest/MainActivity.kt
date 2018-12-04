package com.example.musictest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.Log.e
import android.util.Log.i
import android.view.Menu
import android.view.MenuItem
import com.example.musictest.adapter.MusicListAdapter
import com.example.musictest.model.MusicList
import com.example.musictest.presenter.IMainPresenter
import com.example.musictest.presenter.MainPresenter
import com.example.musictest.view.MusicNavBottom
import com.example.scanmusic.ScanLocalMusic
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.music_nav_bottom.*
import org.litepal.LitePal
import org.litepal.tablemanager.Connector
import java.lang.Thread.interrupted
import java.lang.Thread.sleep
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(),IMainActivity {

    companion object {
        const val ADD_LIST = 0
        const val SET_SEEK_BAR = 1

        var status = false

        var handler:Handler = Handler()

        var thread:Thread = Thread(Runnable {
            while (true){
                while (status){
                    var message = Message.obtain()
                    message.arg1 = MusicNavBottom.musicBinder?.getCurrentPosition()!!
                    if (MusicNavBottom.musicBinder?.getDuration()!! != 0){
                        message.arg2 = (MusicNavBottom.musicBinder?.getCurrentPosition()!! * 100 / MusicNavBottom.musicBinder?.getDuration()!!).toInt()
                    }
                    //Log.e("thread",(MusicNavBottom.musicBinder?.getCurrentPosition()!! * 100 / MusicNavBottom.musicBinder?.getDuration()!!).toString())
                    //e("thread",MusicNavBottom.musicBinder?.getCurrentPosition()!!.toString())
                    //i("thread",MusicNavBottom.musicBinder?.getDuration()!!.toString())
                    message.what = SET_SEEK_BAR
                    handler.sendMessage(message)
                    sleep(1000)
                }
                sleep(500)
            }
        })

        fun threadStart(){
            status = true
            //Log.e("threadStart()",thread.isAlive.toString())
            if (thread != null && thread.isAlive){

            }else{
                thread.start()
            }
        }

        fun threadStop(){
            //Log.e("threadStop()",thread.isAlive.toString())
            if(status){
                status = false
            }
            //Log.e("threadStop()",thread.isAlive.toString())
        }
    }

    var musicNavBottom:MusicNavBottom? = null
    var listThread:Thread? = null
    private var adapter:MusicListAdapter? = null
    private var musicList:MutableList<MusicList> = ArrayList()
    private var scanLocalMusic:ScanLocalMusic = ScanLocalMusic()
    private var mainPresenter:IMainPresenter? = null
    private var time = SimpleDateFormat("mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Connector.getDatabase()

        setContentView(R.layout.activity_main)

        //Log.e("123",musicList.get(0).toString())
        mainPresenter = MainPresenter(this,this)

        initView()
        readList()
        handler = object : Handler(){
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                when(msg?.what){
                    ADD_LIST -> {
                        adapter?.reList(musicList)
                        adapter?.notifyDataSetChanged()
                    }
                    SET_SEEK_BAR -> {
                        //e("SET_SEEK_BAR",msg.arg2.toString())
                        music_use_time.setText(time.format(msg.arg1))
                        seekBar.setProgress(msg.arg2)
                        music_time.setText(time.format(MusicNavBottom.musicBinder?.getDuration()))
                    }
                    else->{

                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.add_music -> {
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    val manifest:Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(this,manifest,1)
                } else {
                    Thread(Runnable {
                        musicList.clear()
                        musicList = scanLocalMusic.getMusicList(this)
                        if (musicList != null || musicList.size != 0){
                            mainPresenter!!.saveMusicList(musicList)
                            MusicNavBottom.musicBinder!!.addMusicLists()
                            var musicNavBottom = MusicNavBottom(this)
                            musicNavBottom.musicStatus(MusicNavBottom.MUSIC_PAUSE)
                            threadStop()
                            var message = Message()
                            message.what = ADD_LIST
                            handler.sendMessage(message)
                        }
                    }).start()
                }
            }
            else -> {

            }
        }
        return true
    }

    private fun initView(){
        setSupportActionBar(toolbar_main)
        var manager = LinearLayoutManager(this)
        music_list.layoutManager = manager
        adapter = MusicListAdapter(this, musicList)
        music_list.adapter = adapter
    }

    private fun readList(){
        listThread = Thread(Runnable {
            musicList = mainPresenter!!.readList()
            if (adapter != null){
                var message = Message()
                message.what = ADD_LIST
                handler.sendMessage(message)
            }
        })
        if (!listThread?.isAlive!!){
            listThread?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!MusicNavBottom.musicBinder?.isMediaPlaying()!!){
            var intent = Intent(this,MusicService::class.java)
            stopService(intent)
        }
        MusicNavBottom.musicBinder = null
        unbindService(MusicNavBottom.serviceConnection)
        threadStop()
    }
}
