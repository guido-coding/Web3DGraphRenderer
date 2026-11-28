package nl.guidobreuer.graph.model.graph3d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Optional;

import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.model.graph3d.Graph.Coordinate;
import render3d.Scene;


public class Graph3DRenderer {
	
	private final Graph graph;
	
	private double r, theta, phi;
	private final int width, height;
	
	private Optional<String> graphLabel = Optional.empty();
	
	private boolean drawAxis = true;
	private boolean drawGrid = true;
	private boolean showLabels = true;
	
	private String xLabel = "x";
	private String yLabel = "y";
	private String zLabel = "z";
	

	public static final double CAMERA_DISTANCE_OBJECT_SIZE_RATIO = 5;
	
	
	/**
	 * Create Graph3DRenderer using a Graph object
	 * @param graph
	 */
	public Graph3DRenderer(Graph graph) {
		this.graph = graph;
		
		r = 100;
		theta = 0.26*Math.PI;
		phi = 0.3*Math.PI;
		
		width = 1000;
		height = 1000;
	}
	
	
	/**
	 * Create a Graph3DRenderer object using a String containing an equation
	 * @param equation
	 * @throws InvalidEquationException
	 */
	public Graph3DRenderer(String equation) throws InvalidEquationException {
		this(new CustomGraph(equation));
		graphLabel = Optional.of("Z = " + equation);
	}
	
	

	
	
	/*
	 * Setters for the various properties
	 */
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
		graph.setBounds(minX, maxX, minY, maxY);
	}
	
	public void setBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		graph.setBounds(minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	public void setOffsets(double xOffset, double yOffset, double zOffset) {
		graph.setOffsets(xOffset, yOffset, zOffset);
	}
	
	public void setSteps(int steps) {
		graph.steps = steps;
	}
	
	public void setZScalingFactor(double scalingFactor) {
		graph.setZScalingFactor(scalingFactor);
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
	
	public void setXLabel(String xLabel) {
		this.xLabel = xLabel;
	}

	public void setYLabel(String yLabel) {
		this.yLabel = yLabel;
	}

	public void setZLabel(String zLabel) {
		this.zLabel = zLabel;
	}
	
	
	
	/**
	 * Render image using default image dimensions
	 * @return
	 */
	public Image getGraphImage() {
		return getGraphImage(width, height);
	}
	
	/**
	 * Render image using specified image dimension
	 * @param width
	 * @param height
	 * @return
	 */
	public Image getGraphImage(int width, int height) {
		Scene scene = createScene();
		
		scene.addObjects(graph.getObjects(drawAxis, drawGrid));
		
		//scene.report();

		double zoom = (r/100) * Graph.STANDARD_AXIS_DIMENSION * CAMERA_DISTANCE_OBJECT_SIZE_RATIO;
		
		Coordinate center = graph.getCenter();
		scene.setCamera(center.x(), center.y(), center.z(), zoom, theta, phi);
		
		Image image = scene.next3DView(width, height);
		
		if (Thread.interrupted() || image == null) {
			throw new IllegalStateException("Request exceeded maximum duration.");
		}
		
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
			
			double xStepSize = (graph.getMaxSceneX() - graph.getMinSceneX())/ 10;
			double yStepSize = (graph.getMaxSceneY() - graph.getMinSceneY())/ 10; 
			
			p = scene.getLocationOnScreen(width, height, graph.getMinSceneX() - xStepSize, graph.getYOffsetScene(), graph.getZOffsetScene());
			g.drawString(xLabel + " (" + graph.minX() + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.getMaxSceneX() + xStepSize, graph.getYOffsetScene(), graph.getZOffsetScene());
			g.drawString(xLabel + " (" + graph.maxX() + ")", p.x, p.y);
			
			p = scene.getLocationOnScreen(width, height, graph.getXOffsetScene(), graph.getMinSceneY() - yStepSize, graph.getZOffsetScene());
			g.drawString(yLabel + " (" + graph.minY() + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.getXOffsetScene(), graph.getMaxSceneY() + yStepSize, graph.getZOffsetScene());
			g.drawString(yLabel + " (" + graph.maxY() + ")", p.x, p.y);
			
			p = scene.getLocationOnScreen(width, height, graph.getXOffsetScene(), graph.getYOffsetScene(), graph.getMaxSceneZ());
			g.drawString(zLabel + " (" + graph.maxZ() + ")", p.x, p.y);
			p = scene.getLocationOnScreen(width, height, graph.getXOffsetScene(), graph.getYOffsetScene(), graph.getMinSceneZ());
			g.drawString(zLabel + " (" + graph.minZ() + ")", p.x, p.y);
		}
		
		if (graphLabel.isPresent()) {			
			g.drawString(graphLabel.get(), 40, 40);
		}

		g.dispose();
		
		return image;
	}
	
	private Scene createScene() {
		Scene scene = new Scene();
		
		Scene.MAX_DRAW_DISTANCE = -1;
		Scene.DRAW_POLYGON_COUNTOUR = true;
		Scene.REJECT_FACES_BEHIND = false;
		Scene.ANTI_ALIAS = true;
		Scene.DRAW_MINIMAP = false;
		
		return scene;
	}
	

}
