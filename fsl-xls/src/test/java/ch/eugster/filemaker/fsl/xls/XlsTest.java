package ch.eugster.filemaker.fsl.xls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.PrintOrientation;
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
	public void testWorkbookCreation() throws IOException
	{
		XSSFWorkbook wb  = new XSSFWorkbook();
		wb.createSheet("Test");
		File outputFile = File.createTempFile("test", ".xlsx");
		System.out.println(outputFile.getAbsolutePath());
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		wb.write(fileOutputStream);
		fileOutputStream.close();
		wb.close();		
		assertTrue(outputFile.getAbsoluteFile().exists());

		try (var workbook = new XSSFWorkbook()) 
		{ 
			workbook.createSheet("Sheet1"); 
			try (OutputStream fos = Files.newOutputStream(Path.of("results/WorkbookWithSheet.xlsx"))) 
			{ 
				workbook.write(fos); 
			}
			catch (IOException e)
			{
				
			}
		}
		catch (IOException e)
		{
			
		}
	}
	
	@Test
	public void testEmptyParameter() throws JsonMappingException, JsonProcessingException
	{
		String response = Xls.createWorkbook("");
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Missing argument 'request'", responseNode.get(Executor.ERRORS).get(0).asText());
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
	public void testCreateAndSaveWorkbook() throws JsonMappingException, JsonProcessingException
	{
		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Test");
		requestNode.put(Key.PATH.key(), "results/CreateAndSave.xlsx");
		
		String response = Xls.createWorkbookWithSheet(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(Xls.activeWorkbook);
		assertEquals("Test", Xls.activeWorkbook.getSheetAt(0).getSheetName());
		
		response = Xls.activeSheetPresent(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("true", responseNode.get(Executor.RESULT).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals("Test", responseNode.get(Key.SHEET.key()).asText());
		
		response = Xls.saveAndReleaseWorkbook(requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(Xls.activeWorkbook);
	}

	@Test
	public void testActiveWorkbookPresent() throws JsonMappingException, JsonProcessingException
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.activeWorkbookPresent(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals("false", responseNode.get(Executor.RESULT).asText());

		response = Xls.createWorkbook(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertTrue(XSSFWorkbook.class.isInstance(Xls.activeWorkbook));

		response = Xls.activeWorkbookPresent(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals("true", responseNode.get(Executor.RESULT).asText());

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.TYPE.key(), Type.XLS.extension());
		
		response = Xls.createWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertTrue(HSSFWorkbook.class.isInstance(Xls.activeWorkbook));

		response = Xls.activeWorkbookPresent(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals("true", responseNode.get(Executor.RESULT).asText());

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
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("false", responseNode.get(Executor.RESULT).asText());

		Sheet sheet = Xls.activeWorkbook.createSheet();
		
		response = Xls.activeSheetPresent(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(sheet.getSheetName(), responseNode.get(Key.SHEET.key()).asText());
	}

	@Test
	public void testCreateWorkbook() throws Exception
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.createWorkbook(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(Xls.activeWorkbook);
		assertNull(responseNode.get(Executor.ERRORS));

		Xls.activeWorkbook = null;
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.TYPE.key(), "blah");
		
		response = Xls.createWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertNull(Xls.activeWorkbook);
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Illegal workbook type 'blah'.", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateWorkbookWithSheet() throws Exception
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.createWorkbookWithSheet(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(Xls.activeWorkbook);
		assertEquals(1, Xls.activeWorkbook.getNumberOfSheets());
		assertEquals("Sheet0", Xls.activeWorkbook.getSheetAt(0).getSheetName());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.createSheet(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		response = Xls.createWorkbook(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		response = Xls.createSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals("Sheet0", responseNode.get(Key.SHEET.key()).asText());
		assertEquals(1, Xls.activeWorkbook.getNumberOfSheets());
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(2, Xls.activeWorkbook.getNumberOfSheets());
		assertEquals(1, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals(Xls.activeWorkbook.getSheetAt(1).getSheetName(), responseNode.get(Key.SHEET.key()).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");
		
		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Key.INDEX.key()));
		assertEquals("Sheet0", responseNode.get(Key.SHEET.key()).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Illegal argument 'sheet' ('Sheet0' already exists)", responseNode.get(Executor.ERRORS).get(0).asText());
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
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		Xls.activeWorkbook = new XSSFWorkbook();

		response = Xls.dropSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("There is no active sheet present", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");
		
		response = Xls.dropSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet with name 'Arbeitsblatt 1' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());

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
		String response = Xls.sheetNames(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		response = Xls.createWorkbook(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(XSSFWorkbook.class.isInstance(Xls.activeWorkbook));
		
		response = Xls.sheetNames(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(0, responseNode.get(Key.SHEET.key()).size());
		assertEquals(0, responseNode.get(Key.INDEX.key()).size());
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Tabelle 1");

		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		response = Xls.sheetNames(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertNull(responseNode.get(Executor.ERRORS));
		
		JsonNode sheetNode = responseNode.get(Key.SHEET.key());
		assertTrue(ArrayNode.class.isInstance(sheetNode));
		assertEquals(1, sheetNode.size());
		assertEquals(0, Xls.activeWorkbook.getActiveSheetIndex());
		assertEquals("Tabelle 1", sheetNode.get(0).asText());
		
		JsonNode indexNode = responseNode.get(Key.INDEX.key());
		assertTrue(ArrayNode.class.isInstance(indexNode));
		assertEquals(1, indexNode.size());
		assertEquals(0, Xls.activeWorkbook.getActiveSheetIndex());
		assertEquals(0, indexNode.get(0).asInt());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt 1");

		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		response = Xls.sheetNames(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertNull(responseNode.get(Executor.ERRORS));
		
		sheetNode = responseNode.get(Key.SHEET.key());
		assertTrue(ArrayNode.class.isInstance(sheetNode));
		assertEquals(2, sheetNode.size());
		assertEquals(0, Xls.activeWorkbook.getActiveSheetIndex());
		assertEquals("Arbeitsblatt 1", sheetNode.get(1).asText());
		
		indexNode = responseNode.get(Key.INDEX.key());
		assertTrue(ArrayNode.class.isInstance(indexNode));
		assertEquals(2, indexNode.size());
		assertEquals(0, Xls.activeWorkbook.getActiveSheetIndex());
		assertEquals(1, indexNode.get(1).asInt());
	}

	@Test
	public void testActivateSheet() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = null;
		
		String response = Xls.activateSheet(mapper.createObjectNode().toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		response = Xls.createWorkbook(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");

		response = Xls.activateSheet(mapper.createObjectNode().toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Missing argument 'sheet' or 'index'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Wrong Name");
		
		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet with name 'Wrong Name' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");

		response = Xls.createSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Sheet0", responseNode.get(Key.SHEET.key()).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 1);

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet with index 1 does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);

		response = Xls.activateSheet(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals("Sheet0", responseNode.get(Key.SHEET.key()).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveWorkbook() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		String pathname = "results/SaveWorkbook.xlsx";
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), pathname);

		String response = Xls.saveWorkbook(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode.put(Key.SHEET.key(), "Tabelle");
		response = Xls.createWorkbookWithSheet(requestNode.toString());

		responseNode = mapper.readTree(response);
		assertNotNull(Xls.activeWorkbook);

		response = Xls.activeSheetPresent(requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals("true", responseNode.get(Executor.RESULT).asText());
		assertEquals(0, responseNode.get(Key.INDEX.key()).asInt());
		assertEquals("Tabelle", responseNode.get(Key.SHEET.key()).asText());
		
//		response = Xls.saveWorkbook(requestNode.toString());
//		
//		responseNode = mapper.readTree(response);
//		assertNotNull(Xls.activeWorkbook);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertNull(responseNode.get(Executor.ERRORS));
		
		response = Xls.saveAndReleaseWorkbook(requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(Xls.activeWorkbook);
	}

	@Test
	public void testReleaseWorkbook() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Xls.releaseWorkbook(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

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
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		response = Xls.createWorkbook(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		response = Xls.createWorkbookWithSheet(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");
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

		Xls.createSheet(mapper.createObjectNode().toString());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Test");
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet with name 'Test' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 2);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setHeaders(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet index (2) is out of range (0..1)", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSetFooters() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		String response = Xls.setFooters(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());

		response = Xls.createWorkbook(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
		
		response = Xls.createWorkbookWithSheet(mapper.createObjectNode().toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		Xls.createSheet(mapper.createObjectNode().toString());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Test");
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet with name 'Test' does not exist", responseNode.get(Executor.ERRORS).get(0).asText());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.INDEX.key(), 2);
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		response = Xls.setFooters(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("Sheet index (2) is out of range (0..1)", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSetPrintSetupWithSingleCellId() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/SetPrintSetup.xlsx");
		Xls.activeWorkbook = new XSSFWorkbook();
		Xls.activeWorkbook.createSheet();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.ORIENTATION.key(), PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put(Key.COPIES.key(), 2);
		requestNode.put(Key.CELL.key(), "A1");

		String response = Xls.setPrintSetup(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testSetPrintSetupWithSingleCellNumericValues() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/SetPrintSetup.xlsx");
		Xls.activeWorkbook = new XSSFWorkbook();
		Xls.activeWorkbook.createSheet();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.ORIENTATION.key(), PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put(Key.COPIES.key(), 2);
		ObjectNode cellNode = requestNode.objectNode();
		cellNode.put(Key.ROW.key(), 0);
		cellNode.put(Key.COL.key(), 0);
		requestNode.set(Key.CELL.key(), cellNode);

		String response = Xls.setPrintSetup(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testSetPrintSetupWithCellRangeIds() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/SetPrintSetup.xlsx");
		Xls.activeWorkbook = new XSSFWorkbook();
		Xls.activeWorkbook.createSheet();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.ORIENTATION.key(), PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put(Key.COPIES.key(), 2);
		requestNode.put(Key.RANGE.key(), "A1:K10");

		String response = Xls.setPrintSetup(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testSetPrintSetupWithCellRangeNumericValues() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/SetPrintSetup.xlsx");
		Xls.activeWorkbook = new XSSFWorkbook();
		Xls.activeWorkbook.createSheet();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.ORIENTATION.key(), PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put(Key.COPIES.key(), 2);
		ObjectNode rangeNode = requestNode.objectNode();
		ObjectNode startNode = rangeNode.objectNode();
		startNode.put(Key.ROW.key(), 0);
		startNode.put(Key.COL.key(), 0);
		rangeNode.set(Key.TOP_LEFT.key(), startNode);
		ObjectNode endNode = rangeNode.objectNode();
		endNode.put(Key.ROW.key(), 10);
		endNode.put(Key.COL.key(), 10);
		rangeNode.set(Key.BOTTOM_RIGHT.key(), endNode);
		requestNode.set(Key.RANGE.key(), rangeNode);

		String response = Xls.setPrintSetup(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testJSONFormatting() throws JsonMappingException, JsonProcessingException
	{
		String value = "{\"amount\":287.30,\"currency\":\"CHF\",\"iban\":\"CH4431999123000889012\",\"reference\":\"000000000000000000000000000\",\"message\":\"Rechnungsnr. 10978 / Auftragsnr. 3987\",\"creditor\":{\"name\":\"Schreinerei Habegger & Söhne\",\"address_line_1\":\"Uetlibergstrasse 138\",\"address_line_2\":\"8045 Zürich\",\"country\":\"CH\"},\"debtor\":{\"name\":\"Simon Glarner\",\"address_line_1\":\"Bächliwis 55\",\"address_line_2\":\"8184 Bachenbülach\",\"country\":\"CH\"},\"format\":{\"graphics_format\":\"PDF\",\"output_size\":\"A4_PORTRAIT_SHEET\",\"language\":\"DE\"}}";
		JsonNode json = mapper.readTree(value);
		System.out.println(json.toPrettyString());
	}
}
