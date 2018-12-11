package com.example.musictest

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.musictest.model.MusicList
import com.example.musictest.receiver.ListenerReceiver
import com.example.musictest.receiver.PlayReceiver
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                    musicBinder.showMusicControllerBar()
                }
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
            const val level = NotificationManager.IMPORTANCE_DEFAULT
            const val LIST_CYCLE = 0
            const val RANDOM_PLAY = 1
            const val SINGLE_CYCLE = 2
            var playNum = 0
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
            playNum = 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                musicBinder.showMusicControllerBar()
            }
            //Log.e("playMusic","do not start")
        }

        fun pauseMusic(){
            if (mediaPlayer.isPlaying){
                //Log.e("pauseMusic","pause")
                mediaPlayer.pause()
            }
            playNum = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                musicBinder.showMusicControllerBar()
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
            //playNum = 2
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                musicBinder.showMusicControllerBar()
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
            //playNum = 3
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                musicBinder.creatChannel(channelId, channelName, MusicBinder.level)
                musicBinder.showMusicControllerBar()
            }
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
            channel.setSound(null,null)
            var manager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        @TargetApi(Build.VERSION_CODES.O)
        fun showMusicControllerBar(){

            var intent = Intent("com.example.musictest.PLAY_RECEIVER")

            var manager:NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                yzMsg(manager.getNotificationChannel(MusicService.channelId))
            }
            var view = RemoteViews(context?.packageName,R.layout.notification_music_bar)
            view.setTextViewText(R.id.bar_music,musicLists.get(num).musicName)
            view.setTextViewText(R.id.bar_song,musicLists.get(num).songName)
            if(isMediaPlaying()){
                intent.putExtra("playNum", "1")
                var pendingIntentPlay = PendingIntent.getBroadcast(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                view.setViewVisibility(R.id.bar_play, View.GONE)
                view.setViewVisibility(R.id.bar_pause,View.VISIBLE)
                view.setOnClickPendingIntent(R.id.bar_pause,pendingIntentPlay)
            }else{
                intent.putExtra("playNum", "0")
                var pendingIntentPause = PendingIntent.getBroadcast(context,2,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                view.setViewVisibility(R.id.bar_play, View.VISIBLE)
                view.setViewVisibility(R.id.bar_pause,View.GONE)
                view.setOnClickPendingIntent(R.id.bar_play,pendingIntentPause)
            }

            intent.putExtra("playNum", "2")
            var pendingIntentNext = PendingIntent.getBroadcast(context,3,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            view.setOnClickPendingIntent(R.id.bar_next,pendingIntentNext)

            /*intent.putExtra("playNum", "3")
            var pendingIntentPrevious = PendingIntent.getBroadcast(context,3,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            view.setOnClickPendingIntent(R.id.bar_previous,pendingIntentPrevious)*/

            var style = Notification.MediaStyle()
            var notification = Notification.Builder(context,MusicService.channelId)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setCustomContentView(view)
                    .setSmallIcon(R.mipmap.ic_launcher)
            manager.notify(1, notification.build())
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
        var receiver = PlayReceiver(musicBinder.context)
        var intentFilter = IntentFilter()
        intentFilter.addAction("com.example.musictest.PLAY_RECEIVER")
        this.registerReceiver(receiver,intentFilter)

        var receiverListener = ListenerReceiver(musicBinder.context)
        var intentFilter1 = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        this.registerReceiver(receiverListener,intentFilter1)
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
