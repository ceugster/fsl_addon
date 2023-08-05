package ch.eugster.filemaker.fsl.camt.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.camt.Camt;
import ch.eugster.filemaker.fsl.camt.Executor;

public class CamtTest extends AbstractTest
{
	private static String xmlFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";

	private static String jsonFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";

	private static String xmlContent;

	private static String jsonContent;

	private static String expectedXmlContent;
	
	private static String expectedJsonContent;
	
	@BeforeEach
	public void before() throws IOException
	{
		File file = new File(xmlFilename);
		InputStream is = new FileInputStream(file);
		byte[] bytes = is.readAllBytes();
		expectedXmlContent = new String(bytes).replaceAll("\\s", "");
		is.close();
		file = new File(jsonFilename);
		is = new FileInputStream(file);
		bytes = is.readAllBytes();
		expectedJsonContent = new String(bytes).replaceAll("\\s", "");
		is.close();
		file = new File(xmlFilename);
		is = new FileInputStream(file);
		bytes = is.readAllBytes();
		xmlContent = new String(bytes);
		is.close();
		file = new File(jsonFilename);
		is = new FileInputStream(file);
		bytes = is.readAllBytes();
		jsonContent = new String(bytes);
		is.close();
	}
	
	@Test
	public void testConvertIllegalCamtXmlFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.XML_FILE.key(), "gigi");

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("'gigi' is not a valid xml file", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testConvertIllegalCamtJsonFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.JSON_FILE.key(), "gigi");

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("'gigi' is not a valid json file", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testConvertIllegalCamtXmlContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.XML_CONTENT.key(), "gigi");

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("xml content is not valid", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testConvertIllegalCamtJsonContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.JSON_CONTENT.key(), "gigi");

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("json content is not valid", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testConvertCamtXmlFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.XML_FILE.key(), xmlFilename);

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(expectedJsonContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
		assertEquals("camt.054.001.04", responseNode.get(Camt.IDENTIFIER_KEY).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testConvertCamtJsonFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.JSON_FILE.key(), jsonFilename);

		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(expectedXmlContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
		assertEquals("camt.054.001.04", responseNode.get(Camt.IDENTIFIER_KEY).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testConvertCamtXmlContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.XML_CONTENT.key(), xmlContent);
		
		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(expectedJsonContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
		assertEquals("camt.054.001.04", responseNode.get(Camt.IDENTIFIER_KEY).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testConvertCamtJsonContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Key.JSON_CONTENT.key(), jsonContent);
		
		String response = Camt.convertCamt(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(expectedXmlContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
		assertEquals("camt.054.001.04", responseNode.get(Camt.IDENTIFIER_KEY).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	//	@Test
//	public void testGetNtfctnFromContent() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Camt.Parameter.XML_CONTENT.key(), sourceContent);
//
//		String response = Camt.extract(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//	}

//	@Test
//	public void testGetNtfctnFromFile() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Camt.Parameter.XML_FILE.key(), sourceFilename);
//
//		String response = Camt.extract(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//	}
	
//	@Test
//	public void testXsd() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Camt.Parameter.XML_FILE.key(), "./src/test/resources/xsd/camt.054.001.04.xsd");
//
//		String response = Camt.extractTags(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//	}
}
