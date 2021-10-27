package com.rsschool.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Int,
    val songTitle: String?,
    val songArtist: String?,
    val songUri: String?,
    val songDuration: String?,
    val bitmapUri: String?
): Parcelable