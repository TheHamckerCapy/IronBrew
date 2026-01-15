package com.example.beerbicep

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


data class TtsController(
    val speak: (String)-> Unit,
    val stop: ()-> Unit,
    val isSpeaking: Boolean
)

@Composable
fun rememberTextToSpeech(): TtsController {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isSpeaking by remember { mutableStateOf(false) }


    DisposableEffect(context) {
        val textTospeech = TextToSpeech(context){status->

            if(status==TextToSpeech.SUCCESS){

            }

        }
        textTospeech.setOnUtteranceProgressListener(object :UtteranceProgressListener(){
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                // This resets the icon when the paragraph ends
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
    return TtsController(
        speak = {text->
            tts?.let {
                if (it.isSpeaking){
                        tts?.stop()
                    isSpeaking=false
                }else{
                    it.speak(text,TextToSpeech.QUEUE_FLUSH,null,"beer_disc")
                    isSpeaking=true
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