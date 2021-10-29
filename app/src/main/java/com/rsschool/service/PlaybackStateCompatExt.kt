package com.rsschool.service

import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.rsschool.model.Song

inline val PlaybackStateCompat.isPrepared
    get() = state == PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_PAUSED

inline val PlaybackStateCompat.isPlaying
    get() = state == PlaybackStateCompat.STATE_BUFFERING ||
            state == PlaybackStateCompat.STATE_PLAYING

inline val PlaybackStateCompat.isPlayEnabled
    get() = actions and PlaybackStateCompat.ACTION_PLAY != 0L ||
            (actions and PlaybackStateCompat.ACTION_PLAY != 0L &&
                    state == PlaybackStateCompat.STATE_PAUSED)

inline val PlaybackStateCompat.currentPlaybackPosition: Long
    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDifference = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (timeDifference * playbackSpeed + position).toLong()
    } else position

inline val PlaybackStateCompat.currentplaybackPosition: Long

    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else position

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            it.mediaId?.toInt() ?: 0,
            it.title.toString(),
            it.subtitle.toString(),
            it.iconUri.toString(),
            it.mediaUri.toString(),
        )
    }
}

