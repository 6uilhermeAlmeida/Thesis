package com.example.thesis.command

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class OpenYoutubeCommand(private val context: Context) {
    fun open(key: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$key"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$key"))
        try {
            context.startActivity(appIntent)
        } catch (exception : ActivityNotFoundException) {
            context.startActivity(webIntent)
        }

    }
}