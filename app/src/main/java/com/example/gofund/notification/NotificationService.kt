package com.example.gofund.notification

import android.Manifest // Import needed
import android.content.Context
import android.content.pm.PackageManager // Import needed
import android.os.Build // Import needed
import android.util.Log // Import needed
import androidx.core.app.ActivityCompat // Import needed
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat // Use Compat version
import com.example.gofund.R
import kotlin.random.Random // For generating a random ID

class NotificationService(
    private val context : Context
) {
    fun showNotification(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationService", "POST_NOTIFICATIONS permission not granted. Cannot show notification.")
            return
        }
        // -----------------------------------------------------

        val notificationId = Random.nextInt()

        // 1. Build the notification object
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_vector) // Ensure this drawable exists
            .setContentTitle("Fund Limit Reminder")
            .setContentText("Spending limit reached. Please review your budget.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set priority (should match channel importance)
            .build() // <-- Finalize the notification object

        // 2. Get the NotificationManagerCompat instance
        val notificationManager = NotificationManagerCompat.from(context)

        // 3. Issue the notification
        notificationManager.notify(notificationId, notification) // <-- Actually show it!

        Log.d("NotificationService", "Notification attempt with ID: $notificationId")

    }

    companion object {
        // Channel ID must match the one created in the Application class
        const val CHANNEL_ID = "channel_id"
    }
}