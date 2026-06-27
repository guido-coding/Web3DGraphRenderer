package render3d;

import java.awt.Color;

public interface ColorAdjuster {
	
	public Color getAdjustedColor(Color color, Face3D face, Object3D object);

}
