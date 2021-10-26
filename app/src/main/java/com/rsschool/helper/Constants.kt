package com.rsschool.helper

import android.content.Context
import android.widget.Toast
import java.util.concurrent.TimeUnit

object Constants {

    const val FILE_NAME = "playlist.json"
    fun Context.toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG)
            .show()
    }

    fun durationConverter(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
    }

    const val NETWORK_FAILURE = "com.rsschool.android.uamp.media.session.NETWORK_FAILURE"
}