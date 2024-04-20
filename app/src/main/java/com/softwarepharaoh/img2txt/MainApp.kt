package com.softwarepharaoh.img2txt

import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainApp : Application() {
//    var instance: MainApplication? = null

    @Override
    override fun onCreate() {
        super.onCreate()
//        instance = this
        copyTessDataForTextRecognizer()
    }

    private fun tessDataPath(): String {
//        return MainApplication().instance!!.getExternalFilesDir(null).toString() + "/tessdata/"
        return applicationContext.getExternalFilesDir(null).toString() + "/tessdata/"
    }

//    fun getTessDataParentDirectory(): String {
////        return MainApplication().instance!!.getExternalFilesDir(null)!!.absolutePath
//        return applicationContext.getExternalFilesDir(null)!!.absolutePath
//    }

    private fun copyTessDataForTextRecognizer() {
        val run = Runnable {
            copyFiles("ara.traineddata")
            copyFiles("eng.traineddata")
            //copyFiles("eng.traineddata");
            //copyFiles("pdf.ttf");
        }
        Thread(run).start()
    }

    private fun copyFiles(fname: String) {
        val assetManager: AssetManager = applicationContext.assets //MainApplication().instance.assets
        var out: OutputStream? = null
        try {
            val inp = assetManager.open(fname)
            val tessPath = tessDataPath()
            val tessFolder = File(tessPath)
            if (!tessFolder.exists()) tessFolder.mkdir()
            val tessData = "$tessPath/$fname"
            val tessFile = File(tessData)
            if (!tessFile.exists()) {
                out = FileOutputStream(tessData)
                val buffer = ByteArray(1024)
                var read = inp.read(buffer)
                while (read != -1) {
                    out.write(buffer, 0, read)
                    read = inp.read(buffer)
                }
                Log.d("MainApplication", " Did finish copy tess file  ")
            } else Log.d("MainApplication", " tess file exist  ")
        } catch (e: Exception) {
            Log.d("MainApplication", "couldn't copy with the following error : $e")
        } finally {
            try {
                out?.close()
            } catch (exx: Exception) {
                //do nothing
            }
        }
    }
}