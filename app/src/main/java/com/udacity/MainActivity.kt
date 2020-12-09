package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.cancelNotifications
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(getString(R.string.download_status_channel_id),getString(R.string.channel_name))

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {

            custom_button.updateDownloadStatus(ButtonState.Clicked)

            when(rg_downloadable.checkedRadioButtonId){
                R.id.rb_glide ->{
                    download(URL_GLIDE,rb_glide.text.toString())
                }
                R.id.rb_loadApp->{

                    download(URL_GLIDE,rb_loadApp.text.toString())
                }
                R.id.rb_retrofit->{
                    download(URL_RETROFIT,rb_retrofit.text.toString())
                }
                else ->{
                    custom_button.updateDownloadStatus(ButtonState.Loading)
                    Toast.makeText(this,"Please select the file to download",Toast.LENGTH_SHORT).show()
                    custom_button.updateDownloadStatus(ButtonState.Completed)

                }
            }




        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val sharedPref = context?.getSharedPreferences("my_pref",Context.MODE_PRIVATE)

            val file_name = sharedPref?.getString(getString(R.string.shared_pref_file_name),"")
            Log.d("file_name",file_name.toString())

            val downloaded_data = DownloadDetail(file_name,id)

            custom_button.updateDownloadStatus(ButtonState.Completed)

            //show notification here
            val notificationManager = ContextCompat.getSystemService(
                    this@MainActivity ,
                    NotificationManager::class.java
            ) as NotificationManager
            notificationManager.sendNotification(getString(R.string.notification_description), applicationContext,downloaded_data)

        }
    }

    private fun download(url:String,file_name:String) {

        val sharedPref = this.getSharedPreferences("my_pref",Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.shared_pref_file_name), file_name)
            apply()
        }


        //cancel previous active notificaitons
        cancelNotifications()
        custom_button.updateDownloadStatus(ButtonState.Loading)

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun cancelNotifications() {
        val notificationManager =
                ContextCompat.getSystemService(
                        this,
                        NotificationManager::class.java
                ) as NotificationManager
        notificationManager.cancelNotifications()
    }

    companion object {
        private const val URL_STARTER_PROJECT =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
       private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }


    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    // TODO: Step 2.4 change importance
                    NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }


}
