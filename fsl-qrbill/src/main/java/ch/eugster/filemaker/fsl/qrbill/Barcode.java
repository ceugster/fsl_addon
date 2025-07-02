package ch.eugster.filemaker.fsl.qrbill;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.JsonNode;

import de.vwsoft.barcodelib4j.image.CompoundColor;
import de.vwsoft.barcodelib4j.image.ImageCreator;
import de.vwsoft.barcodelib4j.oned.BarcodeException;
import de.vwsoft.barcodelib4j.oned.BarcodeType;

public class Barcode extends Executor
{
	public static String generate(String request) throws BarcodeException
	{
		if (createRequestNode(request))
		{
			de.vwsoft.barcodelib4j.oned.Barcode barcode = null;
			JsonNode barcodeTypeNode = getRequestNode().get(Key.BARCODE_TYPE.key());
			if (barcodeTypeNode == null)
			{
				addErrorMessage("Barcode type not provided.");
			}
			else
			{
				try
				{
					BarcodeType barcodeType = BarcodeType.valueOf(barcodeTypeNode.asText());
					barcode = de.vwsoft.barcodelib4j.oned.Barcode.newInstance(barcodeType);
				}
				catch (Exception e)
				{
					addErrorMessage("Barcode type '" + barcodeTypeNode.asText() + "' not supported.");
				}
			}
			if (barcode != null)
			{
				setContent(barcode);
				setFont(barcode);
				setText(barcode);

				ImageCreator imageCreator = new ImageCreator(getWidth(), getHeight());
				setTitle(imageCreator);
				setForegroundColor(imageCreator);
				setBackgroundColor(imageCreator);
				setOpaque(imageCreator);
				setTransform(imageCreator);
				
				Graphics2D g2d = imageCreator.getGraphics2D();
				barcode.draw(g2d, getPosX(), getPosY(), getWidth(), getHeight());
				g2d.dispose();
		
				try (ByteArrayOutputStream os = new ByteArrayOutputStream())
				{
					imageCreator.write(os, getGraphicsFormat(), getColorSpace(), getXRes(), getYRes());
					getResponseNode().put(Executor.RESULT, os.toByteArray());
				}
				catch (Exception e)
				{
					addErrorMessage(e.getLocalizedMessage());
				}
			}
		}
		return getResponse();
	}
	
	private static void setContent(de.vwsoft.barcodelib4j.oned.Barcode barcode)
	{
		JsonNode node = getRequestNode().get(Key.CONTENT.key());
		if (node == null)
		{
			addErrorMessage("A valid (non empty) value for request node 'content' must be provided.");
		}
		else
		{
			if (node.asText().isBlank())
			{
				addErrorMessage("A valid (non empty) value for request node 'content' must be provided.");
			}
			else
			{
				try
				{
					barcode.setContent(node.asText(), getAutoComplete(), getAppendOptionalChecksum());
				}
				catch (BarcodeException e)
				{
					addErrorMessage(e.getLocalizedMessage());
				}
			}
		}
	}
	
	private static void setFont(de.vwsoft.barcodelib4j.oned.Barcode barcode)
	{
		try
		{
			Font font = null;
			JsonNode node = getRequestNode().get(Key.FONT.key());
			if (node != null)
			{
				font = Font.decode(node.asText());
				if (font == null)
				{
					addErrorMessage("Font with name '" + node.asText() + "' not available.");
				}
				else
				{
					barcode.setFont(font);
				}
			}
			else
			{
			    try
			    {
			    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			    	ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Barcode.class.getResourceAsStream("../fonts/OCR-B.ttf")));
					barcode.setFont(font);
			    }
			    catch (Exception e)
			    {
					addErrorMessage(e.getLocalizedMessage());
			    }
			}

