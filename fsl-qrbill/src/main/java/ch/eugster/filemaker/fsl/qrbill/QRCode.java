package ch.eugster.filemaker.fsl.qrbill;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;

import io.nayuki.qrcodegen.QrCode;
import io.nayuki.qrcodegen.QrCode.Ecc;

public class QRCode extends Executor 
{
	public static String generate(String request)
	{
		if (createRequestNode(request))
		{
			try 
			{
				final String value = getRequestNode().get(Key.VALUE.key()).asText();
				final QrCode qrcode = QrCode.encodeText(value, Ecc.values()[getEcc()]);
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				BufferedImage bi = toImage(qrcode, getScale(), getBorder(), getColorLight(), getColorDark());
				ImageIO.write(bi, getGraphicsFormat(), os);
				getResponseNode().put(Key.RESULT.key(), os.toByteArray());
			} 
			catch (Exception e) 
			{
				addErrorMessage("invalid_json_format_parameter: '" + e.getLocalizedMessage() + "'");
			}
		}
		return getResponse();
	}
	
	private static int getEcc()
	{
		int ecc = 0;
		JsonNode node = getRequestNode().get(Key.ECC.key());
		if (node != null && node.asInt() > 0 && node.asInt() < 4)
		{
			ecc = node.asInt();
		}
		return ecc;
	}
	
	private static int getScale()
	{
		int scale = 10;
		JsonNode node = getRequestNode().get(Key.SCALE.key());
		if (node != null && node.asInt() > 0)
		{
			scale = node.asInt();
		}
		return scale;
	}
	
	private static int getBorder()
	{
		int border = 4;
		JsonNode node = getRequestNode().get(Key.BORDER.key());
		if (node != null && node.asInt() > 0)
		{
			border = node.asInt();
		}
		return border;
	}
	
	private static int getColorLight()
	{
		int color = 0xFFFFFF;
		JsonNode node = getRequestNode().get(Key.COLOR_LIGHT.key());
		if (node != null && node.asInt() >= 0x000000 && node.asInt() <= 0xFFFFFF)
		{
			color = node.asInt();
		}
		return color;
	}
	
	private static int getColorDark()
	{
		int color = 0x000000;
		JsonNode node = getRequestNode().get(Key.COLOR_DARK.key());
		if (node != null && node.asInt() >= 0x000000 && node.asInt() <= 0xFFFFFF)
		{
			color = node.asInt();
		}
		return color;
	}
	
	private static String getGraphicsFormat()
	{
		String graphicsFormat = "PNG";
//		JsonNode node = getRequestNode().get(Key.GRAPHICS_FORMAT.key());
//		if (node != null && (node.asText().equals("PNG") || node.asText().equals("SVG")))
//		{
//			graphicsFormat = node.asText();
//		}
		return graphicsFormat;
	}
	
	/**
	 * Returns a raster image depicting the specified QR Code, with
	 * the specified module scale, border modules, and module colors.
	 * <p>For example, scale=10 and border=4 means to pad the QR Code with 4 light border
	 * modules on all four sides, and use 10&#xD7;10 pixels to represent each module.
	 * @param qr the QR Code to render (not {@code null})
	 * @param scale the side length (measured in pixels, must be positive) of each module
	 * @param border the number of border modules to add, which must be non-negative
	 * @param lightColor the color to use for light modules, in 0xRRGGBB format
	 * @param darkColor the color to use for dark modules, in 0xRRGGBB format
	 * @return a new image representing the QR Code, with padding and scaling
	 * @throws NullPointerException if the QR Code is {@code null}
	 * @throws IllegalArgumentException if the scale or border is out of range, or if
	 * {scale, border, size} cause the image dimensions to exceed Integer.MAX_VALUE
	 */
	private static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
		Objects.requireNonNull(qr);
		if (scale <= 0 || border < 0)
			throw new IllegalArgumentException("Value out of range");
		if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
			throw new IllegalArgumentException("Scale or border too large");
		
		BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < result.getHeight(); y++) {
			for (int x = 0; x < result.getWidth(); x++) {
				boolean color = qr.getModule(x / scale - border, y / scale - border);
				result.setRGB(x, y, color ? darkColor : lightColor);
			}
		}
		return result;
	}

	public enum Key
	{
		// @formatter:off
		VALUE("value"), ECC("ecc"), BORDER("border"), SCALE("scale"), COLOR_LIGHT("color_light"), COLOR_DARK("color_dark"), RESULT("result"), GRAPHICS_FORMAT("graphics_format");
		
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
