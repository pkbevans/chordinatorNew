package com.bondevans.chordinator.prefs;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.bondevans.chordinator.R;

public class ChordinatorPrefsFragment extends PreferenceFragmentCompat {
    //	private static final String TAG = "ChordinatorPrefsFragment";
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
