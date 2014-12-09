package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
	
	/*public void onStatusChanged(View view) {
		boolean isStolen = ((ToggleButton) view).isChecked();
		if(isStolen) {
			updateBikeMarkStolen();
		} else {
			updateBikeMarkRecovered();
		}
	}*/
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tag, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.mark_stolen) {
			updateBikeMarkStolen();
			return true;
		} else if (id == R.id.mark_recovered) {
			updateBikeMarkRecovered();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
