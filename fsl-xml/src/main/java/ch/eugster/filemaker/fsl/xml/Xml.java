package ch.eugster.filemaker.fsl.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;
import org.json.XML;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Xml extends Executor
{
//	private static Logger logger = LoggerFactory.getLogger(Xml.class);

	static
	{
		try 
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			DOCUMENT_BUILDER = factory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static final String IDENTIFIER_KEY = "identifier";
	
	public static DocumentBuilder DOCUMENT_BUILDER;
	
	public static final DOMBuilder DOM_BUILDER = new DOMBuilder();
	
	public static final XMLOutputter XML_OUTPUTTER = new XMLOutputter();
	
	public static Document document;
	
	public static String convert(String request)
	{
		if (createRequestNode(request))
		{
			doConvert();
		}
		return getResponse();
	}
	
	public static String XMLSetElement(String document, String path, String value, String type) throws Exception
	{
		Element element = null;
		String[] elementNames = path.split("[.]");
		if ((Objects.isNull(document) || document.isBlank() || document.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").isBlank()) && Objects.nonNull(path) && !path.isBlank())
		{
			element = createDocument(elementNames[0]);
		}
		else
		{
			Xml.document = buildDocument(document);
		}
		element = findElement(Xml.document, elementNames);
		if (type.equals("XMLNumeric"))
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < value.length(); i++)
			{
				if ("-1234567890.".contains(value.substring(i, i + 1)))
				{
					sb = sb.append(value.substring(i, i + 1));
				}
			}
			try
			{
				new BigDecimal(sb.toString());
				element = element.addContent(sb.toString());
			}
			catch (Exception e)
			{
				throw new Exception("Invalid number format: " + value);
			}
		}
		else if (type.equals("XMLBoolean"))
		{
			try
			{
				BigDecimal number = new BigDecimal(value);
				element = element.addContent(number.equals(BigDecimal.ZERO) ? "false" : "true");
			}
			catch (Exception e)
			{
				Boolean bool = Boolean.valueOf(value);
				element = element.addContent(bool.equals(Boolean.TRUE) ? "true" : "false");
			}
		}
		else if (type.equals("XMLString"))
		{
			element = element.addContent(value);
		}
		return XML_OUTPUTTER.outputString(Xml.document);
	}
	
//	public static String XMLDeleteElement(String document, String path) throws SAXException, IOException
//	{
//		Xml.document = buildDocument(document);
//		Element root = Xml.document.getRootElement();
//
//		String[] elementNames = path.split("[.]");
//		int index = 0;
//		int s = elementNames[0].indexOf("[");
//		String elementName = null;
//		if (s == -1)
//		{
//			elementName = elementNames[0];
//		}
//		else
//		{
//			elementName = elementNames[0].substring(0, s - 1);
//			index = Integer.valueOf(elementName.substring(s + 1, elementName.length() - 1));
//		}
//		
//		Element element = null;
//		if (root.getName().equals(elementName))
//		{
//			element = root.detach();
//		}
//		else
//		{
//			element = root.getChildren(elementName).get(index);
//			if (element != null)
//			{
//				findElement(element, )
//			}
//		}
//		
//	}
	
	protected static Element findElement(Document document, String[] elementNames) throws Exception
	{
		Element element = null;
		Element parentElement = document.getRootElement();
		if (elementNames[0].equals(parentElement.getName()))
		{
			for (int i = 1; i < elementNames.length; i++)
			{
				element = parentElement.getChild(elementNames[i]);
				if (Objects.isNull(element))
				{
					element = new Element(elementNames[i]);
					parentElement.addContent(element);
				}
				parentElement = element;
			}
		}
		else
		{
			throw new Exception("Invalid root element name: " + elementNames[0]);
		}
		return parentElement;
	}

	
	protected static Element createDocument(String elementName) throws Exception 
	{
		Element element = null;
		try
		{
			Xml.document = new Document();
			element = new Element(elementName);
			Xml.document.addContent(element);
		}
		catch (Exception e)
		{
			throw new Exception("Invalid element name: \"\"");
		}
		return element;
	}

	private static Element addElement(Element parentElement, String elementName)
	{
		Element element = new Element(elementName);
		parentElement.addContent(element);
		return element;
	}
	
	public static void setElement(String document, String path, String value, String type) throws Exception
	{
		ByteArrayInputStream bais = null;
		try
		{
			Element element = null;
			bais = new ByteArrayInputStream(document.getBytes());
			org.w3c.dom.Document doc = DOCUMENT_BUILDER.parse(bais);
			Xml.document = DOM_BUILDER.build(doc);
			String[] es = path.split("[.]");
			for (int i = 0; i < es.length; i++)
			{
				if (es[i].contains("["))
				{
					element = Xml.document.getRootElement().getChildren().get(i);
				}
				else
				{
					element = Xml.document.getRootElement().getChild(es[i]);
				}
			}
			if (element != null)
			{
				
			}
		}
		finally
		{
			if (bais != null)
			{
				bais.close();
			}
		}
	}

	private static void setElement(Element parentElement, String subPath, String tag, String value)
	{
		
	}
	
	protected static Document buildDocument(String doc) throws Exception
	{
		org.w3c.dom.Document document = null;
		try
		{
			InputSource is = new InputSource(new StringReader(doc));
			document = DOCUMENT_BUILDER.parse(is);
		}
		catch (Exception e)
		{
			throw new Exception("Invalid file format");
		}
		return DOM_BUILDER.build(document);
	}
	
//	private static Element findElement(Element element, String path)
//	{
//	}
	
	private static boolean doConvert()
	{
		boolean result = true;
		JsonNode xmlFileNode = getRequestNode().findPath(Key.XML_FILE.key());
		if (xmlFileNode.isTextual())
		{
			File file = new File(xmlFileNode.asText());
			if (file.isFile())
			{
				result = readXmlFileAndConvertToJson(file);
			}
			else
			{
				result = addErrorMessage("'" + file.getName()+ "' is not a valid xml file");
			}
		}
		else if (xmlFileNode.isMissingNode())
		{
			JsonNode jsonFileNode = getRequestNode().findPath(Key.JSON_FILE.key());
			if (jsonFileNode.isTextual())
			{
				File file = new File(jsonFileNode.asText());
				if (file.isFile())
				{
					result = readJsonFileAndConvertToXml(file);
				}
				else
				{
					result = addErrorMessage("'" + file.getName()+ "' is not a valid json file");
				}
			}
			else if (jsonFileNode.isMissingNode())
			{
				JsonNode xmlContentNode = getRequestNode().findPath(Key.XML_CONTENT.key());
				if (xmlContentNode.isTextual())
				{
					String xml = xmlContentNode.asText();
					result = convertXmlToJson(xml);
				}
				else if (xmlContentNode.isMissingNode())
				{
					JsonNode jsonContentNode = getRequestNode().findPath(Key.JSON_CONTENT.key());
					if (jsonContentNode.isTextual())
					{
						String json = jsonContentNode.asText();
						result = convertJsonToXml(json);
					}
					else if (jsonContentNode.isMissingNode())
					{
						result = addErrorMessage("missing argument, one of '" + Key.XML_FILE.key() + "', '" + Key.XML_CONTENT.key() + "', " + Key.JSON_FILE.key() + "', or '" + Key.JSON_CONTENT.key() + "'");
					}
				}
			}
		}
		return result;
	}

	private static boolean readXmlFileAndConvertToJson(File file)
	{
		boolean result = true;
		try
		{
			String xml = FileUtils.readFileToString(file, "UTF-8");
			result = convertXmlToJson(xml);
		}
		catch (Exception e)
		{
			result = addErrorMessage("'" + file.getName()+ "' is not a valid xml file");
		}
		return result;
	}
	
	private static boolean readJsonFileAndConvertToXml(File file)
	{
		boolean result = true;
		try
		{
			String json = FileUtils.readFileToString(file, "UTF-8");
			result = convertJsonToXml(json);
		}
		catch (Exception e)
		{
			result = addErrorMessage("'" + file.getName()+ "' is not a valid json file");
		}
		return result;
	}
	
	private static boolean convertXmlToJson(String xml)
	{
		boolean result = true;
		try
		{
		    SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		    saxParser.parse(stream, new DefaultHandler());
			JSONObject jsonObject = XML.toJSONObject(xml);
			getResponseNode().put(Executor.RESULT, jsonObject.toString());
		}
		catch (Exception e)
		{
			addErrorMessage("xml content is not valid");
		}
		return result;
	}
	
	private static boolean convertJsonToXml(String json)
	{
		boolean result = true;
		try
		{
			JsonNode tree = mapper.readTree(json);
			ObjectNode objectNode = mapper.createObjectNode();
			objectNode = objectNode.set("root", tree);
			json = objectNode.toPrettyString();
			JSONObject jsonObject = new JSONObject(json);
		    getResponseNode().put(Executor.RESULT, XML.toString(jsonObject));
		}
		catch (Exception e)
		{
			result = addErrorMessage("json content is not valid");
		}
		return result;
	}
	
	public enum Key
	{
		// @formatter:off
		XML_FILE("xml_file"),
		XML_CONTENT("xml_content"),
		JSON_FILE("json_file"),
		JSON_CONTENT("json_content");
		// @formatter:on

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
