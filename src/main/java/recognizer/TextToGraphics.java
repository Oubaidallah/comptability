package main.java.recognizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.common.io.Files;

public class TextToGraphics {

    public static void textToImage(List<EntityAnnotation> entityAnnotationList,Path path) {
    	BufferedImage image;
		try {
			image = ImageIO.read(path.toFile());
			image = process(image, entityAnnotationList);
			ImageIO.write(image, "png", new File(Files.getNameWithoutExtension(path.getFileName().toString()) + ".annotated.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    private static BufferedImage process(BufferedImage old, List<EntityAnnotation> entityAnnotationList) {
        int w = old.getWidth();
        int h = old.getHeight();
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(old, 0, 0, null);
        //g2d.setPaint(Color.red);
        g2d.setFont(new Font("Serif", Font.BOLD, 20));
//        String s = "Hello, world!";
//        FontMetrics fm = g2d.getFontMetrics();
//        int x = img.getWidth() - fm.stringWidth(s) - 5;
//        int y = fm.getHeight();
//        g2d.drawString(s, x, y);
        Boolean detectedAmountLabel = false;
        Boolean amountDetected = false;
        int amountStartY = 0;
        for (EntityAnnotation entityAnnotation : entityAnnotationList) {
        	amountDetected = false;
        	List<Vertex> vertices = entityAnnotation.getBoundingPoly().getVertices();
        	int[] xpoints =  new int[vertices.size()];
			int[] ypoints = new int[vertices.size()];
        	for (int cptVertex = 0; cptVertex< vertices.size(); cptVertex ++) {
        		xpoints[cptVertex] = vertices.get(cptVertex).getX();
        		ypoints[cptVertex] = vertices.get(cptVertex).getY();
			}
        	try {
        		if(detectedAmountLabel){
        			float amount = Float.parseFloat(entityAnnotation.getDescription());
        			if (ypoints[0] > amountStartY){
	        			amountDetected = true;
	        			g2d.setPaint(Color.green);
        			}
        		}
			} catch (NumberFormatException e) {
				g2d.setPaint(Color.red);
			}
        	if (entityAnnotation.getDescription().equals("EUR")){
        		g2d.setPaint(Color.green);
        	}
        	else if (entityAnnotation.getDescription().equals("MONTANT") && !detectedAmountLabel){
        		g2d.setPaint(Color.green);
        		detectedAmountLabel = true;
        		amountStartY = ypoints[0];
        	}else if (!amountDetected){
        		g2d.setPaint(Color.red);
        	}
			Polygon polygon = new Polygon(xpoints, ypoints, xpoints.length);
			g2d.drawPolygon(polygon);
		}
        g2d.dispose();
        return img;
    }

}
