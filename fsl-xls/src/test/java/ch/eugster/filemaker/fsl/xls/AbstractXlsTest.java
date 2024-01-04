package ch.eugster.filemaker.fsl.xls;

import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractXlsTest
{
	protected ObjectMapper mapper = new ObjectMapper();
	
	protected final String WORKBOOK_1 = "./src/test/results/workbook1.xlsx";
	
	protected final String SHEET0 = "Sheet0";

	protected Xls xls;
	
	@BeforeEach
	protected void beforeEach() throws Exception
	{
		if (Objects.isNull(xls))
		{
			xls = new Xls();
		}
	}
	
	@AfterEach
	protected void afterEach() throws Exception
	{
		Xls.activeWorkbook = null;
	}
}
