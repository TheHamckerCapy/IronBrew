package com.example.beerbicep.AdditionalComponents

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

//data class which contains all of the actions for text to speech
data class TtsController(
    val speak: (String) -> Unit,//speaks the given string , if already running restarts
    val stop: () -> Unit,//stops the current speech
    val isSpeaking: Boolean//used for state management for the engine
)

//takes in setting which contains pitch and rate for text to speech
@Composable
fun rememberTextToSpeech(setting: TtsSetting): TtsController {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    //initializes text to speech engine and clears resources as the composable gets destroyed
    DisposableEffect(context) {
        val textTospeech = TextToSpeech(context) { status ->

            if (status == TextToSpeech.SUCCESS) {

            }

        }

        textTospeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                isSpeaking = false
            }

            override fun onError(utteranceId: String?) {
                isSpeaking = false
            }
        })
        tts = textTospeech

        onDispose {
            textTospeech.stop()
            textTospeech.shutdown()
        }
    }
    //update the pitch and rate of the text to speech engine
    LaunchedEffect(tts, setting) {
        tts?.let {
            it.setPitch(setting.pitch)
            it.setSpeechRate(setting.rate)
        }
    }
    return TtsController(
        speak = { text ->
            tts?.let {
                if (it.isSpeaking) {
                    tts?.stop()
                    isSpeaking = false
                } else {
                    it.setSpeechRate(setting.rate)
                    it.setPitch(setting.pitch)
                    it.speak(text, TextToSpeech.QUEUE_FLUSH, null, "beer_disc")
                    isSpeaking = true
                }
            }

        },
        stop = {
            tts?.stop()
            isSpeaking = false
        },
        isSpeaking = isSpeaking
    )

}