package com.audio.baja.utils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.util.*


object FileDir {
    fun getRootDirPath(context: Context): String {
        return if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.getApplicationContext(),
                null
            )[0]
            file.getAbsolutePath()
        } else {
            context.getApplicationContext().getFilesDir().getAbsolutePath()
        }
    }

    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return java.lang.String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }
}