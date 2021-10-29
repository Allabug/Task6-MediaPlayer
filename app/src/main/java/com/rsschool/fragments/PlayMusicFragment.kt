package com.rsschool.fragments


import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.rsschool.R
import com.rsschool.databinding.FragmentPlayMusicBinding
import com.rsschool.helper.Status
import com.rsschool.model.Song
import com.rsschool.service.isPlaying
import com.rsschool.service.toSong
import com.rsschool.viewmodel.MainViewModel
import com.rsschool.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PlayMusicFragment : Fragment(R.layout.fragment_play_music) {

    @Inject
    lateinit var glide: RequestManager

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel: SongViewModel by viewModels()

    private var curPlayingSong: Song? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    private var _binding: FragmentPlayMusicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayMusicBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()

        binding.ibPlay.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        binding.ibPreviousSong.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }

        binding.ibNextSong.setOnClickListener {
            mainViewModel.skipToNextSong()
        }

        binding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        setCurPlayerTimeToTextView(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    shouldUpdateSeekbar = false
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    binding.seekBar.let {
                        mainViewModel.seekTo(it.progress.toLong())
                        shouldUpdateSeekbar = true
                    }
                }
            }
        )
    }

    private fun updateTitleAndSongImage(song: Song) {
        binding.tvTitle.text = song.songTitle
        binding.tvAuthor.text = song.songArtist
        glide.load(song.bitmapUri).into(binding.ibCover)
    }

    private fun subscribeToObservers() {
            mainViewModel.curPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            curPlayingSong = it.toSong()
            updateTitleAndSongImage(curPlayingSong!!)
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner) {
            playbackState = it
            binding.ibPlay.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
            binding.seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner) {
            if (shouldUpdateSeekbar) {
                binding.seekBar.progress = it.toInt()
                setCurPlayerTimeToTextView(it)
            }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner) {
            binding.seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvDuration.text = dateFormat.format(it)
        }
    }

    private fun setCurPlayerTimeToTextView(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        binding.tvCurrentTime.text = dateFormat.format(ms)
    }

}