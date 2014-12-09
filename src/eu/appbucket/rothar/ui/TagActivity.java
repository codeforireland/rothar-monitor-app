package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.task.UpdateTask;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

public class TagActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
	}
	
	public void onStatusChanged(View view) {
		boolean isStolen = ((ToggleButton) view).isChecked();
		if(isStolen) {
			updateBikeMarkStolen();
		} else {
			updateBikeMarkRecovered();
		}
	}
	
	private void updateBikeMarkStolen() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(this).getAssetId());
		asset.setStatus(AssetStatus.STOLEN);
		new UpdateTask(this).execute(new AssetData[]{asset});
	}
	
	private void updateBikeMarkRecovered() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(this).getAssetId());
		asset.setStatus(AssetStatus.RECOVERED);
		new UpdateTask(this).execute(new AssetData[]{asset});
	}
}
