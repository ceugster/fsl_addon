package ch.eugster.filemaker.fsl.xml.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.eugster.filemaker.fsl.xml.Xml;

public abstract class AbstractTest extends Xml
{
	protected static ObjectMapper mapper = new ObjectMapper();

	protected Xml camt;
	
	@BeforeEach
	protected void before() throws Exception
	{
		camt = new Xml();
	}
	
	@AfterEach
	protected void after() throws Exception
	{
		camt = null;
	}
	
}
