package main.java.recognizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;

public class ImageWriteExample {
	public static byte[] imageWriteExample(String url) throws ImageReadException, ImageWriteException, IOException {
		// read image
		File infile = new File(url);
		final BufferedImage image = Imaging.getBufferedImage(infile);

		final ImageFormat format = ImageFormats.TIFF;
		final Map<String, Object> params = new HashMap<>();

		// set optional parameters if you like
		params.put(ImagingConstants.PARAM_KEY_COMPRESSION,
				Integer.valueOf(TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED));

		final byte[] bytes = Imaging.writeImageToBytes(image, format, params);

		return bytes;
	}

	public static void main(String[] args) {
		try {
			System.out.println(imageWriteExample("C:\\test72.jpg"));
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}