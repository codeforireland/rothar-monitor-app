package eu.appbucket.rothar.ui;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;

public class SettingsFragment extends PreferenceFragment /*implements OnSharedPreferenceChangeListener*/ {
	
	@Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.about);
        loadDataToScreen();
    }
	
    private void loadDataToScreen()  {
    	ConfigurationManager config = new ConfigurationManager(this.getActivity());
    	((EditTextPreference) findPreference(
    			getResources().getString(R.string.pref_key_app_uuid)))
    			.setSummary(config.getApplicationUuid());
    	((EditTextPreference) findPreference(
    			getResources().getString(R.string.pref_key_app_version)))
    			.setSummary(getApplicationVersion());
    	((EditTextPreference) findPreference(
    			getResources().getString(R.string.pref_key_tag_id)))
    			.setSummary(config.getAssetId().toString());
    	((EditTextPreference) findPreference(
    			getResources().getString(R.string.pref_key_tag_code)))
    			.setSummary(config.getAssetCode());
    }
    
    private String getApplicationVersion() {
    	String versionName;
    	try {
    		versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "version name not set";
		}
    	return versionName;
    }
}
