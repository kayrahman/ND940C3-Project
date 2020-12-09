package com.udacity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class DownloadDetail(val fileName : String?,
                          val downloadId : Long?
                          ):Parcelable