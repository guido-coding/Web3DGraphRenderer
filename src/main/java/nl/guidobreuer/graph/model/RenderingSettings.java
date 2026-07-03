package nl.guidobreuer.graph.model;

import java.awt.Dimension;
import java.util.Map;

import nl.guidobreuer.graph.exception.InvalidInputException;

public class RenderingSettings {
	
	private final String equation;
	private final double rotation, vrot, zoom;
	private final int width, height;
	private final double minX, minY, minZ, maxX, maxY, maxZ;
	private final boolean autoAdjustZ;
	private final double scalingFactorZ;
	private final boolean showAxis, showGrid, showLabels;
	private final int transparency;
	private final double yOffset, xOffset, zOffset;
	private final int steps;
	
	public RenderingSettings(Map<String, String> input) throws InvalidInputException {
		
		String equationTemp = input.get("equation");
		if (equationTemp == null) {
			equation = "(0.1*y^2 - 0.1*x^2 + 0.1*x*y + 0.1)";
			//throw new InvalidInputException("No equation specified");
		} else {
			equation = equationTemp;
		}
		
		
		rotation = extractDouble(input, "rotation", 0.3);
		vrot = extractDouble(input, "vrot", 0.0001, 1, 0.3);
		zoom = extractDouble(input, "zoom", 50, 200, 100);
		
		width = extractInt(input, "width", 100, 2000, 1000);
		height = extractInt(input, "height", 100, 2000, 700);
		
		minX = extractDouble(input, "minx", -10);
		double maxXT = extractDouble(input, "maxx", 10);
		if (maxXT <= minX) {
			maxX = minX + 1;
		} else {
			maxX = maxXT;
		}
		
		minY = extractDouble(input, "miny", -10);
		double maxYT = extractDouble(input, "maxy", 10);
		if (maxYT <= minY) {
			maxY = minY + 1;
		} else {
			maxY = maxYT;
		}
		
		minZ = extractDouble(input, "minz", -10);
		double maxZT = extractDouble(input, "maxz", 10);
		if (maxZT <= minZ) {
			maxZ = minZ + 1;
		} else {
			maxZ = maxZT;
		}
		
		autoAdjustZ = extractBoolean(input, "autoadjustz", true);
		
		scalingFactorZ = extractDouble(input, "scalingfactorz", 0.01, 100, 1);	
		
		showAxis = extractBoolean(input, "showaxis", true);
		showGrid = extractBoolean(input, "showgrid", true);
		showLabels = extractBoolean(input, "showlabel", true);
		
		transparency = extractInt(input, "alpha", 0, 255, 200);
		
		yOffset = extractDouble(input, "yoffset", 0);
		xOffset = extractDouble(input, "xoffset", 0);
		zOffset = extractDouble(input, "zoffset", 0);
		
		steps = extractInt(input, "steps", 10, 100, 25);
	}

	private boolean extractBoolean(Map<String, String> input, String name, boolean defaultValue) {
		String temp = input.get(name);
		if (temp == null) {
			return defaultValue;
		} else {			
			return temp.equals("true");
		}
	}
	
	private double extractDouble(Map<String, String> input, String name, double defaultValue) {
		double temp;
		try {
			String value = input.get(name);
			if (value == null) return defaultValue;
			temp = Double.parseDouble(value);
		} catch(NumberFormatException e) {
			temp = defaultValue;
		}
		return temp;
	}
	
	private double extractDouble(Map<String, String> input, String name, double min, double max, double defaultValue) {
		double temp;
		try {
			String value = input.get(name);
			if (value == null) return defaultValue;
			temp = Double.parseDouble(value);
			if (temp < min) temp = min;
			if (temp > max) temp = max;
		} catch(NumberFormatException e) {
			temp = defaultValue;
		}
		return temp;
	}
	
	private int extractInt(Map<String, String> input, String name, int min, int max, int defaultValue) {
		int temp;
		try {
			temp = Integer.parseInt(input.get(name));
			if (temp < min) temp = min;
			if (temp > max) temp = max;
		} catch(NumberFormatException e) {
			temp = defaultValue;
		}
		return temp;
	}
	
	public String getEquation() {
		return equation;
	}

	public double getRotation() {
		return rotation;
	}

	public double getVrot() {
		return vrot;
	}

	public double getZoom() {
		return zoom;
	}
	
	public Dimension getSize() {
		return new Dimension(width, height);
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public double getScalingFactorZ() {
		return scalingFactorZ;
	}

	public boolean autoAdjustZ() {
		return autoAdjustZ;
	}

	public boolean showAxis() {
		return showAxis;
	}

	public boolean showGrid() {
		return showGrid;
	}

	public boolean showLabels() {
		return showLabels;
	}

	public int getTransparency() {
		return transparency;
	}

	public double getyOffset() {
		return yOffset;
	}

	public double getxOffset() {
		return xOffset;
	}

	public double getzOffset() {
		return zOffset;
	}

	public int getSteps() {
		return steps;
	}

	
	
}
