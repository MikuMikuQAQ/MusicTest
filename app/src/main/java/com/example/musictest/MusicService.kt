package com.example.musictest

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.example.musictest.model.MusicList
import org.litepal.LitePal
import java.util.*


class MusicService : Service {

    companion object {
        private var musicBinder = MusicBinder()
        var mediaPlayer = MediaPlayer()
        var musicLists:MutableList<MusicList> = ArrayList()
        private var num = 0
        private var oldNum = num

        const val channelId = "musicStyle"
        const val channelName = "音乐通知栏"

        private var musicStatus = object : MediaPlayer.OnCompletionListener{
            override fun onCompletion(mp: MediaPlayer?) {
                musicBinder.nextMusic(false)
            }

        }

        private var perpareListener = object : MediaPlayer.OnPreparedListener{
            override fun onPrepared(mp: MediaPlayer?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                    musicBinder.showMusicControllerBar()
                }
            }
        }

        fun musicLoading(i:Int){
            if(MusicService.musicLists.size > 0) {
                mediaPlayer.setDataSource(musicLists.get(i).musicUri)
            }
            mediaPlayer.prepare()
        }
    }

    constructor() : super(){
        musicLists = LitePal.findAll(MusicList::class.java)
        musicBinder.sendContext(this)
        musicLists = LitePal.findAll(MusicList::class.java)
        mediaPlayer.setOnCompletionListener(musicStatus)
        mediaPlayer.setOnPreparedListener(perpareListener)
    }


    class MusicBinder : Binder(){

        var context:Context? = null

        companion object {
            const val level = NotificationManager.IMPORTANCE_MIN
            const val LIST_CYCLE = 0
            const val RANDOM_PLAY = 1
            const val SINGLE_CYCLE = 2
            var playBack = LIST_CYCLE
        }

        fun addMusicLists(){
            if (mediaPlayer.isPlaying) {
                pauseMusic()
            }
            musicLists.clear()
            musicLists = LitePal.findAll(MusicList::class.java)
            resetMedia()
        }

        fun sendContext(context: Context){
            this.context = context
        }

        fun playMusic(){
            if(!mediaPlayer.isPlaying){
                //Log.e("playMusic","start")
                mediaPlayer.start()
            }
            //Log.e("playMusic","do not start")
        }

        fun pauseMusic(){
            if (mediaPlayer.isPlaying){
                //Log.e("pauseMusic","pause")
                mediaPlayer.pause()
            }
            //Log.e("pauseMusic","do not pause")
        }

        fun closeMedia(){
            if (mediaPlayer != null){
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }

        fun resetMedia(){
            if (!mediaPlayer.isPlaying){
                mediaPlayer.reset()
                musicLoading(num)
            }
        }

        fun nextMusic(fromUser:Boolean){
            mediaPlayer.stop()
            when(playBack){
                LIST_CYCLE -> {
                    if (mediaPlayer.isLooping)
                        mediaPlayer.isLooping = false
                    if (num < musicLists.size - 1)
                        num ++
                    else if(num == musicLists.size - 1)
                        num = 0
                    resetMedia()
                    playMusic()
                }
                RANDOM_PLAY -> {
                    var random = Random()
                    if (mediaPlayer.isLooping)
                        mediaPlayer.isLooping = false
                    oldNum = num
                    num = random.nextInt(musicLists.size)
                    resetMedia()
                    playMusic()
                }
                SINGLE_CYCLE -> {
                    if (!mediaPlayer.isLooping)
                        mediaPlayer.isLooping = true
                    if (fromUser) {
                        if (num < musicLists.size - 1)
                            num ++
                        else if(num == musicLists.size - 1)
                            num = 0
                        if (!mediaPlayer.isLooping)
                            mediaPlayer.isLooping = true
                    }
                    resetMedia()
                    playMusic()
                }
            }
        }

        fun previousMusic(){
            mediaPlayer.stop()
            when(playBack){
                LIST_CYCLE,SINGLE_CYCLE -> {
                    if (num > 0)
                        num --
                    else if(num == 0)
                        num = musicLists.size - 1
                }
                RANDOM_PLAY -> {
                    if (oldNum != num){
                        num = oldNum
                    } else {
                        var random = Random()
                        num = random.nextInt(musicLists.size)
                    }
                }
            }
            resetMedia()
            playMusic()
        }

        fun getDuration():Int{
            return mediaPlayer.duration
        }

        fun getCurrentPosition():Int{
            return mediaPlayer.currentPosition
        }

        fun moveToPosition(position:Int){
            mediaPlayer.seekTo(position)
        }

        fun isMediaPlaying():Boolean{
            if (mediaPlayer.isPlaying){
                return true
            }
                return false
        }

        fun getMusicDetails():MusicList{
            var i:MusicList = musicLists.get(num)
            return i
        }

        @TargetApi(Build.VERSION_CODES.O)
        fun creatChannel(id:String,name:String,level:Int){
            var channel = NotificationChannel(id,name,level)
            channel.setShowBadge(true)
            var manager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        @TargetApi(Build.VERSION_CODES.O)
        fun showMusicControllerBar(){
            var manager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                yzMsg(manager.getNotificationChannel(MusicService.channelId))
            }
            var style = Notification.MediaStyle()
            var notification: Notification = Notification.Builder(context,MusicService.channelId)
                    .setContentTitle(musicLists.get(num).musicName)
                    .setContentText(musicLists.get(num).songName)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context?.resources, R.mipmap.ic_launcher))
                    .addAction(Notification.Action(R.drawable.ic_previous,"",null))
                    .addAction(Notification.Action(R.drawable.ic_play,"",null))
                    .addAction(Notification.Action(R.drawable.ic_next_track,"",null))
                    .build()
            manager.notify(7, notification)
        }

        @TargetApi(Build.VERSION_CODES.O)
        fun yzMsg(channel: NotificationChannel){
            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                context?.startActivity(intent)
            }
        }

        /*fun getmusicLists(list:MutableList<MusicList>){
            MusicService().musicLists = LitePal.findAll(MusicList::class.java)
        }*/

    }

    override fun onBind(intent: Intent): IBinder {
        return musicBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
