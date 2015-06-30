package taskBoard;

/**
 * This class holds the information of each created task
 */

public class StoryData {
	
	private String storyId;
	private String storyInformation;
	private String storyStatus;
	
	public StoryData (String storyId, String storyInformation, String storyStatus){
		this.storyId=storyId;
		this.storyInformation=storyInformation;
		this.storyStatus = storyStatus;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getStoryInformation() {
		return storyInformation;
	}

	public void setStoryInformation(String storyInformation) {
		this.storyInformation = storyInformation;
	}

	public String getStoryStatus() {
		return storyStatus;
	}

	public void setStoryStatus(String storyStatus) {
		this.storyStatus = storyStatus;
	}
}
