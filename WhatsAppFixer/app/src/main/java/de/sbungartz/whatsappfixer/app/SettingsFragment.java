package de.sbungartz.whatsappfixer.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.sbungartz.whatsappfixer.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final Set<String> POSITIVE_LONG_EDIT_TEXT_PREFERENCES = new HashSet<String>(Arrays.asList(
            "gcm.heartbeat.interval",
            "whatsapp.restarting.interval.mobile",
            "whatsapp.restarting.interval.wifi",
            "whatsapp.restarting.wakeduration"
    ));

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
    }

    private void updateLastTriggerTime(SharedPreferences prefs, String keyPrefix) {
        long lastTimeMillis = prefs.getLong(keyPrefix + ".lasttime", 0);

        Preference pref = findPreference(keyPrefix + ".now");
        if(lastTimeMillis == 0) {
            pref.setSummary("Never triggered");
        } else {
            Calendar c = new GregorianCalendar();
            c.setTimeInMillis(lastTimeMillis);
            pref.setSummary(String.format("Last triggered on %1$tF at %1$tT", c));
        }
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

    private static final Pattern LASTTIME_KEY = Pattern.compile("(.*)\\.lasttime");

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Matcher matcher = LASTTIME_KEY.matcher(key);
        if(matcher.matches()) {
            String keyPrefix = matcher.group(1);
            updateLastTriggerTime(prefs, keyPrefix);
        } else {
            if (POSITIVE_LONG_EDIT_TEXT_PREFERENCES.contains(key)) {
                updatePositiveLongEditTextSummary(prefs, key);
            }

            Context context = getActivity();

            Heartbeats.registerNextAlarm(context);
            WhatsappRestarting.registerNextAlarm(context);

            Toast.makeText(context, "Updated background services.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        for(String key : POSITIVE_LONG_EDIT_TEXT_PREFERENCES) {
            updatePositiveLongEditTextSummary(prefs, key);
        }

        updateLastTriggerTime(prefs, "gcm.heartbeat");
        updateLastTriggerTime(prefs, "whatsapp.restarting");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
