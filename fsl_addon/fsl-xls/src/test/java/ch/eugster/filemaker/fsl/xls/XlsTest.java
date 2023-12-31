package ch.eugster.filemaker.fsl.xls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class XlsTest extends AbstractXlsTest
{
	@Test
	public void testEmptyParameter() throws JsonMappingException, JsonProcessingException
	{
		String response = Xls.createWorkbook("");
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'request'", responseNode.get(Executor.ERRORS).get(0).asText());
	}
	
	@Test
	public void testSupportedFunctionNames() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		
		String response = Xls.getSupportedFunctionNames(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals("OK", responseNode.get(Executor.STATUS).asText());
		assertEquals(185, responseNode.get(Executor.RESULT).size());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testCallableMethods() throws Exception
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);

		String response = Xls.getCallableMethods(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		ArrayNode methods = ArrayNode.class.cast(responseNode.get(Executor.RESULT));
		for (int i = 0; i < methods.size(); i++)
		{
			System.out.println(methods.get(i).asText());
		}
	}
	
	@Test
	public void testWorkbookActive() throws JsonMappingException, JsonProcessingException
	{
		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();
		
		String response = Xls.activeSheetPresent(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();
		
		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals("", responseNode.get(Key.SHEET.key()).asText());

		Xls.activeWorkbook = new XSSFWorkbook();
		Sheet sheet = Xls.activeWorkbook.createSheet();
		
		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(sheet.getSheetName(), responseNode.get(Key.SHEET.key()).asText());
	}

	@Test
	public void testActiveSheetPresent() throws Exception
	{
		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();
		
		String response = Xls.activeSheetPresent(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals("", responseNode.get(Key.SHEET.key()).asText());

		Sheet sheet = Xls.activeWorkbook.createSheet();
		
		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(sheet.getSheetName(), responseNode.get(Key.SHEET.key()).asText());
	}

	@Test
	public void testCreateWorkbook() throws Exception
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.createWorkbook(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		response = Xls.createWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.createSheet(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		response = Xls.createSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(Xls.activeWorkbook.getSheetAt(0).getSheetName(), responseNode.get(Key.SHEET.key()).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(Xls.activeWorkbook.getSheetAt(1).getSheetName(), responseNode.get(Key.SHEET.key()).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Key.INDEX.key()));
		assertNull(responseNode.get(Key.SHEET.key()));
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("illegal argument 'sheet' ('Arbeitsblatt 1' already exists)", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testDropSheet() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.createSheet(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		response = Xls.dropSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("there is no active sheet present", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.dropSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet with name 'Arbeitsblatt 1' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook.createSheet("Arbeitsblatt 1");
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.dropSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testGetSheetNames() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.sheetNames(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		Xls.createWorkbook(mapper.createObjectNode().toString());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);

		response = Xls.sheetNames(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		JsonNode sheetNode = responseNode.get(Key.SHEET.key());
		assertTrue(ArrayNode.class.isInstance(sheetNode));
		assertEquals(0, sheetNode.size());
		JsonNode indexNode = responseNode.get("index");
		assertEquals(0, indexNode.size());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");

		Xls.createSheet(requestNode.toString());
		
		response = Xls.sheetNames(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		sheetNode = responseNode.get(Key.SHEET.key());
		assertTrue(ArrayNode.class.isInstance(sheetNode));
		assertEquals(1, sheetNode.size());
		Iterator<JsonNode> iterator = sheetNode.iterator();
		assertEquals("Arbeitsblatt 1", iterator.next().asText());
		indexNode = responseNode.get(Key.INDEX.key());
		assertEquals(1, indexNode.size());
		iterator = indexNode.iterator();
		assertEquals(0, iterator.next().asInt());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testActivateSheet() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.activateSheet(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();
		
		response = Xls.activateSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'sheet' or 'index'", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook.createSheet();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet with name 'Arbeitsblatt 1' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(SHEET0, responseNode.get(Key.SHEET.key()).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 1);

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet with index 1 does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(SHEET0, responseNode.get(Key.SHEET.key()).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveWorkbook() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.saveWorkbook(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument '" + Key.PATH.key() + "'",responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), "$:ç");
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testReleaseWorkbook() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.releaseWorkbook(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();
		
		response = Xls.releaseWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetHeaders() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		String response = Xls.setHeaders(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.createSheet(mapper.createObjectNode().toString());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet with name 'Arbeitsblatt 1' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 1);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (1) is out of range (0..0)", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetFooters() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		String response = Xls.setFooters(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.createWorkbook(mapper.createObjectNode().toString());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.createSheet(mapper.createObjectNode().toString());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet with name 'Arbeitsblatt 1' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 1);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("sheet index (1) is out of range (0..0)", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testJSONFormatting() throws JsonMappingException, JsonProcessingException
	{
		String value = "{\"amount\":287.30,\"currency\":\"CHF\",\"iban\":\"CH4431999123000889012\",\"reference\":\"000000000000000000000000000\",\"message\":\"Rechnungsnr. 10978 / Auftragsnr. 3987\",\"creditor\":{\"name\":\"Schreinerei Habegger & Söhne\",\"address_line_1\":\"Uetlibergstrasse 138\",\"address_line_2\":\"8045 Zürich\",\"country\":\"CH\"},\"debtor\":{\"name\":\"Simon Glarner\",\"address_line_1\":\"Bächliwis 55\",\"address_line_2\":\"8184 Bachenbülach\",\"country\":\"CH\"},\"format\":{\"graphics_format\":\"PDF\",\"output_size\":\"A4_PORTRAIT_SHEET\",\"language\":\"DE\"}}";
		JsonNode json = mapper.readTree(value);
		System.out.println(json.toPrettyString());
	}
}
