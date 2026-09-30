package nl.guidobreuer.graph.service;

import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.model.CustomGraphData;
import nl.guidobreuer.graph.model.RenderingSettings;
import nl.guidobreuer.graph.model.graph3d.DatasetGraph;
import nl.guidobreuer.graph.model.graph3d.Graph3DRenderer;

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
		
		return renderer;
	}
	
}
