package com.smike.headphonesms;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {
  @Override
  protected void onCreate(Bundle state){
    super.onCreate(state);
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  protected void onResume() {
    super.onResume();

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    updateView(sharedPreferences);

    sharedPreferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
      @Override
      public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        OnOffAppWidgetProvider.update(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
          BackupManager.dataChanged(getPackageName());
        }

        onContentChanged();
        updateView(sharedPreferences);
      }
    });
  }

  // TODO(smike): Find a cleaner way to update preferences that might have been changed in
  // elsewhere. There must be a way to do all prefs automatically.
  private void updateView(SharedPreferences sharedPreferences) {
    String enabledKey = getString(R.string.prefsKey_enabled);
    CheckBoxPreference checkBoxPreference = (CheckBoxPreference)this.findPreference(enabledKey);
    boolean enabledValue = sharedPreferences.getBoolean(enabledKey, false);
    checkBoxPreference.setChecked(enabledValue);

    String activationModeKey = getString(R.string.prefsKey_activationMode);
    ListPreference listPreference = (ListPreference)this.findPreference(activationModeKey);
    listPreference.setValue(sharedPreferences.getString(activationModeKey, null));
    listPreference.setEnabled(enabledValue);

    String volumeKey = getString(R.string.prefsKey_volume);
    DialogPreference dialogPreference = (DialogPreference)this.findPreference(volumeKey);
    dialogPreference.setDialogLayoutResource(R.layout.volume_dialog);
  }
}
