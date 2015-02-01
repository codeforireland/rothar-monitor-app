package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_about);
    	loadDataToScreen();
    }
    
    private void loadDataToScreen()  {
    	ConfigurationManager config = new ConfigurationManager(this);
    	((TextView) findViewById(R.id.application_id)).setText(config.getApplicationUuid());
    	((TextView) findViewById(R.id.application_version)).setText(getApplicationVersionNumber());
    	((TextView) findViewById(R.id.tag_id)).setText(config.getAssetId().toString());
    	((TextView) findViewById(R.id.tag_code)).setText(config.getAssetCode());
    }
    
    private String getApplicationVersionNumber() {
    	String versionName;
    	try {
    		versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "version name not set";
		}
    	return versionName;
    }
}
