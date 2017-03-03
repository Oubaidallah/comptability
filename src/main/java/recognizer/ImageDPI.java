package main.java.recognizer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.xml.soap.Node;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class ImageDPI {

	public static void main(String[] args) throws IOException {
		
		//BufferedImage iis = ImageIO.read(new File("test-dpi.jpg"));
		ImageInputStream iis = ImageIO.createImageInputStream(new File("test-dpi.jpg"));
	    Iterator it = ImageIO.getImageReaders(iis);
	    if (!it.hasNext())
	    {
	        System.err.println("No reader for this format");
	        return;
	    }
	    ImageReader reader = (ImageReader) it.next();
	    reader.setInput(iis);

	    IIOMetadata meta = reader.getImageMetadata(0);
	    IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree("javax_imageio_1.0");
	    NodeList nodes = root.getElementsByTagName("HorizontalPixelSize");
	    if (nodes.getLength() > 0)
	    {
	        IIOMetadataNode dpcWidth = (IIOMetadataNode) nodes.item(0);
	        NamedNodeMap nnm = dpcWidth.getAttributes();
	        Node item = (Node) nnm.item(0);
	        int xDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
	        System.out.println("xDPI: " + xDPI);
	    }
	    else
	        System.out.println("xDPI: -");
	    if (nodes.getLength() > 0)
	    {
	        nodes = root.getElementsByTagName("VerticalPixelSize");
	        IIOMetadataNode dpcHeight = (IIOMetadataNode) nodes.item(0);
	        NamedNodeMap nnm = dpcHeight.getAttributes();
	        Node item = (Node) nnm.item(0);
	        int yDPI = Math.round(25.4f / Float.parseFloat(item.getNodeValue()));
	        System.out.println("yDPI: " + yDPI);
	    }
	    else
	        System.out.println("yDPI: -");
	}

}
