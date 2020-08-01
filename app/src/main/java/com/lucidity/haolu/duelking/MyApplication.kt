package com.lucidity.haolu.duelking

import android.app.Application
import androidx.room.Room
import com.lucidity.haolu.Card
import com.lucidity.haolu.CardList
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


/**
 * Used to initialize Realm on application start
 */
class MyApplication : Application() {

    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext,
                AppDatabase::class.java, "tcg")
                .build()

        scope.launch {
            populateTcgDatabase()
        }

//        scope.launch {
//            println(db.cardDao().getAllContainsSubstring("Blue%eyes"))
//        }

//
//        // Check if the database is loaded, if not then load it from raw directory
//        if (!fileFound("tcg.realm", this.filesDir)) {
//            Log.d("MyApplication", "FILE NOT FOUND")
//            copyBundledRealmFile(this.resources.openRawResource(R.raw.tcg), "tcg.realm")
//        }
//
//        // Config the Realm
//        Realm.init(this)
//        val config = RealmConfiguration.Builder()
//                .name("tcg.realm")
//                .build()
//
//        Realm.setDefaultConfiguration(config)
    }

//    private fun isTcgDatabaseInitialized(): Boolean {
//
//    }


    // TODO: move to searchmodule
     private suspend fun populateTcgDatabase() {
        val json = loadJsonFromRawResource(R.raw.database)
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val newAdapter = moshi.adapter<CardList>(CardList::class.java)
        val cardList = newAdapter.fromJson(json!!)

        cardList?.data?.let { list ->
            for (card in list) {
                db.cardDao().insert(Card(card))
            }
        }
    }

    private fun loadJsonFromRawResource(rawSourceId: Int): String? {
        return try {
            val inputStream = resources.openRawResource(rawSourceId)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
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
