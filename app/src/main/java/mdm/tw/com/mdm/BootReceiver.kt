package mdm.tw.com.mdm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import mdm.tw.com.mdm.services.MyService


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MyService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
