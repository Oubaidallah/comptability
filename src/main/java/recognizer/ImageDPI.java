package main.java.recognizer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NodeList;

public class ImageDPI {
	
	public static void main(String[] args) throws IOException {
		
        File input = new File(args[0]);

        ImageInputStream stream = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);

        if (readers.hasNext()) {
            ImageReader reader = readers.next();
            reader.setInput(stream);

            IIOMetadata metadata = reader.getImageMetadata(0);
            IIOMetadataNode standardTree = (IIOMetadataNode) metadata.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
            IIOMetadataNode dimension = (IIOMetadataNode) standardTree.getElementsByTagName("Dimension").item(0);
            int horizontalPixelSizeMM = getPixelSizeMM(dimension, "HorizontalPixelSize");
            int verticalPixelSizeMM = getPixelSizeMM(dimension, "VerticalPixelSize");

            // TODO: Convert pixelsPerMM to DPI left as an exercise to the reader.. ;-)  

            System.err.println("horizontalPixelSizeMM: " + horizontalPixelSizeMM);
            System.err.println("verticalPixelSizeMM: " + verticalPixelSizeMM);
        }
        else {
            System.err.printf("Could not read %s\n", input);
        }
    }

    private static int getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
        // NOTE: The standard metadata format has defined dimension to pixels per millimeters, not DPI...
        NodeList pixelSizes = dimension.getElementsByTagName(elementName);
        IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
        return pixelSize != null ? Integer.parseInt(pixelSize.getAttribute("value")) : -1;
    }

}
