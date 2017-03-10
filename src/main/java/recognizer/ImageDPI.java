package main.java.recognizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.PixelDensity;

public class ImageDPI {

	public static void main(String[] args) throws IOException {

		// file to read 
		File infile = new File("C:\\test350.jpg");
		// file to create
		File outfile = new File("E:\\test-out1.png");
		// Image output format
		ImageFormat imf = ImageFormats.PNG;
		// read image binairy file
		BufferedImage image = ImageIO.read(infile);;
		// initialise image
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

		// get DPI
		final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
		final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();

		// test if DPI is less then 300 or not 
		if ((physicalWidthDpi < 300) || (physicalHeightDpi < 300)) {
			System.out.println(
					"DPI is less then 300 : Width dpi = " + physicalWidthDpi + " Height dpi = " + physicalHeightDpi);
			try {
				Map<String, Object> params = new HashMap<>();
				params.put(ImagingConstants.PARAM_KEY_PIXEL_DENSITY,PixelDensity.createFromPixelsPerCentimetre(300, 300));
				Imaging.writeImage(image, outfile, imf, params);
			} catch (ImageWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("DPI is OK : Width dpi = " + physicalWidthDpi + " Height dpi = " + physicalHeightDpi);
		}

	}

}
