package com.example.beerbicep.AdditionalComponents

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class TtsSetting(
    val pitch: Float = 1.0f,
    val rate: Float = 1.0f
)
//singleton so that only one instance is created and the data gets shared globally across the app
@Singleton
class TtsManager @Inject constructor(){
    private val _setting = MutableStateFlow(TtsSetting())
    val setting = _setting.asStateFlow()//exposed to ui


    fun updatePitch(newPitch: Float) {
        _setting.update { it.copy(pitch = newPitch) }
    }

    fun updateRate(newRate: Float) {
        _setting.update { it.copy(rate = newRate) }
    }
}