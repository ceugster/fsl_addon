package ch.eugster.filemaker.fsl.xls;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class XlsFontTest extends AbstractXlsTest
{
	@Test
	public void testApplyFontStylesWithoutWorkbook() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "Arial");
		requestNode.put(Key.BOLD.key(), 0);
		requestNode.put(Key.ITALIC.key(), 0);
		requestNode.put(Key.SIZE.key(), 14);
		ObjectNode foregroundNode = requestNode.objectNode();
		foregroundNode.put(Key.COLOR.key(), "ORCHID");
		requestNode.set(Key.FOREGROUND.key(), foregroundNode);
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("Workbook missing (create workbook first)", responseNode.get(Executor.ERRORS).get(0).asText());
	}
	
	@Test
	public void testApplyFontStylesWithoutSheet() throws JsonMappingException, JsonProcessingException
	{
		Xls.activeWorkbook = new XSSFWorkbook();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "Arial");
		requestNode.put(Key.BOLD.key(), 0);
		requestNode.put(Key.ITALIC.key(), 0);
		requestNode.put(Key.SIZE.key(), 14);
		ObjectNode foregroundNode = requestNode.objectNode();
		foregroundNode.put(Key.COLOR.key(), "ORCHID");
		requestNode.set(Key.FOREGROUND.key(), foregroundNode);
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("Sheet index (0) is out of range (no sheets)", responseNode.get(Executor.ERRORS).get(0).asText());
	}
	
	@Test
	public void testApplyNumericFontStylesToCell() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesToCell.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesToCell.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "Arial");
		requestNode.put(Key.BOLD.key(), 0);
		requestNode.put(Key.ITALIC.key(), 0);
		requestNode.put(Key.SIZE.key(), 14);
		ObjectNode foregroundNode = requestNode.objectNode();
		foregroundNode.put(Key.COLOR.key(), "ORCHID");
		requestNode.set(Key.FOREGROUND.key(), foregroundNode);
		requestNode.put(Key.CELL.key(), "A1");
		requestNode.put(Key.SHEET.key(), "Tabelle1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Font font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheet("Tabelle1").getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertFalse(font.getBold());
		assertFalse(font.getItalic());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.CELL.key(), "B2");

		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(1).getCellStyle().getFontIndex());
		assertEquals("Calibri", font.getFontName());
		assertTrue(font.getBold());
		assertFalse(font.getItalic());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.CELL.key(), "C3");

		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(2).getCellStyle().getFontIndex());
		assertFalse(font.getBold());
		assertTrue(font.getItalic());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.STYLE.key(), 3);
		requestNode.put(Key.CELL.key(), "D4");

		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(3).getCellStyle().getFontIndex());
		assertTrue(font.getBold());
		assertTrue(font.getItalic());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveAndReleaseWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}

	@Test
	public void testApplyFontStyleBold() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesBold.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesBold.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("bold", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		responseNode = mapper.readTree(response);
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getBold());

				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleBoldError() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStyles.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStyles.xlsx"), file);
		Xls.activeWorkbook = WorkbookFactory.create(file);

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.BOLD.key(), "A");
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(1, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleItalic() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesItalic.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesItalic.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getItalic());
				assertEquals(false, font.getBold());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleUnderline() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesUnderline.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesUnderline.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.UNDERLINE.key(), Font.U_DOUBLE);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.U_DOUBLE : Font.U_NONE, font.getUnderline());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleColor() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesColor.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesColor.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.COLOR.key(), Font.COLOR_RED);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.COLOR_RED: 0, font.getColor());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleSize() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesSize.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesSize.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.SIZE.key(), 30);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? 30 : 12, font.getFontHeightInPoints());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleName() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesName.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesName.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.NAME.key(), "Courier new");
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? "Courier new" : "Calibri", font.getFontName());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());
		
		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleStrikeOut() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesStrikeOut.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesStrikeOut.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.STRIKE_OUT.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getStrikeout());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	
	@Test
	public void testApplyFontStyleTypeOffset() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesTypeOffset.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesTypeOffset.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.TYPE_OFFSET.key(), Font.SS_SUPER);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.SS_SUPER : Font.SS_NONE, font.getTypeOffset());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(false, font.getStrikeout());
			}			
		}

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleBoldToRange() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesBoldToRange.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesBoldToRange.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
			}			
		}

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testApplyFontStyleItalicToRange() throws EncryptedDocumentException, IOException, InterruptedException, TimeoutException, ExecutionException
	{
		File file = new File("results/applyFontStylesItalicToRange.xlsx");
		FileUtils.copyFile(new File("src/test/resources/applyFontStylesItalicToRange.xlsx"), file);
		InputStream is = new FileInputStream(file);
		Xls.activeWorkbook = WorkbookFactory.create(is);
		is.close();

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A2:D2");
		
		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A3:D3");
		
		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.UNDERLINE.key(), Font.U_DOUBLE);
		requestNode.put(Key.RANGE.key(), "A4:D4");
		
		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_DOUBLE, font.getUnderline());
		
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveAndReleaseWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
	
	@Test
	public void testFonts() throws JsonMappingException, JsonProcessingException, InterruptedException, TimeoutException, ExecutionException
	{
		Xls.activeWorkbook = new XSSFWorkbook();
		Sheet sheet = Xls.activeWorkbook.createSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist ein Test"));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist ein weiterer Test"));

		int numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		System.out.println("Fonts im Workbook");
		for (int i = 0; i < numberOfFonts; i++)
		{
			Font font = sheet.getWorkbook().getFontAt(i);
			System.out.println(font.getFontName() + ", Size: " + font.getFontHeightInPoints() + ", Bold: " + font.getBold() + ", Italic: " + font.getItalic() + ", Underline: " + font.getUnderline() + ", Strikeout: " + font.getStrikeout() + ", Type Offset: " + font.getTypeOffset() + ", Color: " + font.getColor());
		}
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Key.NAME.key(), "Arial");
		requestNode.put(Key.SIZE.key(), 30);
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Xls.applyFontStyles(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		assertEquals(2, numberOfFonts);

		requestNode = mapper.createObjectNode();
		requestNode.put(Key.NAME.key(), "Courier New");
		requestNode.put(Key.SIZE.key(), 24);
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.CELL.key(), "A2");
		
		response = Xls.applyFontStyles(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		assertEquals(3, numberOfFonts);

		System.out.println("Fonts im Workbook");
		for (int i = 0; i < numberOfFonts; i++)
		{
			Font font = sheet.getWorkbook().getFontAt(i);
			System.out.println(font.getFontName() + ", Size: " + font.getFontHeightInPoints() + ", Bold: " + font.getBold() + ", Italic: " + font.getItalic() + ", Underline: " + font.getUnderline() + ", Strikeout: " + font.getStrikeout() + ", Type Offset: " + font.getTypeOffset() + ", Color: " + font.getColor());
		}
		
		System.out.println("Fonts in den Zellen");
		System.out.println(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
		System.out.println(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex());
		
		File file = new File("results/testfonts.xlsx");
		requestNode = mapper.createObjectNode();
		requestNode.put(Key.PATH.key(), file.getAbsolutePath());

		response = Xls.saveAndReleaseWorkbook(requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertTrue(file.isFile());
	}
}
