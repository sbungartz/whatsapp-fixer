package de.sbungartz.whatsappfixer.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import de.sbungartz.whatsappfixer.R;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
             .replace(android.R.id.content, new SettingsFragment())
             .commit();


        //EditText intervalInput = (EditText) findViewById(R.id.editText);
        //CheckBox enabledInput = (CheckBox) findViewById(R.id.checkBox);
        //SharedPreferences prefs = getSharedPreferences(Heartbeats.PREFS_NAME, Context.MODE_PRIVATE);
        //intervalInput.setText(Long.toString(prefs.getLong(Heartbeats.PREF_GCM_TRIGGER_INTERVAL, Heartbeats.DEFAULT_GCM_TRIGGER_INTERVAL)));
        //enabledInput.setChecked(prefs.getBoolean("enabled", true));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Context context = getApplicationContext();
        Heartbeats.registerNextAlarm(context);
        WhatsappRestarting.registerNextAlarm(context);
    }

    public void sendHeartbeatClicked(View view) {
        Heartbeats.triggerHeartbeat(getApplicationContext());
        Toast.makeText(this, "Triggered Heartbeat", Toast.LENGTH_SHORT).show();
    }

    public void saveSettingsClicked(View view) {
        EditText intervalInput = (EditText) findViewById(R.id.editText);
        CheckBox enabledInput = (CheckBox) findViewById(R.id.checkBox);
        SharedPreferences prefs = getSharedPreferences(Heartbeats.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("interval", Long.parseLong(intervalInput.getText().toString()));
        editor.putBoolean("enabled", enabledInput.isChecked());
        editor.commit();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        Heartbeats.registerNextAlarm(getApplicationContext());
    }

    public void restartWhatsappClicked(View view) {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("com.whatsapp");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
