package nl.guidobreuer.graph.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class Util {

	public static ResponseEntity<Resource> imageToByteOutput(Image image) {
		try {
			byte[] array = toPNGBytes(image);
			Resource resource = new ByteArrayResource(array);
			ResponseEntity<Resource> ret = ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.body(resource);
			
			return ret;
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	
	private	 static byte[] toPNGBytes(Image image) throws IOException {
		if (image instanceof BufferedImage bufferedImage) {			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			return baos.toByteArray();
		} else {
			BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bufferedImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			return baos.toByteArray();
		}
	}
	
	public static BufferedImage getErrorImage(String message) {
		BufferedImage errorImage = new BufferedImage(500,100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = errorImage.createGraphics();
		g.setColor(new Color(200, 0,0));
		g.setFont(new Font("verdana", Font.BOLD, 18));;
		g.drawString("Error in rendering image: ", 20, 20);
		int height = g.getFontMetrics().getHeight();
		g.setFont(new Font("verdana", Font.PLAIN, 14));
		g.setColor(Color.BLACK);
		g.drawString(message, 20, 20 + height);
		g.dispose();
		return errorImage;
	}
	
}
