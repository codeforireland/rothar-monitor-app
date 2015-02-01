package eu.appbucket.rothar.ui.task.tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.ui.listener.TagRegisterListener;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.exception.ErrorInfo;

class Result {
	protected AssetData asset;
	protected boolean failure = true;
	protected String message;
}

public class RegisterTagTask extends AsyncTask<String, Void, Result> {
	
	private TagRegisterListener listener;
	private Context context;
	//private static final String LOG_TAG = "RegisterTask";

	public RegisterTagTask(Context context, TagRegisterListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	private class RegisterTaskCommunicationError extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public RegisterTaskCommunicationError(String errorMessage) {
			super(errorMessage);
		}
		public RegisterTaskCommunicationError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	private class RegisterTaskProcessingError extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public RegisterTaskProcessingError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	}
	
	private class RegisterTaskServerError extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public RegisterTaskServerError(String serverResponse) {
			super(serverResponse);
		}
	}
	
	@Override
	protected Result doInBackground(String... params) {
		return getAssetByTagCodeInTheBackgroupd(params[0]);
	};
	
	private Result getAssetByTagCodeInTheBackgroupd(String tagCode) {
		Result operationResult = new Result();
		if(!isNetworkAvailable()) {
			operationResult.failure = true;
			operationResult.message = "Can't activate tag because of network problem.";
			return operationResult;
		}
		try {
			String assetRawData = getAssetRawData(tagCode);
			operationResult.asset = convertRowDataToAsset(assetRawData);
			operationResult.failure = false;
			operationResult.message = "Tag activated.";
		} catch (RegisterTaskCommunicationError e) {
			//Log.e(LOG_TAG, "Communication error: ", e);
			operationResult.failure = true;
			operationResult.message = "Can't activate tag because of communication problem.";
		} catch (RegisterTaskProcessingError e) {
			//Log.e(LOG_TAG, "Processing error: ", e);
			operationResult.failure = true;
			operationResult.message = "Can't activate tag because of data problem.";
		} catch(RegisterTaskServerError e) {
			ErrorInfo error = convertRowDataToError(e.getMessage());
			//Log.e(LOG_TAG, "Server error: " + error.getDeveloperMessage());
			operationResult.failure = true;
			operationResult.message = error.getClientMessage();
		}
		return operationResult;
	}
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private String getAssetRawData(String tagCode) throws RegisterTaskCommunicationError, RegisterTaskProcessingError, RegisterTaskServerError {
		String assetUrl = Settings.SERVER_URL + "/v4/assets/code/" + tagCode;
		String assetAsJsonString = "";
		int len = 15000;
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpGet request = new HttpGet(assetUrl);
		request.setHeader("Content-Type", "application/json");
		try {
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();			
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = response.getEntity().getContent();
		        assetAsJsonString = convertInputStreamToString(is, len);
			} else if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
				InputStream is = response.getEntity().getContent();
		        assetAsJsonString = convertInputStreamToString(is, len);
		        throw new RegisterTaskServerError(assetAsJsonString);
			} else {
				throw new RegisterTaskCommunicationError("Server responded with error code: + " + responseCode);
			}
		} catch (IOException e) {
			throw new RegisterTaskCommunicationError("Connection error", e);
		} finally {
			client.close();
		}
		return assetAsJsonString;
	}
	
	private String convertInputStreamToString(InputStream stream, int len) throws RegisterTaskProcessingError {
	    try {
	    	Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		} catch (IOException e) {
			throw new RegisterTaskProcessingError("Can't convert input data to string.", e);
		}		
	}
	
	private AssetData convertRowDataToAsset(String result) {
		AssetData asset = new AssetData(); 
		try {
			JSONObject jsonObject = new JSONObject(result);
			asset = new AssetData();
			asset.setAssetId(jsonObject.getInt("assetId"));
			asset.setUuid(jsonObject.getString("uuid")); 
			asset.setMajor(jsonObject.getInt("major"));
			asset.setMinor(jsonObject.getInt("minor"));
		} catch (JSONException e) {
			//Log.e(LOG_TAG, "Can't process asset data.");
		}
		return asset;
	}
	
	private ErrorInfo convertRowDataToError(String result) {
		ErrorInfo error = new ErrorInfo(); 
		try {
			JSONObject jsonObject = new JSONObject(result);
			error = new ErrorInfo();
			error.setClientMessage(jsonObject.getString("clientMessage"));
			error.setDeveloperMessage(jsonObject.getString("developerMessage")); 			
		} catch (JSONException e) {
			//Log.e(LOG_TAG, "Can't process asset data.");
		}
		return error;
	}
	
	protected void onPostExecute(Result result) {
		if(!result.failure) {
			listener.onTagRegisterSuccess(result.asset);
		} else {
			listener.onTagRegisterFailure(result.message);
		}
	}
	

}
