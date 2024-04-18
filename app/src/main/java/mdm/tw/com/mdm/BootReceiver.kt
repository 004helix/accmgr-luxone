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
        Log.i(context.resources.getString(R.string.log_tag), "Handle intent " + intent.action)
        val serviceIntent = Intent(context, MyService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