			node = getRequestNode().get(Key.FONT_SIZE_ADJUSTED.key());
			if (node != null && node.isBoolean())
			{
				barcode.setFontSizeAdjusted(node.asBoolean());
			}
			else
			{
				barcode.setFontSizeAdjusted(true);
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	private static void setText(de.vwsoft.barcodelib4j.oned.Barcode barcode)
	{
		JsonNode node = getRequestNode().get(Key.TEXT.key());
		if (node != null)
		{
			barcode.setCustomText(node.asText());
			node = getRequestNode().get(Key.TEXT_VISIBLE.key());
			if (node != null && node.isBoolean())
			{
				barcode.setTextVisible(node.asBoolean());
			}
			node = getRequestNode().get(Key.TEXT_TOP.key());
			if (node != null && node.isBoolean())
			{
				barcode.setTextOnTop(node.asBoolean());
			}
			node = getRequestNode().get(Key.TEXT_OFFSET.key());
			if (node != null && node.isDouble())
			{
				barcode.setTextOffset(node.asDouble());
			}
		}
	}

	private static void setTitle(ImageCreator imageCreator)
	{
		JsonNode node = getRequestNode().get(Key.TITLE.key());
		if (node != null && node.isTextual())
		{
			imageCreator.setTitle(node.asText());
		}
	}
	
	private static void setForegroundColor(ImageCreator imageCreator)
	{
//		int color = 0xFFFFFF;
		JsonNode node = getRequestNode().get(Key.COLOR_LIGHT.key());
		if (node != null)
		{
			if (node.asInt() >= 0x000000 && node.asInt() <= 0xFFFFFF)
			{
				int color = node.asInt();
				imageCreator.setForeground(new CompoundColor(color));
			}
			else
			{
				addErrorMessage("Foreground color '" + String.format("0x%08X", node.asInt()) + "' not valid.");
			}
		}
	}

	private static void setBackgroundColor(ImageCreator imageCreator)
	{
//		int color = 0x000000;
		JsonNode node = getRequestNode().get(Key.COLOR_DARK.key());
		if (node != null)
		{
			if (node.asInt() >= 0x000000 && node.asInt() <= 0xFFFFFF)
			{
				int color = node.asInt();
				imageCreator.setForeground(new CompoundColor(color));
			}
			else
			{
				addErrorMessage("Background color '" + String.format("0x%08X", node.asInt()) + "' not valid.");
			}
		}
	}

	private static void setOpaque(ImageCreator imageCreator)
	{
		JsonNode node = getRequestNode().get(Key.OPAQUE.key());
		if (node != null)
		{
			boolean opaque = node.asBoolean();
			imageCreator.setOpaque(opaque);
		}
	}

	private static void setTransform(ImageCreator imageCreator)
	{
		JsonNode node = getRequestNode().get(Key.TRANSFORM.key());
		if (node != null)
		{
			if (node.asInt() >= 0 || node.asInt() <= 7)
			{
				int transform = node.asInt();
				imageCreator.setTransform(transform);
			}
			else
			{
				addErrorMessage("Transform type '" + node.asInt() + "' not valid.");
			}
		}
	}

	private static boolean getAutoComplete()
	{
		JsonNode node = getRequestNode().get(Key.AUTO_COMPLETE.key());
		if (node != null)
		{
			return node.asBoolean();
		}
		return false;
	}

	private static boolean getAppendOptionalChecksum()
	{
		JsonNode node = getRequestNode().get(Key.APPEND_OPTIONAL_CHECKSUM.key());
		if (node != null)
		{
			return node.asBoolean();
		}
		return false;
	}

	private static double getPosX()
	{
		double posX = 0d;
		JsonNode node = getRequestNode().get(Key.POS_X.key());
		if (node != null && node.asDouble() > 0)
		{
			posX = node.asDouble();
		}
		return posX;
	}

	private static double getPosY()
	{
		double posY = 0d;
		JsonNode node = getRequestNode().get(Key.POS_Y.key());
		if (node != null && node.asDouble() > 0)
		{
			posY = node.asDouble();
		}
		return posY;
	}

	private static int getXRes()
	{
		int xRes = 300;
		JsonNode node = getRequestNode().get(Key.X_RES.key());
		if (node != null && node.asInt() > 0)
		{
			xRes = node.asInt();
		}
		return xRes;
	}

	private static int getYRes()
	{
		int yRes = 300;
		JsonNode node = getRequestNode().get(Key.Y_RES.key());
		if (node != null && node.asInt() > 0)
		{
			yRes = node.asInt();
		}
		return yRes;
	}

	private static int getGraphicsFormat()
	{
		JsonNode node = getRequestNode().get(Key.GRAPHICS_FORMAT.key());
		if (node != null)
		{
			for (FileFormat fileFormat : FileFormat.values())
			{
				if (fileFormat.name().equals(node.asText()))
				{
					return fileFormat.ordinal();
				}
			}
		}
		return FileFormat.PNG.ordinal();
	}

	private static int getColorSpace()
	{
		JsonNode node = getRequestNode().get(Key.COLOR_SPACE.key());
		if (node != null)
		{
			String colorSpace = node.asText();
			try
			{
				Space selectedSpace = Space.valueOf(colorSpace);
				return selectedSpace.ordinal();
			}
			catch (IllegalArgumentException e)
			{
				addErrorMessage(e.getLocalizedMessage());
			}
		}
		return Space.RGB.ordinal();
	}

	private static double getWidth()
	{
		double width = 50;
		JsonNode node = getRequestNode().get(Key.WIDTH.key());
		if (node != null && node.asDouble() > 0)
		{
			width = node.asDouble();
		}
		return width;
	}

	private static double getHeight()
	{
		double height = 30;
		JsonNode node = getRequestNode().get(Key.HEIGHT.key());
		if (node != null && node.asDouble() > 0)
		{
			height = node.asDouble();
		}
		return height;
	}

	public enum FileFormat
	{
		PDF, EPS, SVG, PNG, BMP, JPG;
	}

	public enum Space
	{
		RGB, CMYK;
	}

	public enum Transform
	{
		// @formatter:off
		TRANSFORM_0(0), 
		TRANSFORM_90(1), 
		TRANSFORM_180(2), 
		TRANSFORM_270(3), 
		TRANSFORM_0N(4), 
		TRANSFORM_90N(5),
		TRANSFORM_180N(6), 
		TRANSFORM_270N(7);
		// @formatter:on

		private int value;

		private Transform(int value)
		{
			this.value = value;
		}

		public int value()
		{
			return this.value;
		}
	}

	public enum Key
	{
		// @formatter:off
		BARCODE_TYPE("barcodeType"), 
		CONTENT("content"), 
		FONT("font"),
		FONT_SIZE_ADJUSTED("fontSizeAdjusted"),
		TEXT("text"),
		TEXT_VISIBLE("textVisible"),
		TEXT_TOP("textTop"),
		TEXT_OFFSET("textOffset"),
		TITLE("title"),
		
		WIDTH("width"), 
		HEIGHT("heigth"), 
		POS_X("posX"), 
		POS_Y("posY"), 
		X_RES("xRes"), 
		Y_RES("yRes"), 
		COLOR_SPACE("colorSpace"), 
		COLOR_LIGHT("colorLight"), 
		COLOR_DARK("colorDark"), 
		GRAPHICS_FORMAT("graphicsFormat"), 
		OPAQUE("opaque"), 
		TRANSFORM("transform"),
		AUTO_COMPLETE("autoComplete"),
		APPEND_OPTIONAL_CHECKSUM("appendOptionalChecksum");
		// @formatter:off
		
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
