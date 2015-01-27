package eu.appbucket.rothar.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import eu.appbucket.rothar.R;

public class AboutActivity extends PreferenceActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
