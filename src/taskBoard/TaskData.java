package taskBoard;

/**
 * This class holds the information of each created task
 */

public class TaskData {
	
	private String storyId;
	private String taskId;
	private String taskDescription;
	private String taskStatus;
	
	public TaskData (String storyId, String taskId, String taskDescription, String taskStatus){
		this.storyId=storyId;
		this.taskId=taskId;
		this.taskDescription=taskDescription;
		this.taskStatus = taskStatus;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
}
