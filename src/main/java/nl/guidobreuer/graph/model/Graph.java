package nl.guidobreuer.graph.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import render3d.ColorAdjusterFactory;
import render3d.Object3D;
import render3d.Object3DFactory;


abstract class Graph implements Graph3DObject {
	
	public static final int AXIS_TO_GRAPH_SIZE = 200;
	public static final int STANDARD_AXIS_DIMENSION = 10;
	public static final boolean ADJUST_X_Y_SCALING = true;
	
	private double minX, minY, maxX, maxY;
	private double xScaling, yScaling;
	private double minZ, maxZ;
	private double ZscalingFactor;
	protected boolean autoAdjustZ = true;
	protected int alpha = 200;
	private double yOffset, xOffset, zOffset;
	int steps;
	

	/*
	 * Initialize default settings
	 */
	Graph() {
		minX = -10;
		minY = -10;
		maxX = 10;
		maxY = 10;
		ZscalingFactor = 1;
		yOffset = 0;
		xOffset = 0;
		zOffset = 0;
		steps = 10;
		xScaling = 1;
		yScaling = 1;
	}
	
	private void updateScaling() {
		if (ADJUST_X_Y_SCALING) {			
			xScaling = STANDARD_AXIS_DIMENSION / (maxX - minX);
			yScaling = STANDARD_AXIS_DIMENSION / (maxY - minY);
		}
	}
	
	void setBounds(double minX, double maxX, double minY, double maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		autoAdjustZ = true;
		updateScaling();
	}
	
