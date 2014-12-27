package eu.appbucket.rothar.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import eu.appbucket.rothar.R;
import eu.appbucket.rothar.ui.manager.TagManager;

public class TagActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tag, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.mark_stolen) {
			new TagManager(this).updateBikeMarkStolen();
			return true;
		} else if (id == R.id.mark_recovered) {
			new TagManager(this).updateBikeMarkRecovered();
			return true;
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
