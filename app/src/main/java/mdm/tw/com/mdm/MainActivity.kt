package mdm.tw.com.mdm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import mdm.tw.com.mdm.services.MyService


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, MyService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        finish()
    }
}
