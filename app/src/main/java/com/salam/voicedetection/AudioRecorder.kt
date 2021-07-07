package com.salam.voicedetection

import android.media.MediaPlayer

import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException


class AudioRecorder(path: String) {
    val recorder = MediaRecorder()
    val path: String
    private fun sanitizePath(path: String): String {

        var path = path
        if (!path.startsWith("/")) {
            path = "/$path"
        }
        if (!path.contains(".")) {
            path += ".3gp"
        }
        return Environment.getExternalStorageDirectory().absolutePath
                .toString() + path
    }

    @Throws(IOException::class)
    fun start() {
        val state = Environment.getExternalStorageState()
        if (state != Environment.MEDIA_MOUNTED) {
            throw IOException("SD Card is not mounted.  It is " + state
                    + ".")
        }

        // make sure the directory we plan to store the recording in exists
        val directory: File = File(path).getParentFile()
        if (!directory.exists() && !directory.mkdirs()) {
            throw IOException("Path to file could not be created.")
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(path)
        recorder.prepare()
        recorder.start()
    }

    @Throws(IOException::class)
    fun stop() {
        recorder.stop()
        recorder.release()
    }

    @Throws(IOException::class)
    fun playarcoding(path: String?) {
        val mp = MediaPlayer()
        mp.setDataSource(path)
        mp.prepare()
        mp.start()
        mp.setVolume(10f, 10f)
    }

    init {
        this.path = sanitizePath(path)
    }
}