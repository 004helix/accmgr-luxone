package mdm.tw.com.mdm.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import mdm.tw.com.mdm.MainActivity
import mdm.tw.com.mdm.R
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class MyService : Service() {
    private val channelId = "AccOnOffChannel"

    private lateinit var preferences: SharedPreferences

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

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

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
            "Acc Mgr Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )

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

    private fun handleIntent(intent: Intent) {
        if (intent.extras?.getBoolean("accoff", false) != true) {
            runScript("accon", "accon_script", "accon.sh")
        } else {
            runScript("accoff", "accoff_script", "accoff.sh")
        }
    }

    private fun runScript(param: String, dataParam: String, fileName: String) {
        val action = preferences.getString(param, null)
        val data = preferences.getString(dataParam, null)

        if (data == null || action == null || action == "off") {
            return
        }

        val file = File(this.filesDir, fileName)
        var overwrite = true

        try {
            val reader = FileReader(file)
            overwrite = reader.readText() != data
            reader.close()
        } catch (_: Exception) {
        }

        if (overwrite) {
            val writer = FileWriter(file, false)
            writer.write(data)
            writer.close()
        }

        if (action == "su") {
            Runtime.getRuntime().exec(arrayOf("su", "-s", "sh", "-c", "source ${file.path}"))
                .waitFor()
        } else {
            Runtime.getRuntime().exec(arrayOf("sh", "-c", "source ${file.path}")).waitFor()
        }
    }
}
