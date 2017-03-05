package main.java.recognizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.PixelDensity;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;

public class ImageDPI {

	public static void main(String[] args) {

		File infile = new File("C:\\test300.jpg");
		File outfile = new File("E:\\test-out.png");
		ImageFormat imf = null;
		BufferedImage imageBuff = null;
		ImageInfo imageInfo = null;
		try {
			imageInfo = Imaging.getImageInfo(infile);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
		final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();

		if ((physicalWidthDpi < 300) || (physicalHeightDpi < 300)) {
			System.out.println(
					"DPI is less then 300 : Width dpi = " + physicalWidthDpi + " Height dpi = " + physicalHeightDpi);
			Map<String, PixelDensity> params = new HashMap<String, PixelDensity>();
			params.put(ImagingConstants.PARAM_KEY_PIXEL_DENSITY, PixelDensity.createFromPixelsPerCentimetre(300, 300));
			// Imaging.writeImage(infile, outfile, imf, params);
		} else {
			System.out.println("DPI is OK : Width dpi = " + physicalWidthDpi + " Height dpi = " + physicalHeightDpi);
		}

	}

}
