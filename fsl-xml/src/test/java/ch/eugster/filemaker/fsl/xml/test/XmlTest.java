package ch.eugster.filemaker.fsl.xml.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.xml.Xml;

public class XmlTest extends Xml
{
//	private static String xmlCamtFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";
//
//	private static String jsonCamtFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";
//
//	private static String jsonFromXmlCamtFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.jsonFromXml";
//
//	private static String xmlFromJsonCamtFilename = "src/test/resources/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xmlFromJson";
//
//	private static String xmlCamtContent;
//
//	private static String jsonCamtContent;
//
//	private static String expectedXmlCamtContent;
//	
//	private static String expectedJsonCamtContent;
//	
//	private static String jsonSingleRootFilename = "src/test/resources/json_with_one_root_element.json";
//	
//	private static String xmlFromJsonSingleRootFilename = "src/test/resources/json_with_one_root_element.xmlFromJson";
//	
//	private static String expectedXmlSingleRootContent;
	
	@BeforeEach
	public void before() throws IOException
	{
//		File file = new File(xmlFromJsonCamtFilename);
//		InputStream is = new FileInputStream(file);
//		byte[] bytes = new byte[is.available()];
//		DataInputStream dis = new DataInputStream(is);
//		dis.readFully(bytes);
//		expectedXmlCamtContent = new String(bytes).replaceAll("\\s", "");
//		is.close();
//		file = new File(jsonFromXmlCamtFilename);
//		is = new FileInputStream(file);
//		bytes = new byte[is.available()];
//		dis = new DataInputStream(is);
//		dis.readFully(bytes);
//		expectedJsonCamtContent = new String(bytes).replaceAll("\\s", "");
//		is.close();
//		file = new File(xmlCamtFilename);
//		is = new FileInputStream(file);
//		bytes = new byte[is.available()];
//		dis = new DataInputStream(is);
//		dis.readFully(bytes);
//		xmlCamtContent = new String(bytes);
//		is.close();
//		file = new File(jsonCamtFilename);
//		is = new FileInputStream(file);
//		bytes = new byte[is.available()];
//		dis = new DataInputStream(is);
//		dis.readFully(bytes);
//		jsonCamtContent = new String(bytes);
//		is.close();
//
//		file = new File(xmlFromJsonSingleRootFilename);
//		is = new FileInputStream(file);
//		bytes = new byte[is.available()];
//		dis = new DataInputStream(is);
//		dis.readFully(bytes);
//		expectedXmlSingleRootContent = new String(bytes).replaceAll("\\s", "");
//		is.close();
	}
	
//	@Test
//	public void correct() throws IOException
//	{
//		File file = new File(xmlFromJsonCamtFilename);
//		InputStream is = new FileInputStream(file);
//		byte[] bytes = new byte[is.available()];
//		expectedXmlCamtContent = new String(bytes).replaceAll("\\s", "");
//		OutputStream os = new FileOutputStream(file);
//		os.write(expectedXmlCamtContent.getBytes());
//		os.flush();
//		os.close();
//		is.close();
//	}
//	
//	@Test
//	public void testFromFileMakerXmlFileToJson() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.XML_FILE.key(), xmlCamtFilename);
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertEquals(expectedJsonCamtContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
//		assertNull(responseNode.get(Executor.ERRORS));
//	}
//
//	@Test
//	public void testFromFileMakerJsonFileToXml() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.JSON_FILE.key(), jsonCamtFilename);
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertEquals(expectedXmlCamtContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
//		assertNull(responseNode.get(Executor.ERRORS));
//	}
//
//	@Test
//	public void testFromFileMakerXmlToJson() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.XML_CONTENT.key(), xmlCamtContent);
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertEquals(expectedJsonCamtContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
//		assertNull(responseNode.get(Executor.ERRORS));
//	}
//
//	@Test
//	public void testFromFileMakerJsonToXml() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.JSON_CONTENT.key(), jsonCamtContent);
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertEquals(expectedXmlCamtContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
//		assertNull(responseNode.get(Executor.ERRORS));
//	}
//
//	@Test
//	public void testReadNotExistingXmlFile() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.XML_FILE.key(), "gigi");
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
//		assertEquals(1, responseNode.get(Executor.ERRORS).size());
//		assertEquals("'gigi' is not a valid xml file", responseNode.get(Executor.ERRORS).get(0).asText());
//	}
//
//	@Test
//	public void testJsonFileWithOneRootElement() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.JSON_FILE.key(), jsonSingleRootFilename);
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
//		assertEquals(expectedXmlSingleRootContent, responseNode.get(Executor.RESULT).asText().replaceAll("\\s", ""));
//		assertNull(responseNode.get(Executor.ERRORS));
//	}
//
//	@Test
//	public void testReadIllegalXmlContent() throws JsonMappingException, JsonProcessingException
//	{
//		ObjectNode requestNode = mapper.createObjectNode();
//		requestNode.put(Xml.Key.XML_CONTENT.key(), "gigi");
//
//		String response = Xml.convert(requestNode.toString());
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
//		assertEquals(1, responseNode.get(Executor.ERRORS).size());
//		assertEquals("xml content is not valid", responseNode.get(Executor.ERRORS).get(0).asText());
//	}
//
//	@Test
//	public void testFromFileMakerXmlContentToJson() throws JsonMappingException, JsonProcessingException
//	{
//		String request = "{\"xml_content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><Document xmlns=\\\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xsi:schemaLocation=\\\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\\\"><BkToCstmrDbtCdtNtfctn><GrpHdr><MsgId>20221216375204007304861</MsgId><CreDtTm>2022-12-16T23:39:55</CreDtTm><MsgPgntn><PgNb>1</PgNb><LastPgInd>true</LastPgInd></MsgPgntn><AddtlInf>SPS/1.7/PROD</AddtlInf></GrpHdr><Ntfctn><Id>20221216375204007304863</Id><CreDtTm>2022-12-16T23:39:55</CreDtTm><FrToDt><FrDtTm>2022-12-10T00:00:00</FrDtTm><ToDtTm>2022-12-16T23:59:59</ToDtTm></FrToDt><RptgSrc><Prtry>OTHR</Prtry></RptgSrc><Acct><Id><IBAN>CH0809000000450010065</IBAN></Id><Ownr><Nm>Pfluger Christoph August Der Zeitpunkt Solothurn</Nm></Ownr></Acct><Ntry><NtryRef>CH5630000001450010065</NtryRef><Amt Ccy=\\\"CHF\\\">50.00</Amt><CdtDbtInd>CRDT</CdtDbtInd><RvslInd>false</RvslInd><Sts>BOOK</Sts><BookgDt><Dt>2022-12-16</Dt></BookgDt><ValDt><Dt>2022-12-16</Dt></ValDt><AcctSvcrRef>350220009M2I8XBU</AcctSvcrRef><BkTxCd><Domn><Cd>PMNT</Cd><Fmly><Cd>RCDT</Cd><SubFmlyCd>VCOM</SubFmlyCd></Fmly></Domn></BkTxCd><NtryDtls><Btch><NbOfTxs>3</NbOfTxs></Btch><TxDtls><Refs><AcctSvcrRef>221215CH09LS26M5</AcctSvcrRef><InstrId>20221215000800994396358</InstrId><Prtry><Tp>00</Tp><Ref>20221216375204708963137</Ref></Prtry></Refs><Amt Ccy=\\\"CHF\\\">20.00</Amt><CdtDbtInd>CRDT</CdtDbtInd><BkTxCd><Domn><Cd>PMNT</Cd><Fmly><Cd>RCDT</Cd><SubFmlyCd>AUTT</SubFmlyCd></Fmly></Domn></BkTxCd><RltdPties><Dbtr><Nm>Pfluger, Christoph August</Nm><PstlAdr><StrtNm>Werkhofstrasse</StrtNm><BldgNb>19</BldgNb><PstCd>4500</PstCd><TwnNm>Solothurn</TwnNm><Ctry>CH</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>CH9209000000407516054</IBAN></Id></DbtrAcct><UltmtDbtr><Nm>Linda Biedermann</Nm><PstlAdr><StrtNm>Florastr. 16</StrtNm><PstCd>4500</PstCd><TwnNm>Solothurn</TwnNm><Ctry>CH</Ctry></PstlAdr></UltmtDbtr><CdtrAcct><Id><IBAN>CH5630000001450010065</IBAN></Id></CdtrAcct></RltdPties><RltdAgts><DbtrAgt><FinInstnId><BICFI>POFICHBEXXX</BICFI><Nm>POSTFINANCE AG</Nm><PstlAdr><AdrLine>MINGERSTRASSE 20</AdrLine><AdrLine>3030 BERN</AdrLine></PstlAdr></FinInstnId></DbtrAgt></RltdAgts><RmtInf><Strd><CdtrRefInf><Tp><CdOrPrtry><Prtry>QRR</Prtry></CdOrPrtry></Tp><Ref>000000372142141220226485603</Ref></CdtrRefInf><AddtlRmtInf>?REJECT?0</AddtlRmtInf><AddtlRmtInf>?ERROR?000</AddtlRmtInf><AddtlRmtInf>Rechnung Nr. 372142</AddtlRmtInf></Strd></RmtInf><RltdDts><AccptncDtTm>2022-12-16T20:00:00</AccptncDtTm></RltdDts></TxDtls><TxDtls><Refs><AcctSvcrRef>221215CH09LSZ5WQ</AcctSvcrRef><InstrId>20221215000800994366463</InstrId><Prtry><Tp>00</Tp><Ref>20221216375204708885249</Ref></Prtry></Refs><Amt Ccy=\\\"CHF\\\">10.00</Amt><CdtDbtInd>CRDT</CdtDbtInd><BkTxCd><Domn><Cd>PMNT</Cd><Fmly><Cd>RCDT</Cd><SubFmlyCd>AUTT</SubFmlyCd></Fmly></Domn></BkTxCd><RltdPties><Dbtr><Nm>Pfluger, Christoph August</Nm><PstlAdr><StrtNm>Werkhofstrasse</StrtNm><BldgNb>19</BldgNb><PstCd>4500</PstCd><TwnNm>Solothurn</TwnNm><Ctry>CH</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>CH9209000000407516054</IBAN></Id></DbtrAcct><UltmtDbtr><Nm>Christoph Pfluger</Nm><PstlAdr><StrtNm>Werkhofstr. 19</StrtNm><PstCd>4500</PstCd><TwnNm>Solothurn</TwnNm><Ctry>CH</Ctry></PstlAdr></UltmtDbtr><CdtrAcct><Id><IBAN>CH5630000001450010065</IBAN></Id></CdtrAcct></RltdPties><RltdAgts><DbtrAgt><FinInstnId><BICFI>POFICHBEXXX</BICFI><Nm>POSTFINANCE AG</Nm><PstlAdr><AdrLine>MINGERSTRASSE 20</AdrLine><AdrLine>3030 BERN</AdrLine></PstlAdr></FinInstnId></DbtrAgt></RltdAgts><RmtInf><Strd><CdtrRefInf><Tp><CdOrPrtry><Prtry>QRR</Prtry></CdOrPrtry></Tp><Ref>000000372144141220225496407</Ref></CdtrRefInf><AddtlRmtInf>?REJECT?0</AddtlRmtInf><AddtlRmtInf>?ERROR?000</AddtlRmtInf><AddtlRmtInf>Rechnung Nr. 372144</AddtlRmtInf></Strd></RmtInf><RltdDts><AccptncDtTm>2022-12-16T20:00:00</AccptncDtTm></RltdDts></TxDtls><TxDtls><Refs><AcctSvcrRef>221215CH09LUCD83</AcctSvcrRef><InstrId>20221215000800994414138</InstrId><Prtry><Tp>00</Tp><Ref>20221216375204708914844</Ref></Prtry></Refs><Amt Ccy=\\\"CHF\\\">20.00</Amt><CdtDbtInd>CRDT</CdtDbtInd><BkTxCd><Domn><Cd>PMNT</Cd><Fmly><Cd>RCDT</Cd><SubFmlyCd>AUTT</SubFmlyCd></Fmly></Domn></BkTxCd><RltdPties><Dbtr><Nm>Pfluger, Christoph August</Nm><PstlAdr><StrtNm>Werkhofstrasse</StrtNm><BldgNb>19</BldgNb><PstCd>4500</PstCd><TwnNm>Solothurn</TwnNm><Ctry>CH</Ctry></PstlAdr></Dbtr><DbtrAcct><Id><IBAN>CH9209000000407516054</IBAN></Id></DbtrAcct><UltmtDbtr><Nm>Linda Biedermann</Nm><PstlAdr><StrtNm>Spinngasse 6</StrtNm><PstCd>4552</PstCd><TwnNm>Derendingen</TwnNm><Ctry>CH</Ctry></PstlAdr></UltmtDbtr><CdtrAcct><Id><IBAN>CH5630000001450010065</IBAN></Id></CdtrAcct></RltdPties><RltdAgts><DbtrAgt><FinInstnId><BICFI>POFICHBEXXX</BICFI><Nm>POSTFINANCE AG</Nm><PstlAdr><AdrLine>MINGERSTRASSE 20</AdrLine><AdrLine>3030 BERN</AdrLine></PstlAdr></FinInstnId></DbtrAgt></RltdAgts><RmtInf><Strd><CdtrRefInf><Tp><CdOrPrtry><Prtry>QRR</Prtry></CdOrPrtry></Tp><Ref>000000372143141220225247907</Ref></CdtrRefInf><AddtlRmtInf>?REJECT?0</AddtlRmtInf><AddtlRmtInf>?ERROR?000</AddtlRmtInf><AddtlRmtInf>Rechnung Nr. 372143</AddtlRmtInf></Strd></RmtInf><RltdDts><AccptncDtTm>2022-12-16T20:00:00</AccptncDtTm></RltdDts></TxDtls></NtryDtls><AddtlNtryInf>SAMMELGUTSCHRIFT FÜR KONTO: CH5630000001450010065 VERARBEITUNG VOM 16.12.2022 PAKET ID: 221216CH000008UO</AddtlNtryInf></Ntry></Ntfctn></BkToCstmrDbtCdtNtfctn></Document>\\\"}\"}";
//		
//		String response = Xml.convert(request);
//		
//		JsonNode responseNode = mapper.readTree(response);
//		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
//		assertEquals(1, responseNode.get(Executor.ERRORS).size());
//		assertEquals("xml content is not valid", responseNode.get(Executor.ERRORS).get(0).asText());
//	}
	
	@Test
	public void testNewDocument() throws Exception
	{
		Element rootElement = Xml.createDocument("root");
		assertEquals("root", rootElement.getName());
		String content = XML_OUTPUTTER.outputString(Xml.document);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", content);
	}

	@Test
	public void testNewDocumentWithEmptyRootElementName() throws Exception
	{
		try
		{
			Xml.createDocument("");
		}
		catch (Exception e)
		{
			assertEquals("Illegal element name: \"\"", e.getLocalizedMessage());
		}
	}

	@Test
	public void testNewDocumentWithoutRootElementName() throws Exception
	{
		try
		{
			Xml.createDocument(null);
		}
		catch (Exception e)
		{
			assertEquals("Illegal element name: \"\"", e.getLocalizedMessage());
		}
	}

	@Test
	public void testBuildDocument() throws Exception
	{
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root></root>\r\n";
		Document document = Xml.buildDocument(content);
		assertEquals("root", document.getRootElement().getName());
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", XML_OUTPUTTER.outputString(document));
	}

	@Test
	public void testBuildDocumentWithoutElements() throws Exception
	{
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
		Document document = Xml.buildDocument(content);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", XML_OUTPUTTER.outputString(document));
	}
	
	@Test
	public void testSetElementWithWrongRootElementName() throws Exception
	{
		String path = "anotherName";
		try
		{
			Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", path, "value", "XMLString");
		}
		catch (Exception e)
		{
			assertTrue(Exception.class.isInstance(e));
			assertEquals("Illegal root element name: anotherName", e.getLocalizedMessage());
		}
	}

	@Test
	public void testSetRootElementWithoutExistingRootElementAndStringContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", path, "value", "XMLString");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>value</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithoutExistingRootElementAndNumberContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", path, "12'df.20", "XMLNumeric");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>12.20</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithoutExistingRootElementAndBooleanContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", path, "12", "XMLBoolean");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>true</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithoutExistingRootElementAndBooleanContentFromString() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", path, "abc", "XMLBoolean");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>false</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithExistingRootElementAndStringContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", path, "value", "XMLString");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>value</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithExistingRootElementAndNumberContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", path, "12'df.20", "XMLNumeric");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>12.20</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithExistingRootElementAndBooleanContent() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root />\r\n", path, "12", "XMLBoolean");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>true</root>\r\n", content);
	}

	@Test
	public void testSetRootElementWithExistingRootElementAndBooleanContentFromString() throws Exception
	{
		String path = "root";
		String content = Xml.XMLSetElement("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n", path, "abc", "XMLBoolean");
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<root>false</root>\r\n", content);
	}

}
