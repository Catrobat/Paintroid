package org.catrobat.paintroid.web

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

class PlayStore {
    @SuppressWarnings("SwallowedException")
    fun openPlayStore(activity: Activity, applicationId: String) {
        val uriPlayStore = Uri.parse("market://details?id=$applicationId")
        val openPlayStore = Intent(Intent.ACTION_VIEW, uriPlayStore)
        try {
            activity.startActivity(openPlayStore)
        } catch (e: ActivityNotFoundException) {
            val uriNoPlayStore = Uri.parse("http://play.google.com/store/apps/details?id=$applicationId")
            val noPlayStoreInstalled = Intent(Intent.ACTION_VIEW, uriNoPlayStore)

            runCatching {
                activity.startActivity(noPlayStoreInstalled)
            }
        }
    }
}
