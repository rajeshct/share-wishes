package bestwishes.startup.com.wishes.utils

import android.graphics.Bitmap
import android.os.Environment
import bestwishes.startup.com.wishes.BuildConfig
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by rajesh on 10/12/17.
 */

class CacheStore private constructor() {
    private val cacheDir = "/Android/data/${BuildConfig.APPLICATION_ID}/share/"

    init {
        val fullCacheDir = File(Environment.getExternalStorageDirectory().toString(), cacheDir)
        if (!fullCacheDir.exists())
            cleanCacheStart()
    }

    private fun cleanCacheStart() {
        val fullCacheDir = File(Environment.getExternalStorageDirectory().toString(), cacheDir)
        fullCacheDir.mkdirs()
        val noMedia = File(fullCacheDir.toString(), ".nomedia")
        try {
            noMedia.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun saveCacheFile(image: Bitmap): String {
        var fileName = ""
        val fullCacheDir = File(Environment.getExternalStorageDirectory().toString(), cacheDir)
        val fileLocalName = SimpleDateFormat("ddMMyyhhmmssSSS", Locale.ENGLISH)
                .format(java.util.Date()) + ".jpeg"
        val fileUri = File(fullCacheDir.toString(), fileLocalName)
        try {
            val outStream = FileOutputStream(fileUri)
            image.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
            outStream.flush()
            outStream.close()
            fileName = fileUri.absolutePath
        } catch (e: Exception) {
            //
        }

        return fileName
    }


    private object Holder {
        val INSTANCE = synchronized(CacheStore::class.java) {
            CacheStore()
        }
        val cachedImage = mutableMapOf<String, String>()
    }

    companion object {
        val instance: CacheStore by lazy { Holder.INSTANCE }
        val cachedImages: MutableMap<String, String> by lazy { Holder.cachedImage }
    }
}
