package ch.eugster.filemaker.fsl.qrbill.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;

public class QRBillTest
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
	
	private void copyConfiguration(String sourcePath) throws IOException
	{
//		File responseNode = Paths.get(System.getProperty("user.home"), ".fsl", "qrbill", "requestNode.json").toFile();
//		if (responseNode.exists())
//		{
//			responseNode.delete();
//		}
//		File source = new File(sourcePath).getAbsoluteFile();
//		FileUtils.copyFile(source, responseNode);
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
	public void testMinimalParametersValid() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(responseNode.get(Executor.RESULT));
		assertEquals("210000000003139471430009017", responseNode.get("reference").asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testMinimalParametersValidRef26() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "21000000000313947143000901");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(responseNode.get(Executor.RESULT));
		assertEquals("210000000003139471430009017", responseNode.get("reference").asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testMinimalParametersWithNonQRIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH450023023099999999A");
		requestNode.put(QRBill.Key.REFERENCE.key(), "RF49N73GBST73AKL38ZX");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(responseNode.get(Executor.RESULT));
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testMissingAllMandatories() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
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
	public void testWithForeignIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "IT12V0827358981000302206625");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("account_iban_not_from_ch_or_li: 'account'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testWithNormalIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("qr_ref_invalid_use_for_non_qr_iban: 'reference'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidCurrency() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "USD");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("currency_not_chf_or_eur: 'currency'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidReference() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "FS000000000000000000000000");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("ref_invalid: 'reference'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testAllValid() throws IOException
	{
		this.copyConfiguration("src/test/resources/cfg/qrbill_all.json");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("amount", new BigDecimal(350));
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		requestNode.put("message", "Abonnement für 2020");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode debtor = requestNode.putObject("debtor");
		debtor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		debtor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		debtor.put(QRBill.Key.HOUSE_NO.key(), "27");
		debtor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		debtor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		debtor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBill.Key.LANGUAGE.key(), Language.DE.name());

		String response = QRBill.generate(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(responseNode.get(Executor.RESULT));
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testWithReferenceLength26() throws IOException
	{
		this.copyConfiguration("src/test/resources/cfg/qrbill_all.json");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("amount", new BigDecimal(350));
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "21000000000313947143000901");
		requestNode.put("message", "Abonnement für 2020");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode debtor = requestNode.putObject("debtor");
		debtor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		debtor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		debtor.put(QRBill.Key.HOUSE_NO.key(), "27");
		debtor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		debtor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		debtor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBill.Key.LANGUAGE.key(), Language.DE.name());

		String response = QRBill.generate(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNotNull(responseNode.get(Executor.RESULT));
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testInvalidAddress() throws IOException
	{
		this.copyConfiguration("src/test/resources/cfg/qrbill_all.json");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("amount", new BigDecimal(350));
		requestNode.put(QRBill.Key.CURRENCY.key(), "CHF");
		requestNode.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		requestNode.put("message", "Abonnement für 2020");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		ObjectNode debtor = requestNode.putObject("debtor");
		debtor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBill.Key.LANGUAGE.key(), Language.DE.name());

		String response = QRBill.generate(requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(ArrayNode.class, responseNode.get(Executor.ERRORS).getClass());
		assertEquals(8, responseNode.get(Executor.ERRORS).size());
		ArrayNode errors = ArrayNode.class.cast(responseNode.get(Executor.ERRORS));
		Iterator<JsonNode> node = errors.iterator();
		while (node.hasNext())
		{
			String errorMessage = node.next().asText();
			if (errorMessage.equals("field_value_missing: 'creditor.name'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.name'");
			else if (errorMessage.equals("field_value_missing: 'creditor.postalCode'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.postalCode'");
			else if (errorMessage.equals("field_value_missing: 'creditor.town'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.town'");
			else if (errorMessage.equals("field_value_missing: 'creditor.countryCode'"))
				assertEquals(errorMessage, "field_value_missing: 'creditor.countryCode'");
			else if (errorMessage.equals("field_value_missing: 'debtor.name'"))
				assertEquals(errorMessage, "field_value_missing: 'debtor.name'");
			else if (errorMessage.equals("field_value_missing: 'debtor.postalCode'"))
				assertEquals(errorMessage, "field_value_missing: 'debtor.postalCode'");
			else if (errorMessage.equals("field_value_missing: 'debtor.town'"))
				assertEquals(errorMessage, "field_value_missing: 'debtor.town'");
			else if (errorMessage.equals("field_value_missing: 'debtor.countryCode'"))
				assertEquals(errorMessage, "field_value_missing: 'debtor.countryCode'");
			else fail();
		}
	}

	@Test
	public void testInvalidGraphicsFormat() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "USD");
		requestNode.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), "blabla");
		form.put(QRBill.Key.LANGUAGE.key(), Language.IT.toString());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.toString());

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals(
				"invalid_json_format_parameter: 'No enum constant net.codecrete.qrbill.generator.GraphicsFormat.blabla'",
				responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidLanguage() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "USD");
		requestNode.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.toString());
		form.put(QRBill.Key.LANGUAGE.key(), "blabla");
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.toString());

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid_json_format_parameter: 'No enum constant net.codecrete.qrbill.generator.Language.blabla'",
				responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidOutputSize() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = this.mapper.createObjectNode();
		requestNode.put(QRBill.Key.CURRENCY.key(), "USD");
		requestNode.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		requestNode.put(QRBill.Key.REFERENCE.key(), "210000000003139471430009017");
		ObjectNode creditor = requestNode.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.STREET.key(), "Axensteinstrasse");
		creditor.put(QRBill.Key.HOUSE_NO.key(), "27");
		creditor.put(QRBill.Key.POSTAL_CODE.key(), "9000");
		creditor.put(QRBill.Key.TOWN.key(), "St. Gallen");
		creditor.put(QRBill.Key.COUNTRY_CODE.key(), "CH");
		ObjectNode form = requestNode.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.toString());
		form.put(QRBill.Key.LANGUAGE.key(), Language.IT.toString());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), "blabla");

		String response = QRBill.generate(requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals(
				"invalid_json_format_parameter: 'No enum constant net.codecrete.qrbill.generator.OutputSize.blabla'",
				responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testFromFileMaker() throws IOException
	{
		String request = "{\"amount\":124.2,\"creditor\":{\"countryCode\":\"CH\",\"houseNo\":\"101\",\"name\":\"Copy Art AG\",\"postalCode\":\"9014\",\"street\":\"Industriestrasse\",\"town\":\"St. Gallen\"},\"currency\":\"CHF\",\"debtor\":{\"countryCode\":\"CH\",\"houseNo\":\"149\",\"name\":\"KA Boom AG,  \",\"postalCode\":\"9200\",\"town\":\"Gossau SG\"},\"format\":{\"graphics_format\":\"PDF\",\"language\":\"DE\",\"output_size\":\"QR_BILL_ONLY\"},\"iban\":\"CH4431999123000889012\",\"message\":\"Rechnung Nr. 30.10.2012\",\"reference\":\"00000012123000000020121030\"}";
//		String request = "{\"amount\":751.75,\"creditor\":{\"street\":\"Fürstenlandstrasse\",\"houseNo\":\"101\",\"postalCode\":\"9014\",\"town\":\"St. Gallen\",\"countryCode\":\"CH\",\"name\":\"CopyArt\"},\"currency\":\"CHF\",\"debtor\":{\"street\":\"Neugasse\",\"houseNo\":\"1\",\"postalCode\":\"9004\",\"town\":\"St. Gallen\",\"countryCode\":\"CH\",\"name\":\"Hochbauamt Stadt St.Gallen\"},\"format\":{\"graphics_format\":\"PDF\",\"language\":\"DE\",\"output_size\":\"QR_BILL_ONLY\"},\"iban\":\"CH5909000000900221261\",\"reference\":\"RF49N73GBST73AKL38ZX\"}";

		String response = QRBill.generate(request);

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertNotNull(responseNode.get("result"));
		byte[] result = responseNode.get("result").binaryValue();
		File file = new File("/Users/christian/qrbill.pdf");
		OutputStream os = new FileOutputStream(file);
		os.write(result);
		os.close();
	}
}