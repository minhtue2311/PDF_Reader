package com.example.pdf_reader.model

import android.graphics.Bitmap
import java.io.Serializable

data class PDF(
    val title: String,
    val date: Long,
    val size: Long,
    val data: String,
    val preview : Bitmap? = null
) : Serializable