package com.rsschool.service

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.rsschool.model.SongDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicSource @Inject constructor(
    private val musicDatabase: SongDatabase
) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun getMediaData() = withContext(Dispatchers.IO) {
        state = State.INITIALIZING
        val allSongs = musicDatabase.songList
        songs = allSongs!!.map { song ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.songArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, song.songArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, song.songArtist)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.bitmapUri)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.bitmapUri)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.songUri)
                .build()
        }
        state = State.INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = State.CREATED
        set(value) {
            if (value == State.INITIALIZED || value == State.ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == State.CREATED || state == State.INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == State.INITIALIZED)
            true
        }
    }

    enum class State {
        CREATED,
        INITIALIZING,
        INITIALIZED,
        ERROR
    }
}