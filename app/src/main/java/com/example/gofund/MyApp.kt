package com.example.gofund

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.gofund.notification.NotificationService

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                NotificationService.CHANNEL_ID,
                "FundLimit",
                NotificationManager.IMPORTANCE_HIGH,
            )
            channel.description = "Fund Limit reminder"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}