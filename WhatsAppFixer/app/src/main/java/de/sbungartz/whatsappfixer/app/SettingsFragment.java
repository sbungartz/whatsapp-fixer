package de.sbungartz.whatsappfixer.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import de.sbungartz.whatsappfixer.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final Context context = getActivity();

        findPreference("gcm.heartbeat.now").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Heartbeats.triggerHeartbeat(context);
                Toast.makeText(context, "Triggered Heartbeat", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        findPreference("whatsapp.restarting.now").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                WhatsappRestarting.restartNow(context);
                Toast.makeText(context, "Triggered Restart", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        updatePositiveLongEditTextSummary(prefs, "gcm.heartbeat.interval");
        updatePositiveLongEditTextSummary(prefs, "whatsapp.restarting.interval");
        updatePositiveLongEditTextSummary(prefs, "whatsapp.restarting.wakeduration");
    }

    private void updatePositiveLongEditTextSummary(SharedPreferences prefs, String key) {
        EditTextPreference pref = (EditTextPreference) findPreference(key);
        try {
            long val = Long.parseLong(prefs.getString(key, ""));
            if(val > 0) {
                pref.setSummary(Long.toString(val) + " ms");
            } else {
                pref.setSummary("Value must be greater than 0.");
            }
        } catch(NumberFormatException e) {
            pref.setSummary("Value must be an integer greater than 0.");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if(key.equals("gcm.heartbeat.interval") || key.equals("whatsapp.restarting.interval") || key.equals("whatsapp.restarting.wakeduration")) {
            updatePositiveLongEditTextSummary(prefs, key);
        }

        Context context = getActivity();

        Heartbeats.registerNextAlarm(context);
        WhatsappRestarting.registerNextAlarm(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
