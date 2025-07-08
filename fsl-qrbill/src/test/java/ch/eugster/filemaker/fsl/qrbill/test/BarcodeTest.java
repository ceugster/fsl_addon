package ch.eugster.filemaker.fsl.qrbill.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.qrbill.Barcode;
import ch.eugster.filemaker.fsl.qrbill.Barcode.Key;
import ch.eugster.filemaker.fsl.qrbill.Executor;
import de.vwsoft.barcodelib4j.oned.BarcodeException;
import de.vwsoft.barcodelib4j.oned.BarcodeType;

public class BarcodeTest 
{
	@Test
	public void testWithoutBarcodeType() throws IOException, BarcodeException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestNode = mapper.createObjectNode();
		for (Key key : Barcode.Key.values())
		{
			switch (key)
			{
			case CONTENT:
			{
				requestNode.put(key.key(), "978-3-423-20419-4");
				break;
			}
			default:
			{
				
			}
			}
		}
		String response = Barcode.generate(requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		
		assertEquals("978-3-423-20419-4", responseNode.get(Key.CONTENT.key()).asText());
		assertEquals("Fehler", responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.RESULT));
		JsonNode errorsNode = responseNode.get(Executor.ERRORS);
		assertEquals(1, errorsNode.size());
		assertEquals("Barcode type not provided.", errorsNode.get(0).asText());
	}

	@Test
	public void testWithoutContent() throws IOException, BarcodeException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestNode = mapper.createObjectNode();
		for (Key key : Barcode.Key.values())
		{
			switch (key)
			{
			case BARCODE_TYPE:
			{
				requestNode.put(key.key(), "ISBN13");
				break;
			}
			default:
			{
				
			}
			}
		}
		String response = Barcode.generate(requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		
		assertEquals("Fehler", responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.RESULT));
		JsonNode errorsNode = responseNode.get(Executor.ERRORS);
		assertEquals(1, errorsNode.size());
		assertEquals("A valid (non empty) value for request node 'content' must be provided.", errorsNode.get(0).asText());
	}

	@Test
	public void testMinimalIsbn13() throws IOException, BarcodeException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestNode = mapper.createObjectNode();
		for (Key key : Barcode.Key.values())
		{
			switch (key)
			{
			case BARCODE_TYPE:
			{
				requestNode.put(key.key(), BarcodeType.ISBN13.name());
				break;
			}
			case CONTENT:
			{
				requestNode.put(key.key(), "978-3-423-20419-4");
				break;
			}
			default:
			{
				
			}
			}
		}
		String response = Barcode.generate(requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		assertEquals("OK", responseNode.get(Executor.STATUS).asText());
		Files.write(Paths.get("Code39_min.png"), responseNode.get(Executor.RESULT).binaryValue(), StandardOpenOption.CREATE);
		System.out.println(response);
	}

	@Test
	public void testMaximalIsbn13() throws IOException, BarcodeException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestNode = mapper.createObjectNode();
		for (Key key : Barcode.Key.values())
		{
			switch (key)
			{
			case BARCODE_TYPE:
			{
				requestNode.put(key.key(), BarcodeType.ISBN13.name());
				break;
			}
			case CONTENT:
			{
				requestNode.put(key.key(), "978-3-423-20419-4");
				break;
			}
			case APPEND_OPTIONAL_CHECKSUM:
			{
				requestNode.put(Key.APPEND_OPTIONAL_CHECKSUM.key(), true);
			}
			case AUTO_COMPLETE:
			{
				requestNode.put(Key.AUTO_COMPLETE.key(), true);
			}
			case COLOR_DARK:
			{
				requestNode.put(Key.COLOR_DARK.key(), 0x444444);
			}
			case COLOR_LIGHT:
			{
				requestNode.put(Key.COLOR_LIGHT.key(), 0xBBBBBB);
			}
			case COLOR_SPACE:
			{
				requestNode.put(Key.COLOR_SPACE.key(), "CMYK");
			}
			case FONT:
			{
				requestNode.put(Key.FONT.key(), "Monaco");
			}
			case FONT_SIZE_ADJUSTED:
			{
				requestNode.put(Key.FONT_SIZE_ADJUSTED.key(), false);
			}
			case GRAPHICS_FORMAT:
			{
				requestNode.put(Key.GRAPHICS_FORMAT.key(), 3);
			}
			case HEIGHT:
			{
				requestNode.put(Key.HEIGHT.key(), 100);
			}
			case OPAQUE:
			{
				requestNode.put(Key.OPAQUE.key(), false);
			}
			case POS_X:
			{
				requestNode.put(Key.POS_X.key(), 10);
			}
			case POS_Y:
			{
				requestNode.put(Key.POS_Y.key(), 10);
			}
			case TEXT:
			{
				requestNode.put(Key.TEXT.key(), "Mein Text");
			}
			case TEXT_OFFSET:
			{
				requestNode.put(Key.TEXT_OFFSET.key(), 50);
			}
			case TEXT_TOP:
			{
				requestNode.put(Key.TEXT_TOP.key(), true);
			}
			case TEXT_VISIBLE:
			{
				requestNode.put(Key.TEXT_VISIBLE.key(), false);
			}
			case TITLE:
			{
				requestNode.put(Key.TITLE.key(), "TITEL");
			}
			case TRANSFORM:
			{
				requestNode.put(Key.TRANSFORM.key(), 1);
			}
			case WIDTH:
			{
				requestNode.put(Key.WIDTH.key(), 200);
			}
			case X_RES:
			{
				requestNode.put(Key.X_RES.key(), 72);
			}
			case Y_RES:
			{
				requestNode.put(Key.Y_RES.key(), 72);
			}
			default:
			{
				
			}
			}
		}
		String response = Barcode.generate(requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		assertEquals("OK", responseNode.get(Executor.STATUS).asText());
		Files.write(Paths.get("Isbn13_full.png"), responseNode.get(Executor.RESULT).binaryValue(), StandardOpenOption.CREATE);
		System.out.println(response);
	}

	@Test
	public void testMinimalCode39() throws IOException, BarcodeException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode requestNode = mapper.createObjectNode();
		for (Key key : Barcode.Key.values())
		{
			switch (key)
			{
			case BARCODE_TYPE:
			{
				requestNode.put(key.key(), BarcodeType.CODE39.name());
				break;
			}
			case CONTENT:
			{
				requestNode.put(key.key(), "9783423204194");
				break;
			}
			default:
			{
				
			}
			}
		}
		String response = Barcode.generate(requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		assertEquals("OK", responseNode.get(Executor.STATUS).asText());
		Files.write(Paths.get("Isbn13_min.png"), responseNode.get(Executor.RESULT).binaryValue(), StandardOpenOption.CREATE);
		System.out.println(response);
	}

}
