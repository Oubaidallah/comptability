package main.java.recognizer;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageDPI_3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Image image = null;
		try {
			File sourceimage = new File ("C:\\test-dpi.jpg");
			image = ImageIO.read(sourceimage);
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		JFrame frame = new JFrame();
		frame.setSize(600, 600);
		JLabel label = new JLabel(new ImageIcon(image));
		frame.add(label);
		frame.setVisible(true);

	}

}
