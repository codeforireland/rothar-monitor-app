package eu.appbucket.rothar.ui.listener;

import eu.appbucket.rothar.web.domain.asset.AssetData;


public interface TagRegisterListener {

	void onTagRegisterSuccess(AssetData asset);
	void onTagRegisterFailure(String cause);
}
