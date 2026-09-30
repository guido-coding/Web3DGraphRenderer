package nl.guidobreuer.graph.model;


public record CustomGraphData(
		double rotation, 
		double vrot, 
		double zoom,
		int width, 
		int height,
		double minX, 
		double minY, 
		double minZ, 
		double maxX, 
		double maxY, 
		double  maxZ,
		boolean autoAdjustZ,
		double scalingFactorZ,
		boolean showAxis, 
		boolean showGrid, 
		boolean showLabels,
		int transparency,
		double yOffset, 
		double xOffset, 
		double zOffset,
		int steps,
		Graph3DDatapoint[][] data
		) {

	public CustomGraphData {
		if (vrot < 0.0001) vrot = 0.0001;
		if (vrot > 1) vrot = 1;
		
		if (zoom < 50) zoom = 50;
		if (zoom > 200) zoom = 200;
		
		if (width < 100) width = 100;
		if (width > 2000) width = 2000;
		
		if (height < 100) height = 100;
		if (height > 2000) height = 2000;
		
		if (minX > maxX) {
			double temp = minX;
			maxX = minX;
			minX = temp;
		}
		if (minY > maxY) {
			double temp = minY;
			maxY = minY;
			minY = temp;
		}
		if (minZ > maxZ) {
			double temp = minZ;
			maxZ = minZ;
			minZ = temp;
		}
		
		if (minX == maxX) maxX = minX + 1;
		if (minY == maxY) maxY = minY + 1;
		if (minZ == maxZ) maxZ = minZ + 1;
		
		if (scalingFactorZ < 0.01) scalingFactorZ = 0.01;
		if (scalingFactorZ > 100) scalingFactorZ = 100;
		
		if (transparency < 0) transparency = 0;
		if (transparency > 255) transparency = 255;
		
		if (steps < 10) steps = 10;
		if (steps > 100) steps = 100;
		
		if (data == null) throw new NullPointerException("No data provided.");
		if (data.length < 2 || data[0].length < 2) throw new IllegalStateException("Too few datapoints provided"); 
		int length = data[0].length;
		for (Graph3DDatapoint[] array : data) {
			if (array.length != length) throw new IllegalStateException("Columns in data are not of same length.");
		}
	}

}
