package it.jaschke.alexandria;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by saj on 27/01/15.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // set summary
        Preference preference = findPreference(getString(R.string.pref_startFragment_key));
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return setPreferenceSummary( preference,  o);
            }
        });
        setPreferenceSummary(preference,PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "0"));

    }
    public boolean setPreferenceSummary(Preference preference, Object o) {
        String stringValue = o.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
        return true;
    }
}
