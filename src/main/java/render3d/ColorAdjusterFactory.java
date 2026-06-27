package render3d;

import java.awt.Color;

public class ColorAdjusterFactory {

	public static ColorAdjuster getType1ColorAdjuster() {
		return new Type1ColorAdjuster();
	}
	
	public static ColorAdjuster getType2ColorAdjuster() {
		return new Type2ColorAdjuster();
	}
	
	public static ColorAdjuster getType3ColorAdjuster() {
		return new Type3ColorAdjuster();
	}
	
	public static ColorAdjuster getType4ColorAdjuster(double min, double max) {
		return new Type4ColorAdjuster(min, max);
	}
}

class Type1ColorAdjuster implements ColorAdjuster {

	@Override
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double brightnessfactor = 1 - face.getAverageDistance()/20;
		
		if (brightnessfactor<0) brightnessfactor=0;

			//brightnessfactor = Math.pow(brightnessfactor, 0.5);
		
		
		int r = (int)(brightnessfactor * color.getRed());
		int b = (int)(brightnessfactor * color.getBlue());
		int gr = (int)(brightnessfactor * color.getGreen());
		
		return new Color(
				r < 255 ? r : 255,
				gr < 255 ? gr : 255,
				b < 255 ? b : 255,
				color.getAlpha()
				);
	}
	
}

class Type2ColorAdjuster implements ColorAdjuster {

	@Override
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double brightnessfactor;
		if (object.diameter > 0) {			
			brightnessfactor = ((object.getAverageDistance() - face.getAverageDistance() + object.diameter) / (2*object.diameter));	
		} else {
			brightnessfactor = 1;
		}
		
		int r = (int)(brightnessfactor * color.getRed());
		int b = (int)(brightnessfactor * color.getBlue());
		int gr = (int)(brightnessfactor * color.getGreen());
		
		return new Color(
				r < 255 ? r : 255,
				gr < 255 ? gr : 255,
				b < 255 ? b : 255,
				color.getAlpha()
				);
	}
	
}

class Type3ColorAdjuster implements ColorAdjuster {

	@Override
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double averageZ = 0;
		int n = 0;
		for (Point3D p : face.getPoints()) {
			averageZ += p.z;
			n++;
		}
		averageZ = Math.abs(averageZ / n);
		
		int factor = 100;
		int r = (int)(averageZ*factor);
		if (r<0) r = 0;
		if (r > 255) r = 255;
		int g = 255 - r;
		
		
		// TODO Auto-generated method stub
		return new Color(r, g, 0, color.getAlpha());
	}
	
}

class Type4ColorAdjuster implements ColorAdjuster {

	private final double min, max;
	
	Type4ColorAdjuster(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Color getAdjustedColor(Color color, Face3D face, Object3D object) {
		double averageZ = 0;
		int n = 0;
		for (Point3D p : face.getPoints()) {
			averageZ += p.z;
			n++;
		}
		averageZ = averageZ / n;
		
		double relativeZ = (averageZ - min) / (max - min); 
		if (relativeZ < 0) relativeZ = 0;
		if (relativeZ > 1) relativeZ = 1;
		
		int r, g;
		if (relativeZ > 0.5) {			
			r = 255;
			g = (int)(255*2 - 255*relativeZ*2);
		} else {		
			r = (int)(255 * relativeZ*2);
			g = 255;
		}
		
		
		// TODO Auto-generated method stub
		return new Color(r, g, 0, color.getAlpha());
	}
	
}