package eu.appbucket.rothar.ui.task.commons;

public class TaskProcessingError extends RuntimeException {		
	public TaskProcessingError(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
