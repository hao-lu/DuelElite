package com.example.haolu.duelmaster

import android.app.Application
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import io.realm.Realm
import io.realm.RealmConfiguration

// Used to initialize Realm database
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Check if the database is loaded, if not then load it from raw directory
        if (!fileFound("tcg.realm", this.filesDir)) {
            Log.d("MyApplication", "FILE NOT FOUND")
            copyBundledRealmFile(this.resources.openRawResource(R.raw.tcg), "tcg.realm")
        }
//        else {
//        Log.d("MyApplication", "FILE FOUND")
//        }

        // Config the Realm
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("tcg.realm")
                .build()

        Realm.setDefaultConfiguration(config)
    }

    private fun copyBundledRealmFile(inputStream: InputStream, outFileName: String): String? {
        try {
            val file = File(this.filesDir, outFileName)
            val outputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var bytesRead = inputStream.read(buf)
            while (bytesRead > 0) {
                outputStream.write(buf, 0, bytesRead)
                bytesRead = inputStream.read(buf)
            }
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    // Find the .realm file if it exist
    fun fileFound(name: String, file: File): Boolean {
        val list = file.listFiles()
        if (list != null) {
            for (f in list) {
                if (f.isDirectory()) fileFound(name, f)
                else if (name.equals(f.name, true)) return true
            }
        }
        return false
    }
}
