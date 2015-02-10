package eu.appbucket.rothar.ui.listener;

import eu.appbucket.rothar.web.domain.asset.AssetData;


public interface TagUpdateListener {

	void onTagUpdateSuccess(AssetData asset);
	void onTagUpdateFailure(String cause);
}
