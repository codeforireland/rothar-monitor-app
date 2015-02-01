package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.wearable.Asset;

import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.listener.TagRegisterListener;
import eu.appbucket.rothar.ui.task.tag.RegisterTagTask;
import eu.appbucket.rothar.web.domain.asset.AssetData;

public class RegisterActivity extends Activity implements TagRegisterListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	public void registerTagByCode(View view) {
		String tagCode = ((EditText) findViewById(R.id.tag_code)).getText().toString();
		Toast.makeText(this, "Registering tag code: " + tagCode, Toast.LENGTH_SHORT).show();
		new RegisterTagTask(this, this).execute(tagCode);
	}

	@Override
	public void onTagRegisterSuccess(AssetData asset) {
		Toast.makeText(this, "Tag activated.", Toast.LENGTH_SHORT).show();
		saveAssetInPreferences(asset);
		startMapActivity();
	}

	private void saveAssetInPreferences(AssetData asset) {
		ConfigurationManager conf = new ConfigurationManager(this);
		conf.saveAssetId(asset.getAssetId());
		String tagCode = ((EditText) findViewById(R.id.tag_code)).getText().toString();
		conf.saveAssetCode(tagCode);
	}
	
	private void startMapActivity() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onTagRegisterFailure(String cause) {
		Toast.makeText(this, cause, Toast.LENGTH_SHORT).show();
	}
}
