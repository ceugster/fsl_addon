package ch.eugster.filemaker.fsl.qrbill;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Executor
{
	public static final String STATUS = "status";
	
	public static final String ERRORS = "errors";
	
	public static final String OK = "OK";

	public static final String ERROR = "Fehler";

	public static final String RESULT = "result";

	private static ObjectMapper mapper = new ObjectMapper();
	
	private static ObjectNode requestNode;
	
	private static ObjectNode responseNode;
	
	protected static ObjectNode getRequestNode(String request)
	{
		if (Objects.isNull(requestNode))
		{
			try 
			{
				JsonNode _requestNode = mapper.readTree(request);
				requestNode = ObjectNode.class.cast(_requestNode);
			} 
			catch (JsonMappingException e) 
			{
				addErrorMessage("cannot map 'request': illegal json format");
			} 
			catch (JsonProcessingException e) 
			{
				addErrorMessage("cannot process 'request': illegal json format");
			}
			catch (ClassCastException e)
			{
				addErrorMessage("cannot cast 'request': illegal json format");
			}
		}
		return requestNode;
	}

	protected static ObjectNode getResponseNode()
	{
		if (Objects.isNull(responseNode))
		{
			responseNode = mapper.createObjectNode();
		}
		return responseNode;
	}
	
	protected static String getResponse()
	{
		String response = getResponseNode().put(Executor.STATUS, getResponseNode().has(Executor.ERRORS) ? Executor.ERROR : Executor.OK).toString();
		requestNode = null;
		responseNode = null;
		return response;
	}
	
	protected static boolean addErrorMessage(String message)
	{
		ArrayNode errors = ArrayNode.class.cast(getResponseNode().get(Executor.ERRORS));
		if (Objects.isNull(errors))
		{
			errors = getResponseNode().arrayNode();
			getResponseNode().set(Executor.ERRORS, errors);
		}
		errors.add(message);
		return false;
	}
}
