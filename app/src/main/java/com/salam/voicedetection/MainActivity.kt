package com.salam.voicedetection

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val AUDIO_RECORDER_FOLDER = "AudioRecorder"
    private lateinit var recorder: MediaRecorder
    private val output_formats = intArrayOf(MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP)
    private val currentFormat = 0
    private val AUDIO_RECORDER_FILE_EXT_3GP = ".3gp"
    private val AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4"
    private val file_exts = arrayOf<String>(AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP)

    var currentFile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                    this,
                    "no recogniser",
                    Toast.LENGTH_LONG
            ).show()
        }

        findViewById<Button>(R.id.btn_speech).setOnClickListener{
          //  askSpeechInput()
            startRecording()

        }

        findViewById<Button>(R.id.btn_play).setOnClickListener{
            stopRecording()
            playarcoding(currentFile)
        }

    }


    private fun askSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
        startActivityForResult(intent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            findViewById<TextView>(R.id.tv_speech_text).text = results?.toString().toString()
            Log.d("TAG-R", results?.toString().toString())
            stopRecording()
        }
    }


    private fun getFilename(): String? {
        val filepath: String = Environment.getExternalStorageDirectory().getPath()
        val file = File(filepath, AUDIO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        currentFile = file.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + file_exts.get(currentFormat)
        return currentFile
    }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(output_formats.get(currentFormat))
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(getFilename())
        recorder.setOnErrorListener(errorListener)
        recorder.setOnInfoListener(infoListener)
        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (null != recorder) {
            recorder.stop()
            recorder.reset()
            recorder.release()
        }
    }

    @Throws(IOException::class)
    fun playarcoding(path: String?) {
        val mp = MediaPlayer()
        mp.setDataSource(path)
        mp.prepare()
        mp.start()
        mp.setVolume(10f, 10f)
    }


    private val errorListener = MediaRecorder.OnErrorListener { mr, what, extra ->Log.d("--------", "-------------ERROROROROR--------------") }

    private val infoListener = MediaRecorder.OnInfoListener { mr, what, extra -> Log.d("--------", "-------------WARNING--------------")}

}