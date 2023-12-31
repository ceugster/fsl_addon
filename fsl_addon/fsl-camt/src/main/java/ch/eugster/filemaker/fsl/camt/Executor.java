package ch.eugster.filemaker.fsl.camt;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Executor
{
	protected static final String STATUS = "status";
	
	protected static final String ERRORS = "errors";
	
	protected static final String OK = "OK";

	protected static final String ERROR = "Fehler";

	protected static final String RESULT = "result";

	protected static ObjectMapper mapper = new ObjectMapper();
	
	private static ObjectNode requestNode;
	
	private static ObjectNode responseNode;
	
	protected static boolean createRequestNode(String request)
	{
		boolean result = true;
		if (Objects.nonNull(request) && !request.trim().isEmpty())
		{
			try 
			{
				JsonNode _requestNode = mapper.readTree(request);
				requestNode = ObjectNode.class.cast(_requestNode);
				responseNode = mapper.createObjectNode();
			} 
			catch (JsonMappingException e) 
			{
				result = addErrorMessage("cannot not map 'request': illegal json format");
			} 
			catch (JsonProcessingException e) 
			{
				result = addErrorMessage("cannot not process 'request': illegal json format");
			}
			catch (ClassCastException e)
			{
				result = addErrorMessage("cannot not cast 'request': illegal json format");
			}
		}
		else
		{
			result = addErrorMessage("missing argument 'request'");
		}
		return result;
	}

	protected static ObjectNode getRequestNode()
	{
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
