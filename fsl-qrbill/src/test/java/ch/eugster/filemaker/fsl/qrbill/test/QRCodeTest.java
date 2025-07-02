package ch.eugster.filemaker.fsl.qrbill.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.qrbill.Executor;
import ch.eugster.filemaker.fsl.qrbill.QRBill;
import ch.eugster.filemaker.fsl.qrbill.QRCode;
import ch.eugster.filemaker.fsl.qrbill.QRCode.Key;

public class QRCodeTest 
{
	private ObjectMapper mapper = new ObjectMapper();

	protected QRBill qrbill;
	
	@BeforeEach
	protected void beforeEach()
	{
		if (Objects.isNull(qrbill))
		{
			qrbill = new QRBill();
		}
	}
	
	@AfterEach
	protected void afterEach()
	{
//		FileUtils.deleteQuietly(Paths.get(System.getProperty("user.home"), ".fsl", "qrbill", "requestNode.json").toFile());
	}
	
	@Test
	public void testInvalidCommand() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = this.mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		JsonNode errorsNode = responseNode.get(Executor.ERRORS);
		assertEquals(ArrayNode.class, errorsNode.getClass());
		assertEquals(8, errorsNode.size());
		ArrayNode errors = ArrayNode.class.cast(responseNode.get(Executor.ERRORS));
		Iterator<JsonNode> node = errors.iterator();
		while (node.hasNext())
		{
			String errorMessage = node.next().asText();
			if (errorMessage.equals("ref_invalid: 'reference'"))
				assertEquals(errorMessage, "ref_invalid: 'reference'");
			else if (errorMessage.equals("field_value_missing: 'account'"))
				assertEquals(errorMessage, "field_value_missing: 'account'");
			else if (errorMessage.equals("field_value_missing: 'creditor.name'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.name'");
			else if (errorMessage.equals("field_value_missing: 'creditor.town'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.town'");
			else if (errorMessage.equals("field_value_missing: 'creditor.countryCode'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.countryCode'");
			else if (errorMessage.equals("field_value_missing: 'creditor.postalCode'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.postalCode'");
			else if (errorMessage.equals("field_value_missing: 'creditor.addressLine2'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.addressLine2'");
			else if (errorMessage.equals("field_value_missing: 'currency'"))
				assertEquals(errorMessage, "field_value_missing: 'currency'");
			else fail();
		}
	}

	@Test
	public void testEmptyParameters() throws IOException
	{
		String response = QRBill.generate("");

		JsonNode responseNode = this.mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		JsonNode errorsNode = responseNode.get(Executor.ERRORS);
		assertEquals(ArrayNode.class, errorsNode.getClass());
		assertEquals(1, errorsNode.size());
		JsonNode errorNode = errorsNode.iterator().next();
		assertEquals("missing argument 'request'", errorNode.asText());
	}

	@Test
	public void testWrongParameters() throws IOException
	{
		String response = QRBill.generate("{\"workbook\":}");

		JsonNode responseNode = this.mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		JsonNode errorsNode = responseNode.get(Executor.ERRORS);
		assertEquals(ArrayNode.class, errorsNode.getClass());
		assertEquals(1, errorsNode.size());
		JsonNode errorNode = errorsNode.iterator().next();
		assertEquals("cannot process 'request': illegal json format", errorNode.asText());
	}

	@Test
	public void testMinimalParametersValid() throws IOException, NoSuchAlgorithmException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRCode.Key.VALUE.key(), "ADI3004000D054RECH00000006002959");

		String response = QRCode.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		byte[] result = responseNode.get(QRCode.Key.RESULT.key()).binaryValue();
		Files.write(Paths.get("image_simple.png"), result, StandardOpenOption.CREATE);
		assertEquals("ADI3004000D054RECH00000006002959", responseNode.get(Key.VALUE.key()).asText());
		MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(result);
	    byte[] digest1 = md.digest();
	    byte[] content = Files.readAllBytes(Paths.get("image_simple.png"));
	    md.update(content);
	    byte[] digest2 = md.digest();
	    for (int i = 0; i < Math.max(digest1.length, digest2.length); i++)
	    {
	    	assertEquals( digest2[i], digest1[i]);
	    }
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testAllParametersProvided() throws IOException, NoSuchAlgorithmException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRCode.Key.VALUE.key(), "ADI3004000D054RECH00000006002959");
		requestNode.put(QRCode.Key.SCALE.key(), 10);
		requestNode.put(QRCode.Key.BORDER.key(), 4);
		requestNode.put(QRCode.Key.COLOR_LIGHT.key(), 0xAAAAAA);
		requestNode.put(QRCode.Key.COLOR_DARK.key(), 0x444444);
		requestNode.put(QRCode.Key.ECC.key(), 1);
		requestNode.put(QRCode.Key.GRAPHICS_FORMAT.key(), "PNG");

		String response = QRCode.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		byte[] result = responseNode.get(QRCode.Key.RESULT.key()).binaryValue();
		Files.write(Paths.get("qrcode_all_params.png"), result, StandardOpenOption.CREATE);
		assertEquals("ADI3004000D054RECH00000006002959", responseNode.get(Key.VALUE.key()).asText());
		MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(result);
	    byte[] digest1 = md.digest();
	    byte[] content = Files.readAllBytes(Paths.get("image_all.png"));
	    md.update(content);
	    byte[] digest2 = md.digest();
	    for (int i = 0; i < Math.max(digest1.length, digest2.length); i++)
	    {
	    	assertEquals( digest2[i], digest1[i]);
	    }
		assertNull(responseNode.get(Executor.ERRORS));
	}

}
