package com.rsschool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.rsschool.databinding.SongBinding
import com.rsschool.fragments.MusicListFragmentDirections
import com.rsschool.model.Song
import javax.inject.Inject


class SongAdapter @Inject constructor(private val glide: RequestManager) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var binding: SongBinding? = null

    inner class SongViewHolder(itemBinding: SongBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object :
        DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return newItem.hashCode() == oldItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        binding = SongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SongViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currSong = differ.currentList[position]
        binding?.apply { glide.load(currSong.bitmapUri).into(songImage) }

        holder.itemView.apply {
            binding?.songTitle?.text = currSong.songTitle
            binding?.songArtist?.text = currSong.songArtist
            binding?.tvOrder?.text = (position + 1).toString()
            setOnClickListener { mView ->
                val direction = MusicListFragmentDirections
                    .actionMusicListFragmentToPlayMusicFragment()
                mView.findNavController().navigate(direction)
                onItemClickListener?.let { click ->
                    click(currSong)
                }
            }
        }
    }

    private var onItemClickListener: ((Song) -> Unit)? = null
    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount() = songs.size
}