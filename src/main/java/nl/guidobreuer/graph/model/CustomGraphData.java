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

	

}
