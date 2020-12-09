package com.udacity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


       if(intent.hasExtra(DOWNLOAD_STATUS_EXTRA)){
           val status = intent.getParcelableExtra<DownloadDetail>(DOWNLOAD_STATUS_EXTRA)
           Log.d("download_status",status.toString())


           if(status != null){
               if(!status.downloadId?.equals(-1)!!){
                   tv_status.text = "Successful"
               }else{
                   tv_status.text = "Fail"
                   tv_status.setTextColor(getColor(R.color.error))
               }

                   tv_file_name.text = status.fileName

           }
       }

        btn_ok.setOnClickListener {
            //go to main activity
            startActivity(Intent(this,MainActivity::class.java))
        }




    }

    companion object  {
        const val DOWNLOAD_STATUS_EXTRA = "download_status_extra"
    }

}
