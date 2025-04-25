package ch.eugster.filemaker.fsl.qrbill;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;

/**
 * Generates swiss qrbills from json parameters. Based on the works of
 * net.codecrete.qrbill/qrbill-generatory by manuelbl,
 * 
 * @author christian
 *
 */
public class QRBill extends Executor
{
    private static final int[] MOD_10 = { 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };

	public static String generate(String request)
	{
		if (createRequestNode(request))
		{
			try 
			{
				Bill bill = new Bill();
				bill.setAccount(checkString(getRequestNode(), Key.IBAN.key()));
				bill.setReference(checkReference(getRequestNode(), Key.REFERENCE.key()));
				bill.setAmountFromDouble(checkDouble(getRequestNode(), Key.AMOUNT.key()));
				bill.setCurrency(checkString(getRequestNode(), Key.CURRENCY.key()));
				bill.setUnstructuredMessage(checkString(getRequestNode(), Key.MESSAGE.key()));
	
				JsonNode creditor = getRequestNode().get(Key.CREDITOR.key());
				if (JsonNode.class.isInstance(creditor))
				{
					Address address = new Address();
					address.setName(checkString(creditor, Key.NAME.key()));
					address.setStreet(checkString(creditor, Key.STREET.key()));
					address.setHouseNo(checkString(creditor, Key.HOUSE_NO.key()));
					address.setPostalCode(checkString(creditor, Key.POSTAL_CODE.key()));
					address.setTown(checkString(creditor, Key.TOWN.key()));
					address.setCountryCode(checkString(creditor, Key.COUNTRY_CODE.key()));
					bill.setCreditor(address);
				}
	
				JsonNode debtor = getRequestNode().get(Key.DEBTOR.key());
				if (JsonNode.class.isInstance(debtor))
				{
					Address address = new Address();
					address.setName(checkString(debtor, Key.NAME.key()));
					address.setStreet(checkString(debtor, Key.STREET.key()));
					address.setHouseNo(checkString(debtor, Key.HOUSE_NO.key()));
					address.setPostalCode(checkString(debtor, Key.POSTAL_CODE.key()));
					address.setTown(checkString(debtor, Key.TOWN.key()));
					address.setCountryCode(checkString(debtor, Key.COUNTRY_CODE.key()));
					bill.setDebtor(address);
				}
	
				JsonNode form = getRequestNode().get(Key.FORMAT.key());
				if (JsonNode.class.isInstance(form))
				{
					BillFormat format = new BillFormat();
					format.setGraphicsFormat(checkGraphicsFormat(form));
					format.setLanguage(checkLanguage(form));
					format.setOutputSize(checkOutputSize(form));
					bill.setFormat(format);
				}
	
				ValidationResult validation = net.codecrete.qrbill.generator.QRBill.validate(bill);
				if (validation.isValid())
				{
					byte[] swissqrbill = net.codecrete.qrbill.generator.QRBill.generate(bill);
					getResponseNode().put(Executor.RESULT, swissqrbill);
				}
				else
				{
					List<ValidationMessage> msgs = validation.getValidationMessages();
					if (!msgs.isEmpty())
					{
						for (ValidationMessage msg : msgs)
						{
							addErrorMessage(msg.getMessageKey() + ": '" + msg.getField() + "'");
						}
					}
				}
			} 
			catch (Exception e) 
			{
				addErrorMessage("invalid_json_format_parameter: '" + e.getLocalizedMessage() + "'");
			}
		}
		return getResponse();
	}
	
	private static String checkString(JsonNode requestNode, String key)
	{
		if (Objects.nonNull(requestNode))
		{
			JsonNode node = requestNode.get(key);
			return Objects.nonNull(node) ? node.asText() : null;
		}
		return null;
	}

	private static Double checkDouble(JsonNode requestNode, String key)
	{
		if (Objects.nonNull(requestNode))
		{
			JsonNode node = requestNode.get(key);
			return Objects.nonNull(node) ? node.asDouble() : null;
		}
		return null;
	}
	
	private static GraphicsFormat checkGraphicsFormat(JsonNode format)
	{
		JsonNode f = format.get(Key.GRAPHICS_FORMAT.key());
		if (f == null)
			return GraphicsFormat.PDF;
		return GraphicsFormat.valueOf(f.asText());
	}

	private static OutputSize checkOutputSize(JsonNode format)
	{
		JsonNode f = format.get(Key.OUTPUT_SIZE.key());
		if (f == null)
			return OutputSize.QR_BILL_EXTRA_SPACE;
		return OutputSize.valueOf(f.asText());
	}

	private static Language checkLanguage(JsonNode format)
	{
		JsonNode f = format.get(Key.LANGUAGE.key());
		if (f == null)
			return Language.DE;
		return Language.valueOf(f.asText());
	}
	
	private static String checkReference(JsonNode requestNode, String key)
	{
		String reference = null;
		if (Objects.nonNull(requestNode)) 
		{
			try
			{
				reference = requestNode.get(key).asText();
			}
			catch (NullPointerException e)
			{
				addErrorMessage("ref_invalid: 'reference'");
			}
			if (Objects.nonNull(reference))
			{
				if (isNumeric(reference))
				{
					if (reference.length() == 26)
					{
						int checksum = calculateMod10(reference);
						reference = reference + String.valueOf(checksum);
					}
					else if (reference.length() == 27)
					{
						int checksum = Integer.valueOf(reference.substring(26));
						String referenceWithoutChecksum = reference.substring(0, 26);
						if (checksum != calculateMod10(referenceWithoutChecksum))
						{
							addErrorMessage("ref_invalid: 'reference'");
						}
					}
				}
				else 
				{
					reference = checkString(requestNode, key);
				}
				getResponseNode().put("reference", reference);
			}			
		}
		return reference;
	}
	
    private static boolean isNumeric(String value) 
    {
        int len = value.length();
        for (int i = 0; i < len; i++) 
        {
            char ch = value.charAt(i);
            if (ch < '0' || ch > '9')
            {
                return false;
            }
        }
        return true;
    }
    private static int calculateMod10(String reference) 
    {
        int len = reference.length();
        int carry = 0;
        for (int i = 0; i < len; i++) 
        {
            int digit = reference.charAt(i) - '0';
            carry = MOD_10[(carry + digit) % 10];
        }
        return (10 - carry) % 10;
    }
	
//	private static Parameters loadDefaultParameters()
//	{
//		Parameters params = null;
//		if (Objects.isNull(params))
//		{
//			Path cfg = Paths.get(System.getProperty("user.home"), ".fsl", "parameters.json");
//			File file = cfg.toFile();
//			if (file.isFile() && file.canRead())
//			{
//				try
//				{
//					params = mapper.readValue(file, Parameters.class);
//				}
//				catch (Exception e)
//				{
//					params = new Parameters();
//				}
//			}
//		}
//		return params;
//	}
//	
	public enum Key
	{
		// @formatter:off
		IBAN("iban"), REFERENCE("reference"), AMOUNT("amount"), CURRENCY("currency"), MESSAGE("message"),
		CREDITOR("creditor"), DEBTOR("debtor"), NAME("name"), STREET("street"), HOUSE_NO("houseNo"), POSTAL_CODE("postalCode"), TOWN("town"), COUNTRY_CODE("countryCode"),
		FORMAT("format"), GRAPHICS_FORMAT("graphics_format"), OUTPUT_SIZE("output_size"), LANGUAGE("language");
		
		private String key;
		
		private Key(String key)
		{
			this.key = key;
		}
		
		public String key()
		{
			return this.key;
		}
	}
}