package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.DetailActivity.Companion.DOWNLOAD_STATUS_EXTRA
import com.udacity.DownloadDetail
import com.udacity.MainActivity
import com.udacity.R


private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, data: DownloadDetail) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val statusIntent = Intent(applicationContext, DetailActivity::class.java)
    statusIntent.putExtra(DOWNLOAD_STATUS_EXTRA,data)
    val statusPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            statusIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_status_channel_id)
    )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            //when user taps on the notification it dismisses itself.
            .setAutoCancel(true)
            .addAction(R.drawable.ic_assistant_black_24dp,
                    "Check the status",
                    statusPendingIntent
                    )

    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
