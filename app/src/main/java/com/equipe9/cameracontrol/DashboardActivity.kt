package com.equipe9.cameracontrol

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_main.*

class DashboardActivity : AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
//        var cameraManager = getSystemService(CameraManager::class.java)
//        if(!cameraManager.statusHefesto) {
//            disableCamera()
//        } else {
//            enableCamera()
//        }

        tokenBtn.setOnClickListener {

            if (checkGooglePlayServices()) {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        // 2
                        if (!task.isSuccessful) {
                            Log.w(TAG, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }
                        // 3
                        val token = task.result?.token

                        // 4
                        val msg = getString(R.string.token_prefix, token)
                        Log.d(TAG, msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    })
            } else {
                //You won't be able to send notifications to this device
                Log.w(TAG, "Device doesn't have google play services")
            }
        }

        var btnCam = btnRequestAccess

        btnCam.setOnClickListener {
            enableCamera()
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
            IntentFilter("MyData")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NewApi")
        override fun onReceive(context: Context?, intent: Intent) {
            var cameraManager = getSystemService(CameraManager::class.java)
            val firebaseMessage = intent.extras?.getString("message")
            Log.d(TAG, firebaseMessage.toString())
            if(firebaseMessage == "On") {
//                cameraManager.setHefesto(false)
                enableCamera()
            } else if (firebaseMessage == "Off") {
//                cameraManager.setHefesto(false)
                disableCamera()
            }
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        // 1
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // 2
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error")
            // ask user to update google play services and manage the error.
            false
        } else {
            // 3
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    fun disableCamera() {
        val statusText = "Your camera has been disabled by the owner"
        val statusSpannableString = SpannableString(statusText)
        val statusRed = ForegroundColorSpan(Color.RED)
        val statusBold = StyleSpan(Typeface.BOLD)
        statusSpannableString.setSpan(statusRed, 21, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        statusSpannableString.setSpan(statusBold, 21, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        cameraStatusText.text = statusSpannableString
        cameraStatusText.setText(statusSpannableString)
    }

    fun enableCamera() {
        val statusText = "Your camera has been enabled by the owner"
        val statusSpannableString = SpannableString(statusText)
        val statusGreen = ForegroundColorSpan(Color.GREEN)
        val statusBold = StyleSpan(Typeface.BOLD)
        statusSpannableString.setSpan(statusGreen, 21, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        statusSpannableString.setSpan(statusBold, 21, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        cameraStatusText.text = statusSpannableString
        cameraStatusText.setText(statusSpannableString)
    }

    companion object {
        private const val TAG = "DashboardActivity"
    }
}