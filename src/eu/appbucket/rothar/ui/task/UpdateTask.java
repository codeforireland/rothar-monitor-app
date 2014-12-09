package eu.appbucket.rothar.ui.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.task.commons.OperationResult.OPERATION_RESULT;
import eu.appbucket.rothar.ui.task.commons.OperationResult;
import eu.appbucket.rothar.ui.task.commons.TaskCommons;
import eu.appbucket.rothar.ui.task.commons.TaskProcessingError;
import eu.appbucket.rothar.web.domain.asset.AssetData;

public class UpdateTask extends AsyncTask<AssetData, Void, OperationResult> {

	private Context context;
	private AssetData asset;
	
	public UpdateTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected OperationResult doInBackground(AssetData... params) {
		return updateAssetInBackground(params[0]);
	}
	
	private OperationResult updateAssetInBackground(AssetData asset) {
		this.asset = asset;
		String dataToSend = convertJsonToString(asset);
		String url = Settings.SERVER_URL + "/v4/assets/" + asset.getAssetId();
		return TaskCommons.postDataToUrl(dataToSend, url);
	}
	
	private String convertJsonToString(AssetData asset) throws TaskProcessingError {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("assetId", asset.getAssetId());
			jsonObj.put("description", "");
			jsonObj.put("status", asset.getStatus().toString());
		} catch (JSONException e) {
			new TaskProcessingError("Can't convert asset data to json object.", e);
		}
		return jsonObj.toString();
	}
	
	@Override
	protected void onPostExecute(OperationResult result) {
		if(result.getResult() == OPERATION_RESULT.SUCCESS) {
			new ConfigurationManager(context).setAssetStatus(asset.getStatus());
			Toast.makeText(context, "Bike status set to: " + asset.getStatus(), Toast.LENGTH_SHORT).show();		
		} else {
			Toast.makeText(context, "Bike status update failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();			
		}
	}
}
