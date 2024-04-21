package mdm.tw.com.mdm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import mdm.tw.com.mdm.services.MyService

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("accon_test")
            ?.setOnPreferenceClickListener {
                val serviceIntent = Intent(it.context, MyService::class.java)
                ContextCompat.startForegroundService(it.context, serviceIntent)
                Toast.makeText(it.context, "ACC ON", Toast.LENGTH_SHORT).show()
                true
            }

        findPreference<Preference>("accoff_test")
            ?.setOnPreferenceClickListener {
                val serviceIntent = Intent(it.context, MyService::class.java)
                serviceIntent.putExtra("accoff", true)
                ContextCompat.startForegroundService(it.context, serviceIntent)
                Toast.makeText(it.context, "ACC OFF", Toast.LENGTH_SHORT).show()
                true
            }
    }
}
