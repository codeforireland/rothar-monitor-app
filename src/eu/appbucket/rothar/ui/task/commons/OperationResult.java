package eu.appbucket.rothar.ui.task.commons;



public class OperationResult {
	
	public enum OPERATION_RESULT {
		SUCCESS,
		FAILUR
	}
			
	private String message;
	private OPERATION_RESULT result;
	
	private String payload;
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
    
	public String getPayload() {
    	return payload;
    }
    
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
	
	public boolean isFailure() {
		return result.equals(OPERATION_RESULT.FAILUR);
	}
	
	public boolean isSuccess() {
		return result.equals(OPERATION_RESULT.SUCCESS);
	}
}