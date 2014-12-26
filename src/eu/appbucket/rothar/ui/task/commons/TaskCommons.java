package eu.appbucket.rothar.ui.task.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.http.AndroidHttpClient;
import eu.appbucket.rothar.ui.task.commons.OperationResult.OPERATION_RESULT;
import eu.appbucket.rothar.web.domain.exception.ErrorInfo;


public class TaskCommons {
	
	public enum RequestMethod {
		  GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
	}
	
	public static OperationResult getDataFromUrl(String url)  {
		return retrieveDataFromUrl(url);
	}
	
	private static OperationResult retrieveDataFromUrl(String url) {
		OperationResult result = new OperationResult();
		result.setResult(OPERATION_RESULT.FAILUR);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpGet request = new HttpGet(url);
		request.setHeader("Content-Type", "application/json");
		try {
			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();			
			if(responseCode == HttpURLConnection.HTTP_OK) {
				InputStream is = response.getEntity().getContent();
				result.setPayload(convertInputStreamToString(is));
				result.setResult(OPERATION_RESULT.SUCCESS);
			} else if (responseCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {				
		        ErrorInfo error = convertResponseToErrorInfo(response);
		        result.setMessage("Server error: " + error.getClientMessage());
		    } else {
		    	result.setMessage("Unknown error.");
			}
		} catch (IOException e) {
			result.setMessage("Communication error.");
		} finally {
			client.close();
		}
		return result;
	}
	
	public static OperationResult putDataToUrl(String dataToSend, String url)  {
		return sendDataToUrl(RequestMethod.PUT, dataToSend, url);
	}
	
	public static OperationResult postDataToUrl(String dataToSend, String url)  {		
		return sendDataToUrl(RequestMethod.POST, dataToSend, url);
	}
	
	private static OperationResult sendDataToUrl(RequestMethod method, String dataToSend, String url) {
		OperationResult result = new OperationResult();
		result.setResult(OPERATION_RESULT.FAILUR);
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");	
		HttpEntityEnclosingRequestBase request;
		if(method.equals(RequestMethod.POST)) {
			request = new HttpPost(url);	
		} else if (method.equals(RequestMethod.PUT)){
			request = new HttpPut(url);
		} else {
			result.setMessage("Unknow method.");
			return result;
		}
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
		        ErrorInfo error = convertResponseToErrorInfo(response);
		        result.setMessage("Server error: " + error.getClientMessage());
		    } else {				
				result.setMessage("Unknown error.");
			}
		} catch (IOException e) {			
			result.setMessage("Communication error. ");
		} finally {
			client.close();
		}
		return result;
	}
	
	private static ErrorInfo convertResponseToErrorInfo(HttpResponse response) throws IllegalStateException, IOException {
		InputStream is = response.getEntity().getContent();
        String errorJsonString = convertInputStreamToString(is);
        ErrorInfo error = convertRowDataToError(errorJsonString);
        return error;
	}
	
	public static String convertInputStreamToString(InputStream stream) throws TaskProcessingError {
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			while ((line = reader.readLine()) != null)
			    sb.append(line);
		} catch (UnsupportedEncodingException e) {
			throw new TaskProcessingError("Can't convert input data to string (UnsupportedEncodingException).", e);
		} catch (IOException e) {
			throw new TaskProcessingError("Can't convert input data to string (IOException).", e);
		}
        String result = sb.toString();
        return result;
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
