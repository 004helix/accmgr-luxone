package mdm.tw.com.mdm

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import mdm.tw.com.mdm.services.MyService


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (preferences.getString("accon_script", null) == null) {
            val data = StringBuilder()
            data.append("am start-service --ez immediatestart true com.mendhak.gpslogger/.GpsLoggingService\n")
            data.append("am start-service -a action.UartBrocastReceive com.tpms3/com.tl.tpms.service.UartService\n")

            val ed = preferences.edit()
            ed.putString("accon_script", data.toString())
            ed.apply()
        }

        if (preferences.getString("accoff_script", null) == null) {
            val data = StringBuilder()
            data.append("am start-service --ez immediatestop true com.mendhak.gpslogger/.GpsLoggingService\n")

            val ed = preferences.edit()
            ed.putString("accoff_script", data.toString())
            ed.apply()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }
}
