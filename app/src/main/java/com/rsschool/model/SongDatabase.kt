package com.rsschool.model

import android.content.res.AssetManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rsschool.helper.Constants
import java.io.IOException
import javax.inject.Inject


class SongDatabase @Inject constructor(private val assetManager: AssetManager) {

    var songList: List<Song>? = null

    init {
        getListOfAllSongs()
    }

    private fun getJSONDataFromAsset(): String {
        var jsonString = ""
        try {
            jsonString =
                assetManager.open(Constants.FILE_NAME).bufferedReader()
                    .use { it.readText() }
        } catch (ex: IOException) {
            Log.e(TAG, "Could not read file from assets, e")
            ex.printStackTrace()
            return jsonString
        }
        return jsonString
    }

    private fun getListOfAllSongs(): List<Song>? {

        try {
            val jsonFileString = getJSONDataFromAsset()
            Log.i("TAG", jsonFileString)
            val gson = Gson()
            val listSongType = object : TypeToken<MutableList<Song>>() {}.type
            songList = gson.fromJson(jsonFileString, listSongType)
            songList?.forEachIndexed { idx, song ->
                Log.i(
                    TAG,
                    "> Item $idx:\n$song   url =${song.songUri} "
                )
            }
        } catch (ex: Exception) {
            return emptyList()
        }
        return songList
    }
}
private const val TAG = "SongDatabase"