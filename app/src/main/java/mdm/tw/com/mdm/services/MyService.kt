package mdm.tw.com.mdm.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import mdm.tw.com.mdm.R


class MyService : Service() {
    private val channelId = "AccOnOffChannel"

    // com.fyt.boot.ACCON receiver
    private var onReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val serviceIntent = Intent(context, MyService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    // com.fyt.boot.ACCOFF receiver
    private var offReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val serviceIntent = Intent(context, MyService::class.java)
            serviceIntent.putExtra("accoff", true)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        ContextCompat.registerReceiver(
            this,
            onReceiver,
            IntentFilter("com.fyt.boot.ACCON"),
            ContextCompat.RECEIVER_EXPORTED
        )

        ContextCompat.registerReceiver(
            this,
            offReceiver,
            IntentFilter("com.fyt.boot.ACCOFF"),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(offReceiver)
        unregisterReceiver(onReceiver)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // start foreground service
        val serviceChannel = NotificationChannel(
            channelId,
            "Acc On/Off Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder
                .setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        val notification = notificationBuilder.build()

        startForeground(1, notification)

        handleIntent(intent)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun handleIntent(intent: Intent)
    {
        if (intent.extras?.getBoolean("accoff", false) != true) {
            startServices()
        } else {
            stopServices()
        }
    }

    private fun startServices() {
        Runtime.getRuntime().exec(
            arrayOf(
                "su", "-s",
                "sh", "-c",
                "am start-service --ez immediatestart true com.mendhak.gpslogger/.GpsLoggingService; " +
                "am start-service -a action.UartBrocastReceive com.tpms3/com.tl.tpms.service.UartService"
            )
        ).waitFor()
    }

    private fun stopServices() {
        startService(
            Intent()
                .setComponent(
                    ComponentName(
                        "com.mendhak.gpslogger",
                        "com.mendhak.gpslogger.GpsLoggingService"
                    )
                )
                .putExtra("immediatestop", true)
        )
    }
}
