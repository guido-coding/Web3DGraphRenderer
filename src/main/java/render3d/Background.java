package render3d;

import java.awt.image.BufferedImage;

abstract class Background {

	abstract BufferedImage getBackground(double theta, double phi, double horizontalViewAngle, double verticalViewAngle);
	
}
