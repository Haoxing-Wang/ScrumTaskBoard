package taskBoard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.SAXException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class executes the operation based on user input if it is valid,
 * such as create, move, update and complete functions
 */
public class ExecuteOperation {

	private DocumentBuilderFactory buildFactory;
	private DocumentBuilder builder;
	static Document doc;
	private XPath Xpath;
	private static File XMLFile;

	/**
	 * Constructor that creates new instance of DocumentBuilderFactory, DocumentBuilder and Document
	 * @throws IOException If a local file directory was not found
	 * @throws ParserConfigurationException If fail to create a new document builder
	 */
	public ExecuteOperation () throws IOException, ParserConfigurationException{
		// The XMLFile is used to save all the changes made,
		// therefore, its location needs to be changed accordingly.
		// For example, you could use the following directory on Windows
		// XMLFile = new File("C:\\test\\file.xml"); 
		XMLFile = new File("/Users/haoxingwang/Documents/file");
		
		buildFactory = DocumentBuilderFactory.newInstance();
		builder = buildFactory.newDocumentBuilder();
		doc = builder.newDocument();
		Xpath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * Add a new task with the story id, task id and task description 
	 * @param storyId The new story id
	 * @param taskId The new task id
	 * @param description The description of a new story or task
	 * @throws IOException If fail to read the XML file from local
	 * @throws SAXException If fail to parse the XML file
	 * @throws XPathExpressionException If fail to process the expression used in the XPathExpression
	 */
	public void add(String storyId, String taskId, String description) throws IOException, SAXException, XPathExpressionException{
		Element rootElement =null; 							// Create a root element

		if(hasFile(XMLFile)){ 								// File exists
			doc = builder.parse(XMLFile);					// Parse the XML File
			rootElement = doc.getDocumentElement();			// Set value to root element

			XPathExpression storyIdCheck = Xpath.compile("//Story[@storyId="+storyId+"]");  // Check if story id exists
			NodeList storyIdCheckResult = (NodeList) storyIdCheck.evaluate(doc,XPathConstants.NODESET);

			if(taskId==null){								// Create a new story if task id is null
				if(storyIdCheckResult.getLength()!=0)
					throw new IllegalArgumentException("The story id "+storyId+" has been assigned to another one");
				try {
					rootElement.appendChild(createNode(storyId, description));
				} catch (DOMException e) {
					e.printStackTrace();
				}
			}
			else{											// Create a new task
				XPathExpression taskIdCheck = Xpath.compile("//Task[@storyId="+storyId+" and @taskId="+taskId+"]"); // Check if task id exists
				NodeList taskIdCheckResult = (NodeList) taskIdCheck.evaluate(doc,XPathConstants.NODESET);
				if(taskIdCheckResult.getLength()!=0)
					throw new IllegalArgumentException("The task id "+taskId+" has been assigned to another one");
				if(storyIdCheckResult.getLength()==0)
					throw new IllegalArgumentException("Cannot build the task with id "+taskId+" as no associated story exists with id "+storyId);

				try {
					storyIdCheckResult.item(0).appendChild(createNode(storyId, taskId, description,taskStatus.TODO.toString()));
				} catch (DOMException e) {
					e.printStackTrace();
				}
			}
		}
		else{ 													// File not found
			rootElement = doc.createElement("StoriesAndTasks"); // Create a root element called "StoriesAndTasks"
			doc.appendChild(rootElement);

			if(taskId==null)									// Could only create a new story if no file exists before
				try {
					rootElement.appendChild(createNode(storyId, description));
				} catch (DOMException e) {
					e.printStackTrace();
				}
			else
				throw new IllegalArgumentException("Cannot build the task with id "+taskId+" as no associated story exists with id "+storyId);
		}

		outputXMLFile(new DOMSource(doc), new StreamResult(XMLFile));
	}

	/**
	 * Add a new story with given story id and story description
	 * @param storyId The new story id
	 * @param description The new story description
	 */
	public void add(String storyId, String description){
		try {
			add(storyId, null, description); 				// The task id is always null
		} catch (XPathExpressionException | IOException | SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new task node under parent element "Story"
	 * or create a new story node under root element "StoriesAndTasks", with "taskDescription" and "taskStatus" tags
	 * @param storyId The story id associated with a new task id
	 * @param taskId The new task id
	 * @param description The new task description associated with the new task id
	 * @param taskStatus The initial status of the new task 
	 * @return The new created task node or story node
	 */
	public Node createNode (String storyId, String taskId, String description, String taskStatus){
		Element element=null; 
		if(taskId==null && taskStatus==null){				// Create a story node if task id and task status parameter are null		
			element = doc.createElement("Story");
			element.setAttribute("storyId", storyId);
			element.setAttribute("storyInformation", description);
			element.setAttribute("storyStatus", storyStatus.INCOMPLETE.toString());
		}
		else{												// Create a task node
			element = doc.createElement("Task");
			element.setAttribute("storyId", storyId);
			element.setAttribute("taskId", taskId);
			element.appendChild(addTextValueToTags(element, "taskDescription", description));
			element.appendChild(addTextValueToTags(element, "taskStatus", taskStatus));
		}
		return element;
	}

	/**
	 * Create a new story node under root element "StoriesAndTasks" 
	 * @param storyId The new story id
	 * @param description The story description
	 * @return The result of calling createNode(String storyId, String taskId, String description, String taskStatus) method
	 */
	public Node createNode(String storyId, String description){
		return createNode(storyId, null, description, null); // The task id and task status parameter are always null
	}

	// utility method to create text node
	/**
	 * Add the text value to a specific tag under a node
	 * @param element The parent node or element of this new tag
	 * @param tagName The name of this new tag
	 * @param tagValue The text value of the new tag
	 * @return The created tag with contents
	 */
	public Node addTextValueToTags(Element element, String tagName, String tagValue) {
		Element node = doc.createElement(tagName); 			// Create a tag under a specific node with tag name
		node.appendChild(doc.createTextNode(tagValue));		// Add value to this created tag
		return node;
	}

	/**
	 * Delete a task with given story id and task id
	 * or delete a story with the given id
	 * @param storyId The story id of the story that needs to be deleted
	 * @param taskId The task id of the task that needs to be deleted
	 * @throws SAXException If fail to parse the XML file
	 * @throws IOException If fail to process the XML file
	 * @throws XPathExpressionException If fail to process the expression used in the XPathExpression
	 */
	public void delete(String storyId, String taskId) throws SAXException, IOException, XPathExpressionException {
		Document document =null;
		if(hasFile(XMLFile)){ 								// File exists
			XPathExpression expression = null;
			if(taskId==null)								// Create an expression that deletes a story
				expression = Xpath.compile("//Story[@storyId="+storyId+"]");
			else											// Create an expression that deletes a task
				expression = Xpath.compile("//Task[@storyId="+storyId+" and @taskId="+taskId+"]");

			document = builder.parse(XMLFile);				// Parse the XML file is important here
			NodeList nodeList = (NodeList) expression.evaluate(document,XPathConstants.NODESET);

			if(nodeList != null && nodeList.getLength()!=0){
				for(int i=0;i<nodeList.getLength();i++)		// Iterate the node list and remove found node
					nodeList.item(i).getParentNode().removeChild(nodeList.item(i));
			}
			else
				throw new IllegalArgumentException("the sotry id could not be found");
		}
		outputXMLFile(new DOMSource(document), new StreamResult(XMLFile));
	}

	/**
	 * Delete a story with given story id
	 * @param storyId The story id of the story that needs to be deleted
	 */
	public void delete(String storyId){
		try {
			delete(storyId, null);							// The task id is always null
		} catch (XPathExpressionException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * List all the created stories
	 * @throws SAXException If fail to parse the XML file
	 * @throws IOException If fail to process the XML file
	 */
	public void listStories() throws SAXException, IOException{
		HashMap<String, StoryData> allStories = new HashMap<String, StoryData>();

		if(hasFile(XMLFile)){ 	// File exists
			doc = builder.parse(XMLFile);
			Element docElements = doc.getDocumentElement();
			NodeList nodeList = docElements.getElementsByTagName("Story"); // Get all the nodes where the node name is "Story"

			if(nodeList != null && nodeList.getLength() > 0) {
				for(int i = 0 ; i < nodeList.getLength();i++) {			   // Iterate the found node list to get more information
					Element element = (Element)nodeList.item(i);
					String id = getAttributeValue(element, "storyId");
					String description = getAttributeValue(element, "storyInformation");
					String status = getAttributeValue(element, "storyStatus");
					allStories.put(id, new StoryData(id,description,status));
				}
			}
		}
		if(allStories.size()==0) System.out.println("No stories were found, please create a new one");
		for(Entry<String, StoryData> e:allStories.entrySet()){
			StoryData value =e.getValue();
			System.out.println(value.getStoryId()+", "+value.getStoryInformation()+", "+value.getStoryStatus());
		}
	}

	/**
	 * List a task with the given story id
	 * @param storyId The story id used to find all associated tasks
	 */
	public void listTasks(String storyId) {
		HashMap<String, TaskData> allTasks = new HashMap<String, TaskData>();

		if(hasFile(XMLFile)){ 	// File exists
			try {
				doc=builder.parse(XMLFile);
				XPathExpression storyIdCheck = Xpath.compile("//Story[@storyId="+storyId+"]");	// Check whether or not a story id exists
				NodeList storyIdCheckResult = (NodeList) storyIdCheck.evaluate(doc,XPathConstants.NODESET);

				if(storyIdCheckResult.getLength()==0) throw new IllegalArgumentException("The story "+storyId+" id does not exist");
				NodeList nodeList = storyIdCheckResult.item(0).getChildNodes();

				for(int i=0; i<nodeList.getLength(); i++){	 // Iterate the found node list to get more information
					if(nodeList.item(i).getNodeType()!=Node.TEXT_NODE){
						String description = getTagValue((Element)nodeList.item(i), "taskDescription"); // Get the description of a task
						String status = getTagValue((Element)nodeList.item(i), "taskStatus");			 // Get the the status of a task
						String id = getAttributeValue((Element)nodeList.item(i), "taskId");				 // Get the id of a task
						allTasks.put(id, new TaskData(storyId, id, description, status));					 // Add found task to a map
					}
				}

			} catch (SAXException | IOException | XPathExpressionException e) {
				e.printStackTrace();
			}
		}
		
		if(allTasks.size()==0) System.out.println("No tasks were found under story id "+storyId);
		for (Entry<String, TaskData> e:allTasks.entrySet()){ 	 // Print out the all the task along with their details
			TaskData value = e.getValue();
			System.out.println(value.getTaskId()+", "+value.getTaskDescription()+", "+value.getTaskStatus());
		}
	}

	/**
	 * Get the attribute value of a story or task
	 * @param element Either "Story" or "Task"
	 * @param tagName Such as "storyId", "storyDescription"
	 * @return The retrieved value
	 */
	public String getAttributeValue(Element element, String tagName){
		String attributeValue= element.getAttributes().getNamedItem(tagName).getNodeValue();
		return attributeValue;
	}

	/**
	 * Get the node value of a task
	 * @param element The "Task"
	 * @param tagName Such as "taskID", "taskDescription" and "taskStatus"
	 * @return The retrieved value
	 */
	public String getTagValue(Element element, String tagName){
		String textValue =null;
		NodeList nodeList = element.getElementsByTagName(tagName); // Get all the elements by tagName
		if(nodeList != null && nodeList.getLength() > 0) {		   // Get retrieve their values if exist
			Element el = (Element)nodeList.item(0);
			textValue = el.getFirstChild().getNodeValue();
		}
		return textValue;
	}

	/**
	 * Move task to a new status with the given story id, task id and new status
	 * @param storyId The story id associated with the required task id
	 * @param taskId The task id that required to be moved
	 * @param newStatus The new status of a task
	 */
	public void moveTask(String storyId, String taskId, String newStatus){
		if(hasFile(XMLFile)){ 		  // File exists
			try {
				Document document = builder.parse(XMLFile);
				// Check whether or not the story id and task id exist
				XPathExpression idCheck = Xpath.compile("//Task[@storyId="+storyId+" and @taskId="+taskId+"]");
				NodeList idCheckResult = (NodeList) idCheck.evaluate(document,XPathConstants.NODESET);

				if(idCheckResult.getLength()==0)				  
					throw new IllegalArgumentException("The task id"+taskId+" associated with "+storyId+" does not exist");

				String statusValue = getTagValue((Element)idCheckResult.item(0), "taskStatus"); // Get the current status of a task
				if(statusValue.equals(newStatus))
					throw new IllegalArgumentException("The new status cannot the same as the previous status");
				if(!isMember(newStatus))
					throw new IllegalArgumentException("The acceptable status should be: TODO, INPROCESS, TOVERIFY, DONE");

				modifyValue(document,(Element)idCheckResult.item(0),"taskStatus", newStatus);

			} catch (SAXException | IOException | XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Update the task description
	 * @param storyId The story id associated with the required task id
	 * @param taskId The task id that required to be moved
	 * @param newDescription The new description of a task 
	 */
	public void updateTask (String storyId, String taskId, String newDescription){
		if(hasFile(XMLFile)){ // file exists
			try {
				Document document = builder.parse(XMLFile);
				// Check whether or not the story id and task id exist
				XPathExpression idCheck = Xpath.compile("//Task[@storyId="+storyId+" and @taskId="+taskId+"]");
				NodeList idCheckResult = (NodeList) idCheck.evaluate(document,XPathConstants.NODESET);

				if(idCheckResult.getLength()==0)
					throw new IllegalArgumentException("The task id "+taskId+" associated with "+storyId+" does not exist");

				modifyValue(document,(Element)idCheckResult.item(0),"taskDescription", newDescription);
				
			} catch (SAXException | IOException | XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Modify the value which will be called by either update method or move method
	 * @param document The parameter set in update or move method
	 * @param element The name of the node that needs to be modified
	 * @param tagName The specific tag needs to be changed under a node
	 * @param newContents The new contents
	 */
	public void modifyValue(Document document, Element element, String tagName, String newContents){
		NodeList childNodes = element.getChildNodes();
		for(int i=0; i<childNodes.getLength();i++){
			String nodeName = childNodes.item(i).getNodeName();
			if(nodeName.equals(tagName))
				childNodes.item(i).setTextContent(newContents);
		}
		outputXMLFile(new DOMSource(document), new StreamResult(XMLFile));
	} 

	/**
	 * Check whether the new status is acceptable
	 * @param newStatus The new status from user
	 * @return A boolean value to indicate whether or not the new status is acceptable
	 */
	public static boolean isMember (String newStatus){
		for(taskStatus s : taskStatus.values()){
			if(s.name().equals(newStatus)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Mark a user story as completed
	 * @param storyId The id of the story that needs to be marked as completed
	 */
	public void completeStory(String storyId){
		if(hasFile(XMLFile)){ // File exists
			try {
				Document document = builder.parse(XMLFile);
				// Check each task status based on the given story id
				XPathExpression idCheck = Xpath.compile("//Task[@storyId="+storyId+"]");
				NodeList idCheckResult = (NodeList) idCheck.evaluate(document,XPathConstants.NODESET);
				
				if(idCheckResult.getLength()==0)
					throw new IllegalArgumentException("The story with id "+storyId+" cannot be marked as completed, since no subtasks found");

				boolean isCompleted =true;						// Initially, marking the story is completed
				for(int i=0; i<idCheckResult.getLength();i++){	// Iterate the task nodes and get their task status values
					String statusValue = getTagValue((Element)idCheckResult.item(i), "taskStatus");
					if(!statusValue.equals(taskStatus.DONE.toString())) 
						isCompleted=false;
				}

				if (isCompleted){								// Mark this story as completed
					Element node =(Element) idCheckResult.item(0).getParentNode();
					node.setAttribute("storyStatus", storyStatus.COMPLETE.toString());
					System.out.println("The story with id "+storyId+" can be marked as completed");
					
					outputXMLFile(new DOMSource(document), new StreamResult(XMLFile));
				}
				else
					System.out.println("The story with id "+storyId+" cannot be marked as completed, please complete all its sub tasks");

			} catch (SAXException | IOException | XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Check whether or not an XML file exists before executing any commands
	 * @param XMLFile The file needs to be checked
	 * @return true exists otherwise does not exist
	 */
	public boolean hasFile(File XMLFile){
		if(XMLFile.exists() && !XMLFile.isDirectory())
			return true;
		else{
			System.out.println("XML File does not exist, cannot execute the command");
			return false;
		}
	}

	/**
	 * Write the XML file after adding, deleting, moving and updating
	 * @param source The XML input to transform
	 * @param result The result of transforming XML source
	 */
	public static void outputXMLFile(DOMSource source, StreamResult result){
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
			System.out.println("Commands Successfully Executed, XML DOM Successfully Processed in File:");
			System.out.println(XMLFile.getPath());

		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

}
