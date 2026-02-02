package com.example.beerbicep.presentation.Detail

sealed class DetailEvents {
    data object ToggleFav : DetailEvents()
    data object RetryDetail : DetailEvents()
}