package com.example.swipeapps

import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.speech.RecognitionService
import android.content.Intent

class AssistService : VoiceInteractionService()

class AssistSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession = FavSession(this)
}

class DummyRecognition : RecognitionService() {
    override fun onStartListening(intent: Intent?, callback: Callback?) {}
    override fun onCancel(callback: Callback?) {}
    override fun onStopListening(callback: Callback?) {}
}
