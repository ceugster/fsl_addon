package ch.eugster.filemaker.fsl.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFHeaderFooterProperties;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * @author christian
 *
 * @created 2023-07-24
 * 
 * @updated 
 * 
 * The public methods have to follow this convention:
 * 
 * - There is always one parameter of type string. This string must be a valid json object, that is a conversion to a jackson object node must be successfully done. The json object can contain zero or more attributes of valid json types, depending on the method called (see method descriptions).
 * - There is always on return parameter of type string, This string too must be a valid json object as above. The json attribute 'status' is mandatory and contains either 'OK' or 'Fehler', depending on the result of the method. Occuring errors are documented in an array object named 'errors'. Depending on the method json attributes with information are returned. Valid attribute names are documented at the method.
 * 
 * There is a set of controlled attributes, that are recognized valid. @see ch.eugster.filemaker.fsl.xls.Key
 */
public class Xls extends Executor
{
	public static Workbook activeWorkbook;

//	private static Logger log = LoggerFactory.getLogger(Xls.class);

	/**
	 * Set active sheet
	 * 
	 * @param sheet name (string) or index (integer)
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String activateSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doActivateSheet();
			}
		}
		return getResponse();
	}
	
	/**
	 * Return active sheet name and index
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String activeSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doGetActiveSheet();
			}
		}
		return getResponse();
	}

	/**
	 * Return active sheet name and index
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String activeSheetPresent(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doActiveSheetPresent();
			}
		}
		return getResponse();
	}
	
	/**
	 * Apply cell styles
	 * 
	 * @see MergedCellStyles.class for applyable styles
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String applyCellStyles(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doApplyCellStyles();
			}
		}
		return getResponse();
	}
	
	public static String applyFontStyles(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doApplyFontStyles();
			}
		}
		return getResponse();
	}
	
	public static String autoSizeColumns(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doAutoSizeColumns();
			}
		}
		return getResponse();
	}
	
	public static String copyCells(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doCopyCells();
			}
		}
		return getResponse();
	}
	
	/**
	 * Create sheet
	 * 
	 * @param sheet name (optional)
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String createSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doCreateSheet();
			}
		}
		return getResponse();
	}
	
	/**
	 * Creates a workbook
	 * 
	 * @param workbook name
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String createWorkbook(String request)
	{
		if (createRequestNode(request))
		{
			doCreateWorkbook();
		}
		return getResponse();
	}
	
	/**
	 * Create a workbook with initial sheet
	 * 
	 * @param sheet name (optional)
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String createWorkbookWithSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (doCreateWorkbook())
			{
				doCreateSheet();
			}
		}
		return getResponse();
	}
	
//	/**
//	 * Create and save a workbook with initial sheet
//	 * 
//	 * @param sheet name (optional)
//	 * @param path
//	 * 
//	 * @return status 'OK' or 'Fehler'
//	 * @return optional 'errors' array of error messages
//	 * 
//	 */
//	public static String createAndSaveWorkbookWithSheet(String request)
//	{
//		if (createRequestNode(request))
//		{
//			if (doCreateWorkbook())
//			{
//				if (doCreateSheet())
//				{
//					doSaveWorkbook();
//					doReleaseWorkbook();
//				}
//			}
//		}
//		return getResponse();
//	}
//	
	/**
	 * Drop sheet
	 * 
	 * @param sheet name (string) or index (integer)
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String dropSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doDropSheet();
			}
		}
		return getResponse();
	}
	
	/**
	 * Returns an string array of all callable methods
	 * 
	 * @param getRequestNode()  empty
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return methods string array of method names
	 * @return optional errors containing error messages
	 * 
	 */
	public static String getCallableMethods(String request)
	{
		ArrayNode callableMethods = getResponseNode().arrayNode();
		Method[] methods = Xls.class.getDeclaredMethods();
		for (Method method : methods)
		{
			if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers()))
			{
				Parameter[] parameters = method.getParameters();
				if (parameters.length == 1 && parameters[0].getType().equals(String.class))
				{
					callableMethods.add(method.getName());
				}
			}
		}
		getResponseNode().set(Executor.RESULT, callableMethods);
		return getResponse();
	}
	
	public static String getSupportedFunctionNames(String request)
	{
		ArrayNode arrayNode = getResponseNode().arrayNode();
		Collection<String> supportedFunctionNames = FunctionEval.getSupportedFunctionNames();
		for (String supportedFunctionName : supportedFunctionNames)
		{
			arrayNode.add(supportedFunctionName);
		}
		getResponseNode().set(Executor.RESULT, arrayNode);
		return getResponse();
	}

	/**
	 * Rename existing sheet
	 * 
	 * @param index (integer)
	 * @param sheet (string) new sheet name
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String moveSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doMoveSheet();
			}
		}
		return getResponse();
	}
	
	/**
	 * Release current workbook
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String releaseWorkbook(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doReleaseWorkbook();
			}
		}
		return getResponse();
	}

	/**
	 * Rename existing sheet
	 * 
	 * @param index (integer)
	 * @param sheet (string) new sheet name
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String renameSheet(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doRenameSheet();
			}
		}
		return getResponse();
	}

	public static String rotateCells(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doRotateCells();
			}
		}
		return getResponse();
	}
	
	/**
	 * Save and release current workbook
	 * 
	 * @param path where to save the workbook
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String saveAndReleaseWorkbook(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				if (doSaveWorkbook())
				{
					doReleaseWorkbook();
				}
			}
		}
		return getResponse();
	}
	
	/**
	 * Save current workbook
	 * 
	 * @param path where to save the workbook
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String saveWorkbook(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSaveWorkbook();
			}
		}
		return getResponse();
	}

	public static String setCell(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSetCell();
			}
		}
		return getResponse();
	}
	
	public static String setCells(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSetCells();
			}
		}
		return getResponse();
	}
	
	public static String setFooters(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSetFooters();
			}
		}
		return getResponse();
	}

	public static String setHeaders(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSetHeaders();
			}
		}
		return getResponse();
	}
	
	public static String setPrintSetup(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doSetPrintSetup();
			}
		}
		return getResponse();
	}

	/**
	 * Return list of sheet names by index order
	 * 
	 * @return status 'OK' or 'Fehler'
	 * @return optional 'errors' array of error messages
	 * 
	 */
	public static String sheetNames(String request)
	{
		if (createRequestNode(request))
		{
			if (workbookPresent())
			{
				doGetSheetNames();
			}
		}
		return getResponse();
	}
	
	public static String activeWorkbookPresent(String request)
	{
		if (createRequestNode(request))
		{
			getResponseNode().put(Executor.RESULT, Boolean.valueOf(Objects.nonNull(Xls.activeWorkbook)).toString());
		}
		return getResponse();
	}
	
	private static void copyCell(Cell sourceCell, Cell targetCell)
	{
		CellType cellType = sourceCell.getCellType();
		switch (cellType)
		{
			case BLANK:
				break;
			case _NONE:
				break;
			case FORMULA:
			{
				String formula = sourceCell.getCellFormula();
				CellAddress sourceCellAddress = new CellAddress(sourceCell);
				CellAddress targetCellAddress = new CellAddress(targetCell);
				int rowDiff = targetCellAddress.getRow() - sourceCellAddress.getRow();
				int colDiff = targetCellAddress.getColumn() - sourceCellAddress.getColumn();
				formula = copyFormula(sourceCell.getRow().getSheet(), formula, rowDiff, colDiff);
				targetCell.setCellFormula(formula);
				break;
			}
			default:
			{
				CellUtil.copyCell(sourceCell, targetCell, null, null);
				break;
			}
		}
	}

	private static String copyFormula(Sheet sheet, String formula, int rowDiff, int colDiff)
	{
		FormulaParsingWorkbook workbookWrapper = getFormulaParsingWorkbook(sheet);
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		for (int i = 0; i < ptgs.length; i++)
		{
			if (ptgs[i] instanceof RefPtgBase)
			{ 
				// base class for cell references
				RefPtgBase ref = (RefPtgBase) ptgs[i];
				if (ref.isRowRelative())
				{
					ref.setRow(ref.getRow() + rowDiff);
				}
				if (ref.isColRelative())
				{
					ref.setColumn(ref.getColumn() + colDiff);
				}
			}
			else if (ptgs[i] instanceof AreaPtgBase)
			{ 
				// base class for range references
				AreaPtgBase ref = (AreaPtgBase) ptgs[i];
				if (ref.isFirstColRelative())
				{
					ref.setFirstColumn(ref.getFirstColumn() + colDiff);
				}
				if (ref.isLastColRelative())
				{
					ref.setLastColumn(ref.getLastColumn() + colDiff);
				}
				if (ref.isFirstRowRelative())
				{
					ref.setFirstRow(ref.getFirstRow() + rowDiff);
				}
				if (ref.isLastRowRelative())
				{
					ref.setLastRow(ref.getLastRow() + rowDiff);
				}
			}
		}

		formula = FormulaRenderer.toFormulaString(getFormulaRenderingWorkbook(sheet), ptgs);
		return formula;
	}
	
	private static boolean doActivateSheet()
	{
		boolean result = true;
		JsonNode sheetNode = getRequestNode().findPath(Key.SHEET.key());
		if (sheetNode.isTextual())
		{
			Sheet sheet = activeWorkbook.getSheet(sheetNode.asText());
			if (Objects.nonNull(sheet))
			{
				if (activeWorkbook.getActiveSheetIndex() != activeWorkbook.getSheetIndex(sheet))
				{
					activeWorkbook.setActiveSheet(activeWorkbook.getSheetIndex(sheet));
				}
			}
			else
			{
				result = addErrorMessage("Sheet with name '" + sheetNode.asText() + "' does not exist");
			}
		}
		else if (sheetNode.isMissingNode())
		{
			JsonNode indexNode = getRequestNode().findPath(Key.INDEX.key());
			if (indexNode.isInt())
			{
				if (activeWorkbook.getNumberOfSheets() > indexNode.asInt())
				{
					if (activeWorkbook.getActiveSheetIndex() != indexNode.asInt())
					{
						activeWorkbook.setActiveSheet(indexNode.asInt());
					}
				}
				else
				{
					result = addErrorMessage("Sheet with " + Key.INDEX.key() + " " + indexNode.asInt() + " does not exist");
				}
			}
			else if (indexNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.SHEET.key() + "' or '" + Key.INDEX.key() + "'");
			}
			else
			{
				result = addErrorMessage("Illegal argument '" + Key.INDEX.key() + "'");
			}
		}
		else
		{
			result = addErrorMessage("Illegal argument '" + Key.SHEET.key() + "'");
		}
		if (result)
		{
			getResponseNode().put(Key.INDEX.key(), activeWorkbook.getActiveSheetIndex());
			getResponseNode().put(Key.SHEET.key(), activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex()).getSheetName());
		}
		return result;
	}
	
	private static boolean doActiveSheetPresent()
	{
		boolean result = true;
		try
		{
			Sheet sheet = activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex());
			result = Objects.nonNull(sheet);
			getResponseNode().put(Executor.RESULT, Boolean.valueOf(result).toString());
			if (result)
			{
				getResponseNode().put(Key.INDEX.key(), activeWorkbook.getActiveSheetIndex());
				getResponseNode().put(Key.SHEET.key(), sheet.getSheetName());
			}
		}
		catch (IllegalArgumentException e)
		{
			result = false;
			getResponseNode().put(Executor.RESULT, Boolean.valueOf(result).toString());
		}
		return result;
	}
	
	private static boolean doApplyCellStyles()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			if (!cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode);
				cellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
			}
			else
			{
				JsonNode rangeNode = getRequestNode().findPath(Key.RANGE.key());
				cellRangeAddress = getCellRangeAddress(rangeNode);
			}
			if (Objects.nonNull(cellRangeAddress))
			{
				Iterator<CellAddress> cellAddresses = cellRangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Cell cell = getOrCreateCell(sheet, cellAddress);
					if (Objects.nonNull(cell))
					{
						MergedCellStyle m = new MergedCellStyle(cell.getCellStyle());
						result = m.applyRequestedStyles(getRequestNode(), getResponseNode());
						if (result)
						{
							CellStyle cellStyle = getCellStyle(sheet, m);
							cell.setCellStyle(cellStyle);
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return result;
	}

	private static boolean doApplyFontStyles()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			if (!cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode);
				cellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
			}
			else
			{
				JsonNode rangeNode = getRequestNode().findPath(Key.RANGE.key());
				cellRangeAddress = getCellRangeAddress(rangeNode);
			}
			if (Objects.nonNull(cellRangeAddress))
			{
				Iterator<CellAddress> cellAddresses = cellRangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Row row = sheet.getRow(cellAddress.getRow());
					if (Objects.nonNull(row))
					{
						Cell cell = row.getCell(cellAddress.getColumn());
						CellStyle cellStyle = cell.getCellStyle();
						MergedCellStyle mcs = new MergedCellStyle(cellStyle);
						int fontIndex = cellStyle.getFontIndex();
						Font font = sheet.getWorkbook().getFontAt(fontIndex);
						MergedFont m = new MergedFont(font);
						if (m.applyRequestedFontStyles(getRequestNode(), getResponseNode()))
						{
							font = getFont(sheet, m);
							if (font.getIndex() != fontIndex)
							{
								mcs.setFontIndex(font.getIndex());
								cellStyle = getCellStyle(sheet, mcs);
								cell.setCellStyle(cellStyle);
							}
						}
						else
						{
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
		}
		return result;
	}

	private static boolean doAutoSizeColumns()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			if (cellNode.isMissingNode())
			{
				JsonNode rangeNode = getRequestNode().findPath(Key.RANGE.key());
				if (!rangeNode.isMissingNode())
				{
					cellRangeAddress = getCellRangeAddress(rangeNode);
				}
			}
			else
			{
				CellAddress cellAddress = getCellAddress(cellNode);
				cellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getRow(), cellAddress.getColumn());
			}
			if (Objects.nonNull(cellRangeAddress))
			{
				int leftCol = cellRangeAddress.getFirstColumn();
				int rightCol = cellRangeAddress.getLastColumn();
				for (int colIndex = leftCol; colIndex <= rightCol; colIndex++)
				{
					sheet.autoSizeColumn(colIndex);
				}
			}
		}
		return result;
	}
	
	private static boolean doCopyCells()
	{
		boolean result = true;
		Sheet sourceSheet = getSheet();
		Sheet targetSheet = sourceSheet;
		CellRangeAddress sourceCellRangeAddress = null;
		CellRangeAddress targetCellRangeAddress = null;
		JsonNode sourceNode = getRequestNode().findPath(Key.SOURCE.key());
		if (sourceNode.isTextual())
		{
			sourceCellRangeAddress = getCellRangeAddress(sourceNode);
			if (Objects.nonNull(sourceCellRangeAddress))
			{
				JsonNode targetNode = getRequestNode().findPath(Key.TARGET.key());
				if (targetNode.isTextual())
				{
					targetCellRangeAddress = getCellRangeAddress(targetNode);
					if (Objects.isNull(targetCellRangeAddress))
					{
						result = addErrorMessage("Illegal argument '" + targetNode.asText() + "'");
					}
				}
				else if (targetNode.isObject())
				{
					targetSheet = getSheet();
					targetCellRangeAddress = getCellRangeAddress(targetNode);
				}
				else
				{
					result = addErrorMessage("Illegal argument '" + Key.TARGET.key() + "'");
				}
			}
			else
			{
				result = addErrorMessage("Illegal argument '" + sourceNode.asText() + "'");
			}
		}
		else if (sourceNode.isObject())
		{
			sourceSheet = getSheet();
			sourceCellRangeAddress = getCellRangeAddress(sourceNode);
			JsonNode targetNode = getRequestNode().findPath(Key.TARGET.key());
			if (targetNode.isTextual())
			{
				targetCellRangeAddress = getCellRangeAddress(targetNode);
				if (Objects.isNull(targetCellRangeAddress))
				{
					result = addErrorMessage("Illegal argument '" + targetNode.asText() + "'");
				}
			}
			else if (targetNode.isObject())
			{
				targetSheet = getSheet();
				targetCellRangeAddress = getCellRangeAddress(targetNode);
			}
			else
			{
				result = addErrorMessage("Illegal argument '" + Key.TARGET.key() + "'");
			}
		}
		else
		{
			result = addErrorMessage("Illegal argument '" + Key.SOURCE.key() + "'");
		}
		if (Objects.nonNull(sourceCellRangeAddress) && Objects.nonNull(targetCellRangeAddress))
		{
			if (sourceSheet == targetSheet)
			{
				if (sourceCellRangeAddress.intersects(targetCellRangeAddress))
				{
					result = addErrorMessage("Source range and target range must not intersect");
				}
			}
			if (result)
			{
				if (sourceCellRangeAddress.getNumberOfCells() == 1)
				{
					Row sourceRow = sourceSheet.getRow(sourceCellRangeAddress.getFirstRow());
					if (Objects.nonNull(sourceRow))
					{
						Cell sourceCell = sourceRow.getCell(sourceCellRangeAddress.getFirstColumn());
						if (Objects.nonNull(sourceCell))
						{
							Iterator<CellAddress> targetAddresses = targetCellRangeAddress.iterator();
							while (targetAddresses.hasNext())
							{
								CellAddress sourceAddress = new CellAddress(sourceCell);
								CellAddress targetAddress = targetAddresses.next();
								int rowDiff = targetAddress.getRow() - sourceAddress.getRow();
								int colDiff = targetAddress.getColumn() - sourceAddress.getColumn();
								if (sourceCell.getCellType().equals(CellType.FORMULA))
								{
									String copiedFormula = copyFormula(sourceSheet, sourceCell.getCellFormula(),
											rowDiff, colDiff);
									Cell targetCell = getOrCreateCell(targetSheet, targetAddress);
									targetCell.setCellFormula(copiedFormula);
								}
								else
								{
									int targetTop = targetCellRangeAddress.getFirstRow();
									int targetBottom = targetCellRangeAddress.getLastRow();
									int targetLeft = targetCellRangeAddress.getFirstColumn();
									int targetRight = targetCellRangeAddress.getLastColumn();
									for (int r = targetTop; r <= targetBottom; r++)
									{
										Row targetRow = getOrCreateRow(targetSheet, r);
										for (int cell = targetLeft; cell <= targetRight; cell++)
										{
											Cell targetCell = getOrCreateCell(targetRow, cell);
											if (sourceCell.getCellType().equals(CellType.STRING))
											{
												targetCell.setCellValue(sourceCell.getRichStringCellValue());

											}
											else if (sourceCell.getCellType().equals(CellType.NUMERIC))
											{
												targetCell.setCellValue(sourceCell.getNumericCellValue());
											}
										}
									}
								}
							}
						}
					}
				}
				else if (sourceCellRangeAddress.getNumberOfCells() == targetCellRangeAddress.getNumberOfCells()
						&& sourceCellRangeAddress.getLastRow()
								- sourceCellRangeAddress.getFirstRow() == targetCellRangeAddress.getLastRow()
										- targetCellRangeAddress.getFirstRow())
				{
					Iterator<CellAddress> sourceAddresses = sourceCellRangeAddress.iterator();
					Iterator<CellAddress> targetAddresses = targetCellRangeAddress.iterator();
					while (sourceAddresses.hasNext())
					{
						CellAddress sourceAddress = sourceAddresses.next();
						Row sourceRow = sourceSheet.getRow(sourceAddress.getRow());
						if (Objects.nonNull(sourceRow))
						{
							Cell sourceCell = sourceRow.getCell(sourceAddress.getColumn());
							if (Objects.nonNull(sourceCell))
							{
								CellAddress targetAddress = targetAddresses.next();
								Row targetRow = getOrCreateRow(targetSheet, targetAddress.getRow());
								Cell targetCell = getOrCreateCell(targetRow, targetAddress.getColumn());
								copyCell(sourceCell, targetCell);
							}
						}
					}
				}
				else
				{
					result = addErrorMessage("Source and target range dimensions must not differ");
				}
			}
			else
			{
				result = addErrorMessage("Missing argument 'sheet' for source");
			}
		}
		else
		{
			result = addErrorMessage("Missing argument 'target'");
		}
		return result;
	}
	
	private static boolean doCreateSheet()
	{
		boolean result = false;
		Sheet sheet = null;
		JsonNode sheetNode = getRequestNode().findPath(Key.SHEET.key());
		if (sheetNode.isMissingNode())
		{
			sheet = activeWorkbook.createSheet();
			result = true;
		}
		else if (sheetNode.isTextual())
		{
			try
			{
				sheet = activeWorkbook.createSheet(sheetNode.asText());
				result = true;
			}
			catch (IllegalArgumentException e)
			{
				result = addErrorMessage("Illegal argument 'sheet' ('" + sheetNode.asText() + "' already exists)");
			}
		}
		if (result)
		{
			getResponseNode().put(Key.SHEET.key(), sheet.getSheetName());
			getResponseNode().put(Key.INDEX.key(), activeWorkbook.getSheetIndex(sheet));
		}
		return result;
	}

	private static boolean doCreateWorkbook()
	{
		boolean result = true;
		Type type = null;
		JsonNode typeNode = getRequestNode().findPath(Key.TYPE.key());
		if (typeNode.isTextual())
		{
			type = Type.findByExtension(typeNode.asText());
		}
		else if (typeNode.isMissingNode())
		{
			type = Type.XLSX;
		}
		if (Objects.nonNull(type))
		{
			switch (type)
			{
				case XLSX:
				{
					activeWorkbook = new XSSFWorkbook();
					break;
				}
				case XLS:
				{
					activeWorkbook = new HSSFWorkbook();
					break;
				}
			}
		}
		else
		{
			result = addErrorMessage("Illegal workbook type '" + typeNode.asText() + "'.");
		}
		return result;
	}

	private static boolean doDropSheet()
	{
		boolean result = true;
		JsonNode sheetNode = getRequestNode().findPath(Key.SHEET.key());
		if (sheetNode.isTextual())
		{
			Sheet sheet = activeWorkbook.getSheet(sheetNode.asText());
			if (Objects.nonNull(sheet))
			{
				activeWorkbook.removeSheetAt(activeWorkbook.getSheetIndex(sheet));
			}
			else
			{
				result = addErrorMessage("Sheet with name '" + sheetNode.asText() + "' does not exist");
			}
		}
		else if (sheetNode.isMissingNode())
		{
			JsonNode indexNode = getRequestNode().findPath(Key.INDEX.key());
			if (indexNode.isInt())
			{
				if (activeWorkbook.getActiveSheetIndex() > -1)
				{
					activeWorkbook.removeSheetAt(indexNode.asInt());
				}
				else
				{
					result = addErrorMessage("Sheet with index '" + indexNode.asInt() + "' does not exist");
				}
			}
			else if (indexNode.isMissingNode())
			{
				if (activeWorkbook.getNumberOfSheets() > 0)
				{
					if (activeWorkbook.getActiveSheetIndex() > -1)
					{
						activeWorkbook.removeSheetAt(activeWorkbook.getActiveSheetIndex());
					}
				}
				else
				{
					result = addErrorMessage("There is no active sheet present");
				}
			}
		}
		return result;
	}
	
	private static boolean doGetActiveSheet()
	{
		boolean result = true;
		Sheet sheet = activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex());
		if (Objects.nonNull(sheet))
		{
			getResponseNode().put(Key.INDEX.key(), activeWorkbook.getSheetIndex(sheet));
			getResponseNode().put(Key.SHEET.key(), sheet.getSheetName());
		}
		else
		{
			result = addErrorMessage("There is no active sheet present");
		}
		return result;
	}

	private static boolean doGetSheetNames()
	{
		boolean result = true;
		ArrayNode sheetsNode = getResponseNode().arrayNode();
		ArrayNode indexNode = getResponseNode().arrayNode();
		int numberOfSheets = activeWorkbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++)
		{
			sheetsNode.add(activeWorkbook.getSheetAt(i).getSheetName());
			indexNode.add(i);
		}
		getResponseNode().set(Key.SHEET.key(), sheetsNode);
		getResponseNode().set(Key.INDEX.key(), indexNode);
		return result;
	}

	private static boolean doMoveSheet()
	{
		boolean result = true;
		Sheet sheet = activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex());
		JsonNode sourceNode = getRequestNode().findPath(Key.SOURCE.key());
		if (sourceNode.isTextual())
		{
			sheet = activeWorkbook.getSheet(sourceNode.asText());
		}
		else if (sourceNode.isInt())
		{
			sheet = activeWorkbook.getSheetAt(sourceNode.asInt());
		}
		else if (!sourceNode.isMissingNode())
		{
			result = addErrorMessage("Illegal argument '" + Key.SOURCE.key() + "'");
		}
		if (result)
		{
			JsonNode targetNode = getRequestNode().findPath(Key.TARGET.key());
			if (targetNode.isInt())
			{
				if (activeWorkbook.getNumberOfSheets() >= targetNode.asInt())
				{
					if (targetNode.asInt() < 0)
					{
						result = addErrorMessage("Illegal argument '" + Key.TARGET.key() + "' (sheet index is out of range: " + targetNode.asInt() + ")");
					}
						
					if (activeWorkbook.getActiveSheetIndex() != targetNode.asInt())
					{
						activeWorkbook.setSheetOrder(sheet.getSheetName(), targetNode.asInt());
					}
				}
				else
				{
					result = addErrorMessage("Illegal argument '" + Key.TARGET.key() + "' (sheet index is out of range: " + targetNode.asInt() + " > " + activeWorkbook.getNumberOfSheets() + ")");
				}
			}
			else if (targetNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.TARGET.key() + "'");
			}
		}
		return result;
	}

	private static boolean doReleaseWorkbook()
	{
		try 
		{
			activeWorkbook.close();
			activeWorkbook = null;
			return true;
		}
		catch (IOException e)
		{
			addErrorMessage("Cannot not close workbook.");
			return false;
		}
	}
	
	private static boolean doRenameSheet()
	{
		boolean result = true;
		JsonNode sheetNode = getRequestNode().findPath(Key.SHEET.key());
		if (sheetNode.isTextual())
		{
			int index = activeWorkbook.getActiveSheetIndex();
			JsonNode indexNode = getRequestNode().findPath(Key.INDEX.key());
			if (indexNode.isInt())
			{
				index = indexNode.asInt();
			}
			else if (!indexNode.isMissingNode())
			{
				result = addErrorMessage("Illegal argument '" + Key.INDEX.key() + "'");
			}
			if (result)
			{
				activeWorkbook.setSheetName(index, sheetNode.asText());
			}
		}
		else if (sheetNode.isMissingNode())
		{
			result = addErrorMessage("Missing argument '" + Key.SHEET.key() + "'");
		}
		else
		{
			result = addErrorMessage("Illegal argument '" + Key.SHEET.key() + "'");
		}
		return result;
	}
	
	private static boolean doRotateCells()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			if (!cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode);
				cellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
			}
			else
			{
				JsonNode rangeNode = getRequestNode().findPath(Key.RANGE.key());
				cellRangeAddress = getCellRangeAddress(rangeNode);
			}
			if (Objects.nonNull(cellRangeAddress)) 
			{
				int rotation = Integer.MIN_VALUE;
				JsonNode rotationNode = getRequestNode().findPath(Key.ROTATION.key());
				if (rotationNode.isInt())
				{
					rotation = IntNode.class.cast(getRequestNode().get(Key.ROTATION.key())).asInt();
				}
				else if (rotationNode.isMissingNode())
				{
					result = addErrorMessage("Missing argument '" + Key.ROTATION.key() + "'");
				}
				else
				{
					result = addErrorMessage("Illegal argument '" + Key.ROTATION.key() + "'");
				}
				if (rotation != Integer.MIN_VALUE)
				{
					Iterator<CellAddress> cellAddresses = cellRangeAddress.iterator();
					while (cellAddresses.hasNext())
					{
						CellAddress cellAddress = cellAddresses.next();
						Row row = sheet.getRow(cellAddress.getRow());
						if (Objects.nonNull(row))
						{
							Cell cell = row.getCell(cellAddress.getColumn());
							if (Objects.nonNull(cell))
							{
								CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
								cellStyle.setRotation((short) rotation);
								cell.setCellStyle(cellStyle);
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private static boolean doSaveWorkbook()
	{
		boolean result = true;
		JsonNode pathNode = getRequestNode().findPath(Key.PATH.key());
		if (pathNode.isTextual())
		{
			String pathname = replaceExtension();
			try (OutputStream os = new FileOutputStream(new File(pathname)))
			{
				activeWorkbook.write(os);
				os.close();
			}
			catch (Exception e)
			{
				result = addErrorMessage("Saving workbook failed (" + e.getLocalizedMessage() + ")");
			}
		}
		else if (pathNode.isMissingNode())
		{
			result = addErrorMessage("Missing argument '" + Key.PATH.key() + "'");
		}
		else
		{
			result = addErrorMessage("Illegal argument '" + Key.PATH.key() + "'");
		}
		return result;
	}

	private static String replaceExtension()
	{
		String pathname = getRequestNode().get(Key.PATH.key()).asText();
		if (XSSFWorkbook.class.isInstance(activeWorkbook))
		{
			if (!pathname.endsWith(".xlsx"))
			{
				if (pathname.endsWith(".xls"))
				{
					pathname = pathname.substring(0, pathname.lastIndexOf(".xls") - 1) + ".xlsx";
				}
				else
				{
					pathname = pathname + ".xlsx";
				}
			}
		}
		if (HSSFWorkbook.class.isInstance(activeWorkbook))
		{
			if (!pathname.endsWith(".xls"))
			{
				if (pathname.endsWith(".xlsx"))
				{
					pathname = pathname.substring(0, pathname.lastIndexOf(".xlsx") - 1) + ".xls";
				}
				else
				{
					pathname = pathname + ".xls";
				}
			}
		}
		return pathname;
	}
	
	private static boolean doSetCell(Sheet sheet, CellAddress cellAddress, JsonNode valueNode)
	{
		boolean result = true;
		Cell cell = getOrCreateCell(sheet, cellAddress);
		if (valueNode.isNumber())
		{
			if (valueNode.isInt())
			{
				cell.setCellValue(valueNode.asInt());
			}
			else if (valueNode.isLong())
			{
				cell.setCellValue(valueNode.asLong());
			}
			else if (valueNode.isDouble())
			{
				cell.setCellValue(valueNode.asDouble());
			}
		}
		else if (valueNode.isTextual())
		{
			if (!valueNode.asText().trim().isEmpty())
			{
				try
				{
					cell.setCellValue(DateUtil.parseDateTime(valueNode.asText()));
					MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
					int formatIndex = BuiltinFormats.getBuiltinFormat("M/d/yy");
					mcs.setDataFormat((short) formatIndex);
					CellStyle cellStyle = getCellStyle(sheet, mcs);
					cell.setCellStyle(cellStyle);
				}
				catch (Exception tpe)
				{
					try
					{
						Date date = DateFormat.getDateInstance().parse(valueNode.asText());
						cell.setCellValue(DateUtil.getExcelDate(date));
						MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
						int formatIndex = BuiltinFormats.getBuiltinFormat("M/d/yy");
						mcs.setDataFormat((short) formatIndex);
						CellStyle cellStyle = getCellStyle(sheet, mcs);
						cell.setCellStyle(cellStyle);
					}
					catch (ParseException dpe)
					{
						try
						{
							double time = DateUtil.convertTime(valueNode.asText());
							cell.setCellValue(time);
							MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
							int formatIndex = BuiltinFormats.getBuiltinFormat("h:mm");
							mcs.setDataFormat((short) formatIndex);
							CellStyle cellStyle = getCellStyle(sheet, mcs);
							cell.setCellStyle(cellStyle);
						}
						catch (Exception e)
						{
							try
							{
								cell.setCellFormula(valueNode.asText());
								FormulaEvaluator evaluator = activeWorkbook.getCreationHelper()
										.createFormulaEvaluator();
								CellType cellType = evaluator.evaluateFormulaCell(cell);
								System.out.println(cellType);
							}
							catch (FormulaParseException fpe)
							{
								setRichTextString(cell, valueNode.asText());
							}
						}
					}
				}
			}
		}
		else if (valueNode.isBoolean())
		{
			cell.setCellValue(valueNode.asBoolean());
		}
		else
		{
			addErrorMessage("Der Wertetyp '" + valueNode.asText() + "' wird nicht unterstützt.");
		}
		return result;
	}

	private static boolean doSetCell()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		result = Objects.nonNull(sheet);
		if (result)
		{
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			JsonNode valueNode = getRequestNode().findPath(Key.VALUE.key());
			if (cellNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.CELL.key() + "'");
			}
			else if (valueNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.VALUE.key() + "'");
			}
			else if (valueNode.isArray())
			{
				result = addErrorMessage("Argument '" + Key.VALUE.key() + "' must have a single value.");
			}
			else if (valueNode.isValueNode())
			{
				ValueNode value = ValueNode.class.cast(valueNode);
				if (cellNode.isValueNode())
				{
					CellAddress cellAddress = getCellAddress(cellNode);
					result = doSetCell(sheet, cellAddress, value);
				}
				else
				{
					result = addErrorMessage("Illegal argument '" + Key.CELL.key() + "'");
				}
			}
			else
			{
				result = addErrorMessage("Illegal argument '" + Key.VALUE.key() + "'");
			}
		}
		else
		{
			result = addErrorMessage("Missing argument '" + Key.SHEET.key() + "'");
		}
		return result;
	}

	private static boolean doSetCells()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			JsonNode cellNode = getRequestNode().findPath(Key.CELL.key());
			JsonNode valuesNode = getRequestNode().findPath(Key.VALUES.key());
			if (cellNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.CELL.key() + "'");
			}
			else if (valuesNode.isMissingNode())
			{
				result = addErrorMessage("Missing argument '" + Key.VALUES.key() + "'");
			}
			else if (valuesNode.isObject())
			{
				result = addErrorMessage("Argument '" + Key.VALUES.key() + "' must be an array of values.");
			}
			else if (valuesNode.isArray())
			{
				ArrayNode valuesArrayNode = ArrayNode.class.cast(valuesNode);
				if (cellNode.isArray())
				{
					if (cellNode.size() == valuesArrayNode.size())
					{
						result = setCells(sheet, ArrayNode.class.cast(cellNode), valuesArrayNode);
					}
					else
					{
						result = addErrorMessage("Size of 'cell' array does not equal to size of 'values' array");
					}
				}
				else
				{
					Direction direction = Direction.DEFAULT;
					JsonNode directionNode = getRequestNode().findPath(Key.DIRECTION.key());
					if (directionNode.isTextual())
					{
						try
						{
							direction = Direction.valueOf(directionNode.asText().toUpperCase());
						}
						catch (Exception e)
						{
							result = addErrorMessage("Illegal argument 'direction'");
						}
					}
					else if (directionNode.isMissingNode())
					{
					}
					else
					{
						result = addErrorMessage("Illegal argument 'direction'");
					}
					if (result)
					{
						if (cellNode.isTextual())
						{
							doSetCells(sheet, TextNode.class.cast(cellNode), valuesArrayNode, direction);
						}
						else if (cellNode.isObject())
						{
							doSetCells(sheet, ObjectNode.class.cast(cellNode), valuesArrayNode, direction);
						}
						else
						{
							result = addErrorMessage("Illegal argument '" + Key.CELL.key() + "'");
						}
					}
				}
			}
			else
			{
				result = addErrorMessage("Illegal argument '" + Key.VALUES.key() + "'");
			}
		}
		else
		{
			result = addErrorMessage("Missing argument '" + Key.SHEET.key() + "'");
		}
		return result;
	}

	private static boolean doSetCells(Sheet sheet, CellAddress cellAddress, ArrayNode valuesNode, Direction direction)
	{
		boolean result = Objects.nonNull(cellAddress);
		if (result)
		{
			if (valuesNode.size() > 0)
			{
				if (direction.validRange(getResponseNode(), sheet.getWorkbook(), cellAddress, valuesNode.size()))
				{
					for (int i = 0; i < valuesNode.size(); i++)
					{
						JsonNode valueNode = valuesNode.get(i);
						result = doSetCell(sheet, cellAddress, valueNode);
						if (result)
						{
							cellAddress = direction.nextIndex(cellAddress);
						}
						else
						{
							break;
						}
					}
				}
			}
			else
			{
				result = addErrorMessage("Invalid_argument '" + Key.VALUES.key() + "'");
			}
		}
		return result;
	}
	
	private static boolean doSetCells(Sheet sheet, ObjectNode cellNode, ArrayNode valuesNode, Direction direction)
	{
		boolean result = true;
		try
		{
			CellAddress cellAddress = getCellAddress(cellNode);
			result = doSetCells(sheet, cellAddress, valuesNode, direction);
		}
		catch (Exception e)
		{
			result = addErrorMessage("Illegal argument '" + cellNode.asText() + "'");
		}
		return result;
	}
	
	private static boolean doSetCells(Sheet sheet, TextNode cellNode, ArrayNode valuesNode, Direction direction)
	{
		boolean result = true;
		try
		{
			CellAddress cellAddress = getCellAddress(cellNode);
			result = doSetCells(sheet, cellAddress, valuesNode, direction);
		}
		catch (Exception e)
		{
			result = addErrorMessage("Illegal argument '" + cellNode.asText() + "'");
		}
		return result;
	}
	
	private static boolean doSetFooters()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			Footer footer = sheet.getFooter();
			JsonNode leftNode = getRequestNode().findPath(Key.LEFT.key());
			if (leftNode.isTextual())
			{
				footer.setLeft(replaceTemplates(TextNode.class.cast(leftNode)));
			}
			JsonNode centerNode = getRequestNode().findPath(Key.CENTER.key());
			if (centerNode.isTextual())
			{
				footer.setCenter(replaceTemplates(TextNode.class.cast(centerNode)));
			}
			JsonNode rightNode = getRequestNode().findPath(Key.RIGHT.key());
			if (rightNode.isTextual())
			{
				footer.setRight(replaceTemplates(TextNode.class.cast(rightNode)));
			}
		}
		return result;
	}
	
	private static String replaceTemplates(TextNode node)
	{
		String text = node.asText();
		if (text.contains("%page"))
		{
			text.replace("%page", HeaderFooter.page());
		}
		if (text.contains("%numPages"))
		{
			text.replace("%numPages", HeaderFooter.numPages());
		}
		if (text.contains("%date"))
		{
			text.replace("%date", HeaderFooter.date());
		}
		if (text.contains("%time"))
		{
			text.replace("%time", HeaderFooter.time());
		}
		if (text.contains("%tab"))
		{
			text.replace("%tab", HeaderFooter.tab());
		}
		return text;
	}
	
	private static boolean doSetHeaders()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			Header header = null;
			XSSFHeaderFooterProperties props = null;
			JsonNode typeNode = getRequestNode().get(Key.TYPE.key());
			if (Objects.isNull(typeNode) || typeNode.isMissingNode() || HSSFSheet.class.isInstance(sheet) || SXSSFSheet.class.isInstance(sheet))
			{
				header = sheet.getHeader();
			}
			else if (typeNode.isTextual() && XSSFSheet.class.isInstance(sheet))
			{
				XSSFSheet xssfSheet = XSSFSheet.class.cast(sheet);
				props = xssfSheet.getHeaderFooterProperties();
				if (typeNode.asText().equals("first"))
				{
					header = xssfSheet.getFirstHeader();
				}
				else if (typeNode.asText().equals("even"))
				{
					header = xssfSheet.getEvenHeader();
				}
				else if (typeNode.asText().equals("odd"))
				{
					header = xssfSheet.getOddHeader();
				}
				else
				{
					addErrorMessage("Illegal type, only 'first', 'even' or 'odd' allowed.");
				}
			}
			if (Objects.nonNull(header))
			{
				JsonNode leftNode = getRequestNode().findPath(Key.LEFT.key());
				if (leftNode.isTextual())
				{
					header.setLeft(replaceTemplates(TextNode.class.cast(leftNode)));
					
				}
				JsonNode centerNode = getRequestNode().findPath(Key.CENTER.key());
				if (centerNode.isTextual())
				{
					header.setCenter(replaceTemplates(TextNode.class.cast(centerNode)));
				}
				JsonNode rightNode = getRequestNode().findPath(Key.RIGHT.key());
				if (rightNode.isTextual())
				{
					header.setRight(replaceTemplates(TextNode.class.cast(rightNode)));
				}
				if (Objects.nonNull(props))
				{
					props.setAlignWithMargins(true);
					props.setScaleWithDoc(true);
				}
			}
		}
		return result;
	}
	
	/**
	 * TODO add cell range to print
	 * @return
	 */
	private static boolean doSetPrintSetup()
	{
		boolean result = true;
		Sheet sheet = getSheet();
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode rangeNode = getRequestNode().get(Key.RANGE.key());
			if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
			{
				cellRangeAddress = getCellRangeAddress(rangeNode);
			}
			else
			{
				JsonNode cellNode = getRequestNode().get(Key.CELL.key());
				CellAddress cellAddress = getCellAddress(cellNode);
				if (Objects.nonNull(cellAddress))
				{
					cellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
				}
			}
			if (Objects.nonNull(cellRangeAddress))
			{
				sheet.getWorkbook().setPrintArea(sheet.getWorkbook().getSheetIndex(sheet), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn(), cellRangeAddress.getFirstRow(), cellRangeAddress.getLastRow());
			}

			PrintOrientation orientation = PrintOrientation.DEFAULT;
			JsonNode orientationNode = getRequestNode().findPath(Key.ORIENTATION.key());
			if (!orientationNode.isMissingNode())
			{
				if (orientationNode.isTextual())
				{
					try
					{
						orientation = PrintOrientation.valueOf(orientationNode.asText().toUpperCase());
					}
					catch (Exception e)
					{
						result = addErrorMessage("Illegal argument 'orientation'");
					}
				}
				switch (orientation)
				{
					case LANDSCAPE:
					{
						sheet.getPrintSetup().setLandscape(true);
						break;
					}
					case PORTRAIT:
					{
						sheet.getPrintSetup().setNoOrientation(false);
						sheet.getPrintSetup().setLandscape(false);
						break;
					}
					default:
					{
						sheet.getPrintSetup().setNoOrientation(true);
						break;
					}
				}
			}
			int copies = 1;
			JsonNode copiesNode = getRequestNode().findPath(Key.COPIES.key());
			if (!copiesNode.isMissingNode())
			{
				if (copiesNode.isInt())
				{
					copies = copiesNode.asInt();
				}
				if (copies > 0 && copies <= Short.MAX_VALUE)
				{
					sheet.getPrintSetup().setCopies((short) copies);
				}
			}
		}
		return result;
	}

	private static CellAddress getCellAddress(JsonNode cellNode)
	{
		CellAddress cellAddress = null;
		if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
		{
			if (cellNode.isTextual())
			{
				cellAddress = new CellAddress(cellNode.asText());
//				if (Objects.isNull(cellAddress))
//				{
//					addErrorMessage("Illegal argument '" + cellNode.asText() + "'");
//				}
			}
			else if (cellNode.isObject())
			{
				JsonNode rowNode = cellNode.findPath(Key.ROW.key());
				if (rowNode.isInt())
				{
					JsonNode columnNode = cellNode.findPath(Key.COL.key());
					if (columnNode.isInt())
					{
						cellAddress = new CellAddress(rowNode.asInt(), columnNode.asInt());
					}
					else if (columnNode.isMissingNode())
					{
						addErrorMessage("Missing argument '" + Key.COL.key() + "'");
					}
					else
					{
						addErrorMessage("Illegal argument '" + Key.COL.key() + "'");
					}
				}
				else if (rowNode.isMissingNode())
				{
					addErrorMessage("Missing argument '" + Key.ROW.key() + "'");
				}
				else
				{
					addErrorMessage("Illegal argument '" + Key.ROW.key() + "'");
				}
			}
			else
			{
				addErrorMessage("Illegal argument '" + Key.CELL.key() + "'");
			}
		}
		return cellAddress;
	}

	private static CellAddress getCellAddress(JsonNode cellNode, String key)
	{
		CellAddress cellAddress = null;
		if (cellNode.isTextual())
		{
			cellAddress = new CellAddress(cellNode.asText());
//			if (Objects.isNull(cellAddress))
//			{
//				addErrorMessage("Illegal argument '" + cellNode.asText() + "'");
//			}
		}
		else if (cellNode.isObject())
		{
			cellAddress = getCellAddress(ObjectNode.class.cast(cellNode));
		}
		else if (Objects.isNull(cellNode) || cellNode.isMissingNode())
		{
			addErrorMessage("Missing argument '" + key + "'");
		}
		else
		{
			addErrorMessage("Illegal argument '" + key + "'");
		}
		return cellAddress;
	}

	private static CellRangeAddress getCellRangeAddress(JsonNode rangeNode)
	{
		CellRangeAddress cellRangeAddress = null;
		if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
		{
			CellAddress topLeftAddress = null;
			CellAddress bottomRightAddress = null;
			if (rangeNode.isTextual())
			{
				String range = rangeNode.asText();
				String[] rangeParts = range.split(":");
				String topLeftCell = null;
				String bottomRightCell = null;
				if (rangeParts.length > 0)
				{
					topLeftCell = rangeParts[0];
					topLeftAddress = new CellAddress(topLeftCell);
					bottomRightAddress = new CellAddress(topLeftCell);
				}
				if (rangeParts.length == 2)
				{
					bottomRightCell = rangeParts[1];
					bottomRightAddress = new CellAddress(bottomRightCell);
				}
				if (Objects.nonNull(topLeftAddress) && Objects.nonNull(bottomRightAddress))
				{
					cellRangeAddress = new CellRangeAddress(topLeftAddress.getRow(), bottomRightAddress.getRow(), topLeftAddress.getColumn(), bottomRightAddress.getColumn());
				}
			}
			else if (rangeNode.isObject())
			{
				topLeftAddress = getCellAddress(rangeNode.findPath(Key.TOP_LEFT.key()));
				if (Objects.nonNull(topLeftAddress))
				{
					bottomRightAddress = getCellAddress(rangeNode.findPath(Key.BOTTOM_RIGHT.key()));
					if (Objects.nonNull(bottomRightAddress))
					{
						cellRangeAddress = new CellRangeAddress(topLeftAddress.getRow(), bottomRightAddress.getRow(), topLeftAddress.getColumn(), bottomRightAddress.getColumn());
					}
					else
					{
						JsonNode topNode = rangeNode.findPath(Key.TOP.key());
						if (topNode.isInt())
						{
							JsonNode leftNode = rangeNode.get(Key.LEFT.key());
							if (leftNode.isInt())
							{
								topLeftAddress = new CellAddress(topNode.asInt(), leftNode.asInt());
							}
							else
							{
								addErrorMessage("Illegal argument '" + Key.LEFT.key() + "'");
							}
						}
						else
						{
							addErrorMessage("Illegal argument '" + Key.TOP.key() + "'");
						}
					}
				}
				else
				{
					JsonNode topNode = rangeNode.findPath(Key.TOP.key());
					if (topNode.isInt())
					{
						JsonNode leftNode = rangeNode.get(Key.LEFT.key());
						if (leftNode.isInt())
						{
							topLeftAddress = new CellAddress(topNode.asInt(), leftNode.asInt());
							if (Objects.nonNull(topLeftAddress))
							{
								JsonNode bottomNode = rangeNode.findPath(Key.TOP.key());
								if (bottomNode.isInt())
								{
									JsonNode rightNode = rangeNode.get(Key.LEFT.key());
									if (rightNode.isInt())
									{
										bottomRightAddress = new CellAddress(bottomNode.asInt(), rightNode.asInt());
//										if (Objects.isNull(bottomRightAddress))
//										{
//											bottomRightAddress = getCellAddress(rangeNode.findPath(Key.BOTTOM_RIGHT.key()));
//											if (Objects.nonNull(bottomRightAddress))
//											{
//												cellRangeAddress = new CellRangeAddress(topLeftAddress.getRow(), bottomRightAddress.getRow(), topLeftAddress.getColumn(), bottomRightAddress.getColumn());
//											}
//											else
//											{
//												addErrorMessage("Illegal argument '" + Key.BOTTOM_RIGHT.key() + "'");
//											}
//										}
//										else
//										{
											cellRangeAddress = new CellRangeAddress(topLeftAddress.getRow(), bottomRightAddress.getRow(), topLeftAddress.getColumn(), bottomRightAddress.getColumn());
//										}
									}
									else
									{
										addErrorMessage("Illegal argument '" + Key.LEFT.key() + "'");
									}
								}
								else
								{
									addErrorMessage("Illegal argument '" + Key.TOP.key() + "'");
								}
							}
						}
						else
						{
							addErrorMessage("Illegal argument '" + Key.LEFT.key() + "'");
						}
					}
					else
					{
						addErrorMessage("Illegal argument '" + Key.TOP.key() + "'");
					}
				}
			}
			else
			{
				addErrorMessage("Illegal argument '" + Key.RANGE.key() + "'");
			}
		}
		return cellRangeAddress;
	}

	private static CellStyle getCellStyle(Sheet sheet, MergedCellStyle m)
	{
		CellStyle cellStyle = null;
		for (int i = 0; i < sheet.getWorkbook().getNumCellStyles(); i++)
		{
			CellStyle cs = sheet.getWorkbook().getCellStyleAt(i);
			if (cs.getAlignment().equals(m.getHalign()) && cs.getVerticalAlignment().equals(m.getValign())
					&& cs.getBorderBottom().equals(m.getBottom()) && cs.getBorderLeft().equals(m.getLeft())
					&& cs.getBorderRight().equals(m.getRight()) && cs.getBorderTop().equals(m.getTop())
					&& cs.getBottomBorderColor() == m.getbColor() && cs.getLeftBorderColor() == m.getlColor()
					&& cs.getRightBorderColor() == m.getrColor() && cs.getTopBorderColor() == m.gettColor()
					&& cs.getDataFormat() == m.getDataFormat() && cs.getFillBackgroundColor() == m.getBgColor()
					&& cs.getFillForegroundColor() == m.getFgColor() && cs.getFontIndex() == m.getFontIndex()
					&& cs.getFillPattern().equals(m.getFillPattern()) && cs.getShrinkToFit() == m.getShrinkToFit()
					&& cs.getWrapText() == m.getWrapText())
			{
				cellStyle = cs;
				break;
			}
		}
		if (Objects.isNull(cellStyle))
		{
			cellStyle = sheet.getWorkbook().createCellStyle();
			m.applyToCellStyle(sheet, cellStyle);
		}
		return cellStyle;
	}

	private static Font getFont(Sheet sheet, MergedFont m)
	{
		Font font = null;
		for (int i = 0; i < sheet.getWorkbook().getNumberOfFonts(); i++)
		{
			Font f = sheet.getWorkbook().getFontAt(i);
			if (f.getFontName().equals(m.getName()) && f.getFontHeightInPoints() == m.getSize()
					&& f.getBold() == m.getBold() && f.getItalic() == m.getItalic()
					&& f.getUnderline() == m.getUnderline() && f.getStrikeout() == m.getStrikeOut()
					&& f.getTypeOffset() == m.getTypeOffset() && f.getColor() == m.getColor())
			{
				font = f;
				break;
			}
		}
		if (Objects.isNull(font))
		{
			font = sheet.getWorkbook().createFont();
			font.setFontName(m.getName());
			font.setFontHeightInPoints(m.getSize().shortValue());
			font.setBold(m.getBold().booleanValue());
			font.setItalic(m.getItalic().booleanValue());
			font.setUnderline(m.getUnderline().byteValue());
			font.setStrikeout(m.getStrikeOut().booleanValue());
			font.setTypeOffset(m.getTypeOffset().shortValue());
			font.setColor(m.getColor().shortValue());
		}
		return font;
	}

	private static FormulaParsingWorkbook getFormulaParsingWorkbook(Sheet sheet)
	{
		FormulaParsingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}

	private static FormulaRenderingWorkbook getFormulaRenderingWorkbook(Sheet sheet)
	{
		FormulaRenderingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}
	
	private static Cell getOrCreateCell(Row row, int colIndex)
	{
		Cell cell = null;
		if (Objects.nonNull(row))
		{
			if (validateColIndex(colIndex))
			{
				cell = row.getCell(colIndex);
				if (Objects.isNull(cell))
				{
					cell = row.createCell(colIndex);
				}
			}
			else
			{
				addErrorMessage("Illegal cell index (" + colIndex + " > " + activeWorkbook.getSpreadsheetVersion().getLastColumnIndex() + ")");
			}
		}
		return cell;
	}

	private static Cell getOrCreateCell(Sheet sheet, CellAddress cellAddress)
	{
		Cell cell = null;
		if (Objects.nonNull(cellAddress))
		{
			Row row = getOrCreateRow(sheet, cellAddress.getRow());
			if (Objects.nonNull(row))
			{
				cell = getOrCreateCell(row, cellAddress.getColumn());
			}
		}
		return cell;
	}

	private static Row getOrCreateRow(Sheet sheet, int rowIndex)
	{
		Row row = null;
		if (validateRowIndex(rowIndex))
		{
			row = sheet.getRow(rowIndex);
			if (Objects.isNull(row))
			{
				row = sheet.createRow(rowIndex);
			}
		}
		else
		{
			addErrorMessage("Illegal row index (" + rowIndex + " > " + sheet.getWorkbook().getSpreadsheetVersion().getLastRowIndex() + ")");
		}
		return row;
	}

	private static Sheet getSheet()
	{
		Sheet sheet = null;
		try
		{
			sheet = activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex());
			
			JsonNode sheetNode = getRequestNode().findPath(Key.SHEET.key());
			if (sheetNode.isMissingNode())
			{
				JsonNode indexNode = getRequestNode().findPath(Key.INDEX.key());
				if (indexNode.isInt())
				{
					sheet = activeWorkbook.getSheetAt(indexNode.asInt());
					if (Objects.isNull(sheet))
					{
						addErrorMessage("Sheet with index '" + indexNode.asInt() + "' does not exist");
					}
				}
				else if (indexNode.isTextual())
				{
					sheet = activeWorkbook.getSheet(indexNode.asText());
					if (Objects.isNull(sheet))
					{
						addErrorMessage("Sheet with index '" + indexNode.asText() + "' does not exist");
					}
				}
				else if (!indexNode.isMissingNode())
				{
					addErrorMessage("Illegal argument '" + Key.INDEX.key() + "'");
				}
			}
			else if (sheetNode.isTextual())
			{
				sheet = activeWorkbook.getSheet(sheetNode.asText());
				if (Objects.isNull(sheet))
				{
					addErrorMessage("Sheet with name '" + sheetNode.asText() + "' does not exist");
				}
			}
			else if (sheetNode.isInt())
			{
				sheet = activeWorkbook.getSheetAt(sheetNode.asInt());
				if (Objects.isNull(sheet))
				{
					addErrorMessage("Sheet with index '" + sheetNode.asInt() + "' does not exist");
				}
			}
			else
			{
				addErrorMessage("Illegal argument '" + Key.SHEET.key() + "'");
			}
		}
		catch (IllegalArgumentException e)
		{
			sheet = null;
			addErrorMessage(e.getLocalizedMessage());
		}
		return sheet;
	}
	
	private static boolean setCells(Sheet sheet, ArrayNode cellNode, ArrayNode valuesNode)
	{
		boolean result = true;
		for (int i = 0; i < cellNode.size(); i++)
		{
			CellAddress cellAddress = getCellAddress(cellNode.get(i), Key.CELL.key());
			JsonNode valueNode = valuesNode.get(i);
			result = doSetCell(sheet, cellAddress, valueNode);

		}
		return result;
	}

	private static void setRichTextString(Cell cell, String value)
	{
		if (XSSFCell.class.isInstance(cell))
		{
			cell.setCellValue(new XSSFRichTextString(value));
		}
		else
		{
			cell.setCellValue(new HSSFRichTextString(value));
		}
	}
	
	private static boolean validateColIndex(int colIndex)
	{
		return colIndex > -1 && colIndex < activeWorkbook.getSpreadsheetVersion().getMaxColumns();
	}
	
	private static boolean validateRowIndex(int rowIndex)
	{
		return rowIndex > -1 && rowIndex < activeWorkbook.getSpreadsheetVersion().getMaxRows();
	}
	
	private static boolean workbookPresent()
	{
		boolean result = true;
		if (Objects.isNull(activeWorkbook))
		{
			result = addErrorMessage("Workbook missing (create workbook first)");
		}
		return result;
	}

//	private static boolean isFunctionSupported(String function)
//	{
//		int pos = function.indexOf("(");
//		if (pos > -1)
//		{
//			String name = function.substring(0, pos - 1);
//			FunctionNameEval functionEval = new FunctionNameEval(name);
//			System.out.println(functionEval);
//		}
//		return true;
//	}

}