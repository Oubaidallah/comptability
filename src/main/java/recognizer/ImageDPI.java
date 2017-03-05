package main.java.recognizer;

import java.io.File;
import java.io.IOException;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

public class ImageDPI {

	public static void main(String[] args) {
		
		File file = new File("C:\\Sanstitre.png");
		
		ImageInfo imageInfo = null;
		try {
			imageInfo = Sanselan.getImageInfo(file);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
		final int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();
		
		if((physicalWidthDpi<300) || (physicalHeightDpi<300)){
			System.out.println("DPI is less then 300 : Width dpi = "+physicalWidthDpi +" Height dpi = " + physicalHeightDpi);
		} else
		{
			System.out.println("DPI is OK :D");
		}
		
		
		

	}

}