	void setBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.minZ = minZ;
		this.maxZ = maxZ;
		autoAdjustZ = false;
		updateScaling();
	}
	
	void setOffsets(double xOffset, double yOffset, double zOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
	}
	
	void setZScalingFactor(double ZScalingFactor) {
		this.ZscalingFactor = ZScalingFactor;
	}
	
	record Coordinate(double x, double y, double z) {};
	
	/**
	 * Get center position (x, y, z coordinates) of all objects in the graph.
	 * @return
	 */
	Coordinate getCenter() {
		double averageX = (maxX + minX)/2;
		double averageY = (maxY + minY)/2;
		double averageZ = (maxZ + minZ)/2;
		return new Coordinate(
				averageX*xScaling, 
				averageY*yScaling, 
				averageZ*ZscalingFactor);
	}
	
	double getMaxSceneX() { return maxX * xScaling;}
	double getMinSceneX() { return minX * xScaling;}
	double getMaxSceneY() { return maxY * yScaling;}
	double getMinSceneY() { return minY * yScaling;}
	double getMaxSceneZ() { return maxZ * ZscalingFactor;}
	double getMinSceneZ() { return minZ * ZscalingFactor;}	
	double minX() {return minX;}
	double maxX() {return maxX;}
	double minY() {return minY;}
	double maxY() {return maxY;}
	double maxZ() {return maxZ;}
	double minZ() {return minZ;}
	double getXOffsetScene() { return xOffset * xScaling;}
	double getYOffsetScene() { return yOffset * yScaling;}
	double getZOffsetScene() { return zOffset * ZscalingFactor;}
	
	
	@Override
	public List<Object3D> getObjects() {
		return getObjects(true, true);
	}
	
	
	
	protected abstract Graph3DDatapoint[][] getGraphDatapoints(double minX, double maxX, double minY, double maxY, int steps);
	
	
	private void initializeZMinMax(Graph3DDatapoint[][] datapoints) {
		if (autoAdjustZ) {
			minZ = datapoints[0][0].z();
			maxZ = minZ;
			for (int x=0; x<datapoints[0].length; x++) {
				for (int y=0; y<datapoints.length; y++) {
					if (datapoints[y][x].z() < minZ) minZ = datapoints[y][x].z();
					if (datapoints[y][x].z() > maxZ) maxZ = datapoints[y][x].z();
				}
			}
		}
	}

	/**
	 * Get all objects to be rendered
	 * @param showAxis whether or not axis are rendered
	 * @param showGrid whether or not the grid is rendered
	 * @return
	 */
	public List<Object3D> getObjects(boolean showAxis, boolean showGrid) {
		Graph3DDatapoint[][] datapoints = getGraphDatapoints(minX, maxX, minY, maxY, steps);
		initializeZMinMax(datapoints);
		
		List<Object3D> objects = new ArrayList<Object3D>();
		
		if (showAxis) {			
			objects.addAll(getAxes(minX, maxX, minY, maxY, minZ , maxZ));
		}
		if (showGrid) {			
			objects.addAll(getGrid(minX, maxX, minY, maxY));
		}
		objects.addAll(getGraph(datapoints));
		
		return objects;
	}

	

	
	private List<Object3D> getAxes(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		List<Object3D> axesObjects = new ArrayList<Object3D>();
		
		double LowestZ = minZ < zOffset ? minZ : zOffset;
		double highestZ = maxZ > zOffset ? maxZ : zOffset;
		
		int steps = 20;
		double stepSize = (maxX-minX)/steps;
		for (double x = minX; x < maxX; x += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(x*xScaling, yOffset*yScaling, zOffset*ZscalingFactor, (x+stepSize)*xScaling, yOffset*yScaling, zOffset*ZscalingFactor, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (maxY-minY)/steps;
		for (double y = minY; y < maxY; y += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset*xScaling, y*yScaling, zOffset*ZscalingFactor, xOffset*xScaling, (y+stepSize)*yScaling, zOffset*ZscalingFactor, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (highestZ-LowestZ)/steps;
		for (double z = LowestZ; z < highestZ; z += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset*xScaling, yOffset*yScaling, z*ZscalingFactor, xOffset*xScaling, yOffset*yScaling, (z+stepSize)*ZscalingFactor, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
		}
		
		
		return axesObjects;
	}
	
	private List<Object3D> getGrid(double minX, double maxX, double minY, double maxY) {
		List<Object3D> gridObjects = new ArrayList<Object3D>();
		
		double xStepSize = (maxX - minX) / steps;
		double yStepSize = (maxY - minY) / steps;
		
		for (double x = minX; x<maxX; x += xStepSize) {
			for (double y=minY; y<maxY; y += yStepSize) {
				Object3D o = Object3DFactory.createPolygon3D(
						new double[] {x*xScaling, x*xScaling, (x+xStepSize)*xScaling, (x+xStepSize)*xScaling}, 
						new double[] {y*yScaling, (y+yStepSize)*yScaling, (y+yStepSize)*yScaling, y*yScaling}, 
						new double[] {zOffset*ZscalingFactor,zOffset*ZscalingFactor,zOffset*ZscalingFactor,zOffset*ZscalingFactor}, 
						new Color(0,0,255,10));
				gridObjects.add(o);
			}
		}
		
		return gridObjects;
	}
	
	

	private List<Object3D> getGraph(Graph3DDatapoint[][] datapoints) {
		List<Object3D> graphObjects = new ArrayList<Object3D>();
		
		for (int x=0; x<datapoints[0].length-1; x++) {
			for (int y=0; y<datapoints.length-1; y++) {
				
				if (!areInBounds(
						Bounds.ANY_WITHIN,
						new Coordinate(datapoints[y][x].x(), datapoints[y][x].y(), datapoints[y][x].z()),
						new Coordinate(datapoints[y+1][x].x(), datapoints[y+1][x].y(), datapoints[y+1][x].z()),
						new Coordinate(datapoints[y][x+1].x(), datapoints[y][x+1].y(), datapoints[y][x+1].z()),
						new Coordinate(datapoints[y+1][x+1].x(), datapoints[y+1][x+1].y(), datapoints[y+1][x+1].z())
						)) continue;
				
				graphObjects.add(
					Object3DFactory.createPolygon3D(
						new double[] {datapoints[y][x].x()*xScaling, datapoints[y+1][x].x()*xScaling, datapoints[y+1][x+1].x()*xScaling, datapoints[y][x+1].x()*xScaling}, 
						new double[] {datapoints[y][x].y()*yScaling, datapoints[y+1][x].y()*yScaling, datapoints[y+1][x+1].y()*yScaling, datapoints[y][x+1].y()*yScaling}, 
						new double[] {datapoints[y][x].z()*ZscalingFactor, datapoints[y+1][x].z()*ZscalingFactor, datapoints[y+1][x+1].z()*ZscalingFactor, datapoints[y][x+1].z()*ZscalingFactor}, 
						new Color(100,100,255,alpha))
					.setColorAdjuster(ColorAdjusterFactory.getType4ColorAdjuster(minZ*ZscalingFactor, maxZ*ZscalingFactor)));
			}
		}
		
		
		return graphObjects;
	}
	
	private enum Bounds {
		ALL_WITHIN,
		ANY_WITHIN
	}
	
	
	
	private boolean areInBounds(Bounds bounds, Coordinate... points) {
		boolean pointsInBounds = false;
		for (Coordinate pair : points) {
			if (isInBounds(pair.x(), pair.y(), pair.z() )) {
				pointsInBounds = true;
				if (bounds == Bounds.ANY_WITHIN) {
					return true;
				}
			} else {
				if (bounds == Bounds.ALL_WITHIN) {					
					return false;
				}
			}
		}
		return pointsInBounds;
	}
	
	private boolean isInBounds(double x, double y, double z) {
		return (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ);
	}
	

}
