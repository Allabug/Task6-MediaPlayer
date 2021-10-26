package com.rsschool.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rsschool.R
import com.rsschool.adapter.SongAdapter
import com.rsschool.databinding.FragmentMusicListBinding
import com.rsschool.helper.Constants.FILE_NAME
import com.rsschool.model.Song
import java.io.IOException


class MusicListFragment : Fragment(R.layout.fragment_music_list) {

    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    private var songList: MutableList<Song> = ArrayList()
    private lateinit var songAdapter: SongAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setActionBar(binding.toolbar)
        loadSong()
        setUpRecyclerView()
    }

    private fun getJSONDataFromAsset(): String {
        var jsonString = ""
        try {
            jsonString =
                requireContext().assets.open(FILE_NAME).bufferedReader().use { it.readText() }
        } catch (ex: IOException) {
            Log.e("TAG", "Could not read file from assets, e")
            ex.printStackTrace()
            return jsonString
        }
        return jsonString
    }

    private fun getListOfAllSongs(): MutableList<Song> {
        var songs: MutableList<Song> = ArrayList()
        try {
            val jsonFileString = getJSONDataFromAsset()
            Log.i("TAG", jsonFileString)
            val gson = Gson()
            val listSongType = object : TypeToken<MutableList<Song>>() {}.type
            songs = gson.fromJson(jsonFileString, listSongType)
            songs.forEachIndexed { idx, song -> Log.i("TAG", "> Item $idx:\n$song   url =${song.songUri} ") }
        } catch (ex: Exception) {
            return songs
        }
        return songs
    }

    private fun loadSong() {
        songList = getListOfAllSongs()
        Log.i("TAG", "size = ${songList.size}")
    }

    private fun setUpRecyclerView() {
        songAdapter = SongAdapter()
        binding.rvSongList.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = songAdapter
            addItemDecoration(object : DividerItemDecoration(
                activity, LinearLayout.VERTICAL
            ) {})
        }
        songAdapter.differ.submitList(songList)
//        songList.clear()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}