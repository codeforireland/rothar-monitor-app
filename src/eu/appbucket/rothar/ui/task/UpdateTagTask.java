package eu.appbucket.rothar.ui.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.TagUpdateListener;
import eu.appbucket.rothar.ui.task.commons.OperationResult.OPERATION_RESULT;
import eu.appbucket.rothar.ui.task.commons.OperationResult;
import eu.appbucket.rothar.ui.task.commons.TaskCommons;
import eu.appbucket.rothar.ui.task.commons.TaskProcessingError;
import eu.appbucket.rothar.web.domain.asset.AssetData;

public class UpdateTagTask extends AsyncTask<AssetData, Void, OperationResult> {

	private Context context;
	private TagUpdateListener listener;
	private AssetData asset;
	
	public UpdateTagTask(Context context, TagUpdateListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	protected OperationResult doInBackground(AssetData... params) {
		return updateAssetInBackground(params[0]);
	}
	
	private OperationResult updateAssetInBackground(AssetData asset) {
		this.asset = asset;
		String dataToSend = convertJsonToString(asset);
		String url = Settings.SERVER_URL + "/v4/assets/" + asset.getAssetId();
		return TaskCommons.putDataToUrl(dataToSend, url);
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
			listener.onTagUpdateSuccess(asset);
					
		} else {
			listener.onTagUpdateFailure(result.getMessage());			
		}
	}
}
