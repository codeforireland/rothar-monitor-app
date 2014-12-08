package eu.appbucket.rothar.ui;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;


public class TaskCommons {
	
	public enum OPERATION_RESULT {
		SUCCESS,
		FAILUR
	}
	
	class OperationResult {
			
		private String message;
		private OPERATION_RESULT result;
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public void setResult(OPERATION_RESULT result) {
			this.result = result;
		}
		
		public String getMessage() {
			return message;
		}
		
		public OPERATION_RESULT getResult() {
			return result;
		}
	}
	
	public OperationResult postDataToUrl(String dataToSend, String url)  {
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
			client.execute(request);
			result.setResult(OPERATION_RESULT.SUCCESS);
		} catch (IOException e) {
			result.setResult(OPERATION_RESULT.FAILUR);
			result.setMessage("Communication error. ");
		} finally {
			client.close();
		}
		return result;
	}
}
