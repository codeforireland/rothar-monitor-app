package eu.appbucket.rothar.ui.manager;

import android.content.Context;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.task.UpdateTagTask;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

public class TagManager {
	
	private Context context;
	
	public TagManager(Context context) {
		this.context = context;
	}
	
	public void updateBikeMarkStolen() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(context).getAssetId());
		asset.setStatus(AssetStatus.STOLEN);
		new UpdateTagTask(context).execute(new AssetData[]{asset});
	}
	
	public void updateBikeMarkRecovered() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(context).getAssetId());
		asset.setStatus(AssetStatus.RECOVERED);
		new UpdateTagTask(context).execute(new AssetData[]{asset});
	}
}
