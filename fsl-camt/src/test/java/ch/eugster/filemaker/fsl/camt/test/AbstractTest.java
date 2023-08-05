package ch.eugster.filemaker.fsl.camt.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.eugster.filemaker.fsl.camt.Camt;

public abstract class AbstractTest
{
	protected static ObjectMapper mapper = new ObjectMapper();

	protected Camt camt;
	
	@BeforeEach
	protected void before() throws Exception
	{
		camt = new Camt();
	}
	
	@AfterEach
	protected void after() throws Exception
	{
		camt = null;
	}
	
}
