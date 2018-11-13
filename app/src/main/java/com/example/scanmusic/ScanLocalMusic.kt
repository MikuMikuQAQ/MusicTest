package com.example.scanmusic

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.MediaStore
import android.util.Log
import com.example.musictest.model.MusicList

class ScanLocalMusic {

    fun getMusicList(context:Context):MutableList<MusicList>{
        var cursor:Cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)
        var lists:MutableList<MusicList> = ArrayList()
        if (cursor.moveToFirst()){
            do {
                var list = MusicList(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)))
                //Log.e("123",list.toString())
                lists.add(list)
            }while (cursor.moveToNext())
            cursor.close()
        }
        return lists
    }

}