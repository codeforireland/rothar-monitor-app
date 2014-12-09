package eu.appbucket.rothar.ui.task.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import eu.appbucket.rothar.ui.task.commons.OperationResult.OPERATION_RESULT;
import eu.appbucket.rothar.web.domain.exception.ErrorInfo;


public class TaskCommons {
	
	public static OperationResult postDataToUrl(String dataToSend, String url)  {
		OperationResult result = new OperationResult();
		result.setResult(OPERATION_RESULT.FAILUR);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/json");
		try {
			StringEntity data = new StringEntity(dataToSend);
			data.setContentEncoding("UTF-8");
			data.setContentType("application/json");
			request.setEntity(data);			
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			if(responseCode == HttpStatus.SC_OK) {
				result.setResult(OPERATION_RESULT.SUCCESS);	
			} else if (responseCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
				// TODO: cleanup this mess
				result.setResult(OPERATION_RESULT.FAILUR);
				InputStream is = response.getEntity().getContent();
		        String errorJsonString = convertInputStreamToString(is);
		        ErrorInfo error = convertRowDataToError(errorJsonString);
		        result.setMessage("Server error: " + error.getClientMessage());
		    } else {
				result.setResult(OPERATION_RESULT.FAILUR);
				result.setMessage("Unknown error.");
			}
		} catch (IOException e) {
			result.setResult(OPERATION_RESULT.FAILUR);
			result.setMessage("Communication error. ");
		} finally {
			client.close();
		}
		return result;
	}
	
	public static String convertInputStreamToString(InputStream stream) throws TaskProcessingError {
		int len = 1500;
		try {
	    	Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		} catch (IOException e) {
			throw new TaskProcessingError("Can't convert input data to string.", e);
		}		
	}
	
	private static ErrorInfo convertRowDataToError(String result) {
		ErrorInfo error = new ErrorInfo(); 
		try {
			JSONObject jsonObject = new JSONObject(result);
			error = new ErrorInfo();
			error.setClientMessage(jsonObject.getString("clientMessage"));
			error.setDeveloperMessage(jsonObject.getString("developerMessage")); 			
		} catch (JSONException e) {
			// Log.e(LOG_TAG, "Can't process asset data.");
		}
		return error;
	}
}
