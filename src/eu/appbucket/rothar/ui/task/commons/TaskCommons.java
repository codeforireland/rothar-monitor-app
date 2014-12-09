package eu.appbucket.rothar.ui.task.commons;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;
import eu.appbucket.rothar.ui.task.commons.OperationResult.OPERATION_RESULT;


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
				result.setResult(OPERATION_RESULT.FAILUR);
				result.setMessage("Server error.");
				InputStream is = response.getEntity().getContent();
		        String assetAsJsonString = convertInputStreamToString(is, len);
		        // parse json are set reponse 
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
}
