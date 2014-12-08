package eu.appbucket.rothar.ui.task;

public class TaskProcessingError extends RuntimeException {		
	public TaskProcessingError(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
