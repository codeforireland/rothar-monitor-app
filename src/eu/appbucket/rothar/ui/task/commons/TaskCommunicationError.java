package eu.appbucket.rothar.ui.task.commons;

public class TaskCommunicationError extends RuntimeException {		
	public TaskCommunicationError(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
