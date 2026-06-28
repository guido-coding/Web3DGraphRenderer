package graph3d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;

import equationparser.InvalidEquationException;
import render3d.Scene;

/**
 * Settings / adjustments
 * 
 * -equation							-done
 * -rotation, phi, r (zoom)				-done
 * -rendering width and height			-done
 * -min/max x, y, z						-done
 * -auto adjust z						-done
 * -scaling factor z					-done
 * -show axis, show grid, show labels	-done
 * -transparency						-done
 * -y offset x-axis						-done
 * -x offset y-axis						-done
 * -z offset x-axis + y-axis + grid		-done
 * -stepsize							-done
 * 
 */
public class Graph3DRenderer {
	
	private final Scene scene;
	private final Graph graph;
	
	private double r, theta, phi;
	private final int width, height;
	
	private String equation = "";
	
	private boolean drawAxis = true;
	private boolean drawGrid = true;
	private boolean showLabels = true;
	
	public static final double CAMERA_DISTANCE_OBJECT_SIZE_RATIO = 5;
	
	private Graph3DRenderer(Graph graph) {
		this.graph = graph;
		
		r = 100;
		theta = 0.26*Math.PI;
		phi = 0.3*Math.PI;
		
		width = 1000;
		height = 1000;
		
		scene = new Scene();

		Scene.MAX_DRAW_DISTANCE = -1;
		Scene.DRAW_POLYGON_COUNTOUR = true;
		Scene.REJECT_FACES_BEHIND = false;
		Scene.ANTI_ALIAS = true;
		Scene.DRAW_MINIMAP = false;

	}
	
	
	public Graph3DRenderer(double A, double B, double C, double D, double E) {
		this(new CustomGraph(A, B, C, D, E));
	}
	
	public Graph3DRenderer(String equation) throws InvalidEquationException {
		this(new CustomGraph2(equation));
		this.equation = equation;
	}
	
	public void setRotation(double rotation) {
		theta = rotation * Math.PI;
	}
	
	public void setPhi(double phi) {
		this.phi = Math.PI * phi;
	}
	
	public void setR(double r) {
		this.r = r;
	}
	
	public void drawAxis(boolean drawAxis) {
		this.drawAxis = drawAxis;
	}
	
	public void drawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}
	
	public void showLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}
	
	public void setBounds(double minX, double maxX, double minY, double maxY) {
		graph.minX = minX;
		graph.maxX = maxX;
		graph.minY = minY;
		graph.maxY = maxY;
		graph.autoAdjustZ = true;
	}
	
	public void setBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		graph.minX = minX;
		graph.maxX = maxX;
		graph.minY = minY;
		graph.maxY = maxY;
		graph.minZ = minZ;
		graph.maxZ = maxZ;
		graph.autoAdjustZ = false;
	}
	
	public void setOffsets(double xOffset, double yOffset, double zOffset) {
		graph.xOffset = xOffset;
		graph.yOffset = yOffset;
		graph.zOffset = zOffset;
	}
	
	public void setSteps(int steps) {
		graph.steps = steps;
	}
	
	public void setZScalingFactor(double scalingFactor) {
		graph.ZscalingFactor = scalingFactor;
	}
	
	public void setTransparency(int alpha) {
		if (alpha < 0) {
			graph.alpha = 0;
		} else if (alpha > 255) {
			graph.alpha = 255;
		} else {			
			graph.alpha = alpha;
		}
	}
	
	public Image getGraphImage() {
		return getGraphImage(width, height);
	}
	
	public Image getGraphImage(int width, int height) {
		scene.addObjects(graph.getObjects(drawAxis, drawGrid));
		
		//scene.report();
		
		double averageX = (graph.maxX + graph.minX)/2;
		double averageY = (graph.maxY + graph.minY)/2;
		double averageZ = (graph.maxZ+graph.minZ)/2;
	
		double xDim = graph.maxX - graph.minX;
		double yDim = graph.maxY - graph.minY;
		double maxDim = xDim > yDim ? xDim : yDim;
		
		double zoom = (r/100) * maxDim * CAMERA_DISTANCE_OBJECT_SIZE_RATIO;
		
		scene.setCamera(averageX, averageY, averageZ, zoom, theta, phi);
		
		Image image = scene.next3DView(width, height);
		
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.BLACK);
		g.setFont(new Font("verdana", Font.BOLD, 18));
		
		/*
		Point p = scene.getLocationOnScreen(image.getWidth(), image.getHeight(), 0, 0, 0);
		g.drawString("0", p.x, p.y);
		*/
		
		if (showLabels) {
			Point p;
			
			double xStepSize = (graph.maxX - graph.minX)/ 10;
			double yStepSize = (graph.maxY - graph.minY)/ 10; 
			
			p = scene.getLocationOnScreen(width, height, graph.minX - xStepSize, graph.yOffset, graph.zOffset);
			g.drawString("x (" + graph.minX + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.maxX + xStepSize, graph.yOffset, graph.zOffset);
			g.drawString("x (" + graph.maxX + ")", p.x, p.y);
			
			p = scene.getLocationOnScreen(width, height, graph.xOffset, graph.minY - yStepSize, graph.zOffset);
			g.drawString("y (" + graph.minY + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.xOffset, graph.maxY + yStepSize, graph.zOffset);
			g.drawString("y (" + graph.maxY + ")", p.x, p.y);
			
			p = scene.getLocationOnScreen(width, height, graph.xOffset, graph.yOffset, graph.maxZ);
			g.drawString("z (" + graph.maxZ + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.xOffset, graph.yOffset, graph.minZ);
			g.drawString("z (" + graph.minZ + ")", p.x, p.y);
		}
		
		g.drawString("z = " + equation, 40, 40);

		g.dispose();
		
		return image;
	}
	

}
