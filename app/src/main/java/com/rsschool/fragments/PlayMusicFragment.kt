package com.rsschool.fragments

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rsschool.R
import com.rsschool.databinding.FragmentPlayMusicBinding
import com.rsschool.helper.Constants
import com.rsschool.model.Song

private const val TAG = "PlayMusicFragment"

class PlayMusicFragment : Fragment() {
    private var _binding: FragmentPlayMusicBinding? = null
    private val binding get() = _binding!!
    private val args: PlayMusicFragmentArgs by navArgs()
    private var mMediaPlayer: MediaPlayer? = null
    private var songsList: Array<Song> = arrayOf()
    private lateinit var song: Song
    private var currentPosition = 0
    private var seekLength: Int = 0

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
        songsList = args.listOfSongs
        currentPosition = args.currentPosition
        song = songsList[currentPosition]

        downloadImage()

        Log.d(TAG, "position = $currentPosition, $song")
//        mMediaPlayer = MediaPlayer()
        mMediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        binding.tvTitle.text = song.songTitle
        binding.tvDuration.text = Constants.durationConverter(song.songDuration!!.toLong())
        binding.tvAuthor.text = song.songArtist

        binding.ibPlay.setOnClickListener {
            playSong()
        }

        binding.ibNextSong.setOnClickListener {
            TODO()
        }

        binding.ibPreviousSong.setOnClickListener {
            TODO()
        }
    }

    private fun downloadImage() {

        binding.apply {
            Glide.with(this@PlayMusicFragment)
                .load(song.bitmapUri)
                .error(R.drawable.ic_cover)
                .into(ibCover)
        }
    }


    private fun playSong() {
        if (!mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(song.songUri)
            mMediaPlayer!!.prepare()
            mMediaPlayer!!.seekTo(seekLength)
            mMediaPlayer!!.start()

            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_pause
                )
            )
            updateSeekBar()
        } else {
            mMediaPlayer!!.pause()
            seekLength = mMediaPlayer!!.currentPosition
            binding.ibPlay.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!,
                    R.drawable.ic_play
                )
            )
        }
    }

    private fun updateSeekBar() {
        if (mMediaPlayer != null) {
            binding.tvCurrentTime.text =
                Constants.durationConverter(mMediaPlayer!!.currentPosition.toLong())
        }
        seekBarSetUp()
        Handler(Looper.getMainLooper()).postDelayed(runnable, 50)
    }

    private var runnable = Runnable { updateSeekBar() }

    private fun seekBarSetUp() {

        if (mMediaPlayer != null) {
            binding.seekBar.progress = mMediaPlayer!!.currentPosition
            binding.seekBar.max = mMediaPlayer!!.duration
        }
        binding.seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    mMediaPlayer!!.seekTo(progress)
                    binding.tvCurrentTime.text = Constants.durationConverter(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                    if (seekBar != null) {
                        mMediaPlayer!!.seekTo(seekBar.progress)
                    }
                }
            }
        })
    }


    private fun clearMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.stop()
            }
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

//    override fun onStop() {
//        super.onStop()
//        playSong()
//    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

}