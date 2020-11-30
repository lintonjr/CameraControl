package com.equipe9.cameracontrol

import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }


    private fun handleMessage(remoteMessage: RemoteMessage) {

        val handler = android.os.Handler(Looper.getMainLooper())

        handler.post(Runnable {
            Toast.makeText(
                baseContext, "Your device is receiving owner configuration",
                Toast.LENGTH_LONG
            ).show()

            Log.d(TAG, "From: ${remoteMessage.from}")

            // Check if message contains a data payload.
            if (remoteMessage.data.isNotEmpty()) {
                Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            }

            // Check if message contains a notification payload.
            remoteMessage.notification?.let {
                Log.d(TAG, "Message Notification Body: ${it.body}")
            }

            remoteMessage.notification?.let {
                val intent = Intent("MyData")
                Log.d("Message On Boarding", it.body.toString())
                intent.putExtra("message", it.body);

                broadcaster?.sendBroadcast(intent);
            }
        })
    }

    companion object {
        private const val TAG = "FIREBASEMESSAGE"
    }
}