package nl.guidobreuer.graph.model;

import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.model.graph3d.DatasetGraph;
import nl.guidobreuer.graph.model.graph3d.Graph3DRenderer;

public class Graph3DRendererBuilder {

	/**
	 * Create a Graph3DRenderer object using a RenderingSettings object
	 * @param settings
	 * @return
	 * @throws InvalidEquationException in case provided equation is not a valid equation (parsing of equation fails)
	 */
	public static Graph3DRenderer createGraph3DRenderer(RenderingSettings settings) throws InvalidEquationException {
		Graph3DRenderer renderer = new Graph3DRenderer(settings.getEquation());
		
		renderer.setRotation(settings.getRotation());
		renderer.setPhi(settings.getVrot());
		renderer.setR(settings.getZoom());
		
		if (settings.autoAdjustZ()) {
			renderer.setBounds(settings.getMinX(), settings.getMaxX(), settings.getMinY(), settings.getMaxY());
		} else {
			renderer.setBounds(settings.getMinX(), settings.getMaxX(), settings.getMinY(), settings.getMaxY(), settings.getMinZ(), settings.getMaxZ());
		}
		
		renderer.setOffsets(settings.getxOffset(), settings.getyOffset(), settings.getzOffset());
		renderer.setZScalingFactor(settings.getScalingFactorZ());
		renderer.setSteps(settings.getSteps());
		
		renderer.setTransparency(settings.getTransparency());
		
		renderer.showLabels(settings.showLabels());
		renderer.drawAxis(settings.showAxis());
		renderer.drawGrid(settings.showGrid());
		
		renderer.setXLabel(settings.getXLabel());
		renderer.setYLabel(settings.getYLabel());
		renderer.setZLabel(settings.getZLabel());
		
		return renderer;
	}
	
	
	/**
	 * Create a Graph3DRenderer object using a CustomGraphData object
	 * @param settings
	 * @return
	 */
	public static Graph3DRenderer createGraph3DRendererCustomData(CustomGraphData settings) {
		Graph3DRenderer renderer = new Graph3DRenderer(new DatasetGraph(settings.data()));
		
		
		renderer.setRotation(settings.rotation());
		renderer.setPhi(settings.vrot());
		renderer.setR(settings.zoom());
		
		if (settings.autoAdjustZ()) {
			renderer.setBounds(settings.minX(), settings.maxX(), settings.minY(), settings.maxY());
		} else {
			renderer.setBounds(settings.minX(), settings.maxX(), settings.minY(), settings.maxY(), settings.minZ(), settings.maxZ());
		}
		
		renderer.setOffsets(settings.xOffset(), settings.yOffset(), settings.zOffset());
		renderer.setZScalingFactor(settings.scalingFactorZ());
		renderer.setSteps(settings.steps());
		
		renderer.setTransparency(settings.transparency());
		
		renderer.showLabels(settings.showLabels());
		renderer.drawAxis(settings.showAxis());
		renderer.drawGrid(settings.showGrid());
		
		renderer.setXLabel(settings.xlabel());
		renderer.setYLabel(settings.ylabel());
		renderer.setZLabel(settings.zlabel());
		
		return renderer;
	}
	
}
