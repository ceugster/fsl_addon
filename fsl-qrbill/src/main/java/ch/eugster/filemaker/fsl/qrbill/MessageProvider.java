package ch.eugster.filemaker.fsl.qrbill;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

interface MessageProvider
{
	default boolean addErrorMessage(ObjectNode responseNode, String message)
	{
		ArrayNode errors = ArrayNode.class.cast(responseNode.get(Executor.ERRORS));
		if (Objects.isNull(errors))
		{
			errors = responseNode.arrayNode();
			responseNode.set(Executor.ERRORS, errors);
		}
		errors.add(message);
		return false;
	}

}
