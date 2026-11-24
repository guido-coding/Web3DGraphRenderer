package nl.guidobreuer.graph.model.graph3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import nl.guidobreuer.graph.model.Graph3DDatapoint;
import render3d.ColorAdjusterFactory;
import render3d.Object3D;
import render3d.Object3DFactory;
import render3d.Point3D;

/*
 * TODO refactor ZscalingFactor
 */
abstract class Graph implements Graph3DObject {
	
	public static final int AXIS_TO_GRAPH_SIZE = 200;
	public static final int STANDARD_AXIS_DIMENSION = 10;
	
	private double minX, minY, maxX, maxY;
	private double xScaling, yScaling;
	private double minZ, maxZ;
	double ZscalingFactor;
	protected boolean autoAdjustZ = true;
	protected int alpha = 200;
	protected double yOffset, xOffset, zOffset;
	int steps;
	


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
		xScaling = STANDARD_AXIS_DIMENSION / (maxX - minX);
		yScaling = STANDARD_AXIS_DIMENSION / (maxY - minY);
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
	
	record Coordinate(double x, double y, double z) {};
	
	Coordinate getCenter() {
		double averageX = (maxX + minX)/2;
		double averageY = (maxY + minY)/2;
		double averageZ = (maxZ + minZ)/2;
		return new Coordinate(
				averageX*xScaling, 
				averageY*yScaling, 
				averageZ);
	}
	
	double getMaxSceneX() { return maxX * xScaling;}
	double getMinSceneX() { return minX * xScaling;}
	double getMaxSceneY() { return maxY * yScaling;}
	double getMinSceneY() { return minY * yScaling;}
	double minX() {return minX;}
	double maxX() {return maxX;}
	double minY() {return minY;}
	double maxY() {return maxY;}
	double maxZ() {return maxZ;}
	double minZ() {return minZ;}
	
	
	
	@Override
	public List<Object3D> getObjects() {
		return getObjects(true, true);
	}
	
	
	
	protected abstract Graph3DDatapoint[][] getGraphDatapoints(double minX, double maxX, double minY, double maxY, int steps, double ZscalingFactor);
	
	
	private void initializeZMinMax(Graph3DDatapoint[][] datapoints) {
		if (!autoAdjustZ) {
			minZ = minZ * ZscalingFactor;
			maxZ = maxZ * ZscalingFactor;
			return;
		}
		minZ = datapoints[0][0].z();
		maxZ = minZ;
		for (int x=0; x<datapoints[0].length; x++) {
			for (int y=0; y<datapoints.length; y++) {
				if (datapoints[y][x].z() < minZ) minZ = datapoints[y][x].z();
				if (datapoints[y][x].z() > maxZ) maxZ = datapoints[y][x].z();
			}
		}
		//System.out.println("min: " + minZ + "; max: " + maxZ);
	}

	public List<Object3D> getObjects(boolean showAxis, boolean showGrid) {
		Graph3DDatapoint[][] datapoints = getGraphDatapoints(minX, maxX, minY, maxY, steps, ZscalingFactor);
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
		
		/*
		double xDim = maxX - minX;
		double yDim = maxY - minY;
		double maxDim = xDim > yDim ? xDim : yDim;
		*/
		double LowestZ = minZ < zOffset ? minZ : zOffset;
		double highestZ = maxZ > zOffset ? maxZ : zOffset;
		
		int steps = 20;
		double stepSize = (maxX-minX)/steps;
		for (double x = minX; x < maxX; x += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(x*xScaling, yOffset*yScaling, zOffset, (x+stepSize)*xScaling, yOffset*yScaling, zOffset, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (maxY-minY)/steps;
		for (double y = minY; y < maxY; y += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset*xScaling, y*yScaling, zOffset, xOffset*xScaling, (y+stepSize)*yScaling, zOffset, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (highestZ-LowestZ)/steps;
		for (double z = LowestZ; z < highestZ; z += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset*xScaling, yOffset*yScaling, z, xOffset*xScaling, yOffset*yScaling, z+stepSize, STANDARD_AXIS_DIMENSION/AXIS_TO_GRAPH_SIZE));
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
						new double[] {0+zOffset,0+zOffset,0+zOffset,0+zOffset}, 
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
						new Pair(datapoints[y][x].x(), datapoints[y][x].y()),
						new Pair(datapoints[y+1][x].x(), datapoints[y+1][x].y()),
						new Pair(datapoints[y][x+1].x(), datapoints[y][x+1].y()),
						new Pair(datapoints[y+1][x+1].x(), datapoints[y+1][x+1].y())
						)) continue;
				
				graphObjects.add(
					Object3DFactory.createPolygon3D(
						new double[] {datapoints[y][x].x()*xScaling, datapoints[y+1][x].x()*xScaling, datapoints[y+1][x+1].x()*xScaling, datapoints[y][x+1].x()*xScaling}, 
						new double[] {datapoints[y][x].y()*yScaling, datapoints[y+1][x].y()*yScaling, datapoints[y+1][x+1].y()*yScaling, datapoints[y][x+1].y()*yScaling}, 
						new double[] {datapoints[y][x].z(), datapoints[y+1][x].z(), datapoints[y+1][x+1].z(), datapoints[y][x+1].z()}, 
						new Color(100,100,255,alpha))
					.setColorAdjuster(ColorAdjusterFactory.getType4ColorAdjuster(minZ, maxZ)));
			}
		}
		
		
		return graphObjects;
	}
	
	private enum Bounds {
		ALL_WITHIN,
		ANY_WITHIN
	}
	
	
	
	private record Pair(double x, double y) {}
	
	private boolean areInBounds(Bounds bounds, Pair... points) {
		boolean pointsInBounds = false;
		for (Pair pair : points) {
			if (isInBounds(pair.x(), pair.y() )) {
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
	
	private boolean isInBounds(double x, double y) {
		return (x > minX && x < maxX && y > minY && y < maxY);
	}
	

}
