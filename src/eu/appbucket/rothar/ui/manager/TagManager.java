package eu.appbucket.rothar.ui.manager;

import android.content.Context;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.ui.listener.TagUpdateListener;
import eu.appbucket.rothar.ui.task.UpdateTagTask;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

public class TagManager {
	
	private Context context;
	private TagUpdateListener listener;
	
	public TagManager(Context context, TagUpdateListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	public void updateBikeMarkStolen() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(context).getAssetId());
		asset.setStatus(AssetStatus.STOLEN);
		new UpdateTagTask(context, listener).execute(new AssetData[]{asset});
	}
	
	public void updateBikeMarkRecovered() {
		AssetData asset = new AssetData();
		asset.setAssetId(new ConfigurationManager(context).getAssetId());
		asset.setStatus(AssetStatus.RECOVERED);
		new UpdateTagTask(context, listener).execute(new AssetData[]{asset});
	}
	
	public boolean isBikeStolen() {
		AssetStatus bikeStatus = new ConfigurationManager(context).getAssetStatus();
		if(bikeStatus == AssetStatus.STOLEN) {
			return true;
		}
		else return false;
	}
}
