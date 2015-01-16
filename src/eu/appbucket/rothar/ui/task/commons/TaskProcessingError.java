package eu.appbucket.rothar.ui.task.commons;

public class TaskProcessingError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public TaskProcessingError(String errorMessage, Throwable throwable) {
		super(errorMessage, throwable);
	}
}
