package nl.guidobreuer.graph.service;

import equationparser.InvalidEquationException;
import graph3d.Graph3DRenderer;
import nl.guidobreuer.graph.model.RenderingSettings;

public class Graph3DRendererBuilder {

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
		
		return renderer;
	}
	
}
