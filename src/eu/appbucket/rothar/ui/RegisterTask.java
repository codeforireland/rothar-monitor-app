package eu.appbucket.rothar.ui;

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
import android.util.Log;
import android.widget.Toast;
import eu.appbucket.rothar.common.ConfigurationManager;
import eu.appbucket.rothar.common.Settings;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.exception.ErrorInfo;

public class RegisterTask extends AsyncTask<String, Void, String> {

	private Context context;
	private static final String LOG_TAG = "RegisterTask";
	
	public RegisterTask(Context context) {
		this.context = context;
	}
	
	private class RegisterTaskCommunicationError extends RuntimeException {
		public RegisterTaskCommunicationError(String errorMessage) {
			super(errorMessage);
		}
		public RegisterTaskCommunicationError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	} 
	
	private class RegisterTaskProcessingError extends RuntimeException {
		public RegisterTaskProcessingError(String errorMessage) {
			super(errorMessage);
		}
		public RegisterTaskProcessingError(String errorMessage, Throwable throwable) {
			super(errorMessage, throwable);
		}
	}
	
	private class RegisterTaskServerError extends RuntimeException {
		public RegisterTaskServerError(String serverResponse) {
			super(serverResponse);
		}
	}
	
	@Override
	protected String doInBackground(String... params) {
		return getAssetByTagCodeInTheBackgroupd(params[0]);
	};
	
	private String getAssetByTagCodeInTheBackgroupd(String tagCode) {
		if(!isNetworkAvailable()) {
			return "Can't activate tag because of network problem.";
		}
		try {
			String assetRawData = getAssetRawData(tagCode);
			AssetData asset = convertRowDataToAsset(assetRawData);
			saveAssetIntoPreferences(asset);	
			return "Tag activated.";	
		} catch (RegisterTaskCommunicationError e) {
			Log.e(LOG_TAG, "Communication error: ", e);
			return "Can't activate tag because of communication problem.";
		} catch (RegisterTaskProcessingError e) {
			Log.e(LOG_TAG, "Processing error: ", e);
			return "Can't activate tag because of data problem.";
		} catch(RegisterTaskServerError e) {
			ErrorInfo error = convertRowDataToError(e.getMessage());
			Log.e(LOG_TAG, "Server error: " + error.getDeveloperMessage());
			return error.getClientMessage();
		}
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
			Log.e(LOG_TAG, "Can't process asset data.");
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
			Log.e(LOG_TAG, "Can't process asset data.");
		}
		return error;
	}
	
	private void saveAssetIntoPreferences(AssetData asset) {
		new ConfigurationManager(context).saveAssetId(asset.getAssetId());
	}
	
	protected void onPostExecute(String notification) {
		Toast.makeText(context, notification, Toast.LENGTH_SHORT).show();
	}
}
