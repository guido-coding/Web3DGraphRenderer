package graph3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import render3d.ColorAdjusterFactory;
import render3d.Object3D;
import render3d.Object3DFactory;

abstract class Graph implements Graph3DObject {
	
	public static final int AXIS_TO_GRAPH_SIZE = 200;
	
	protected double minX, minY, maxX, maxY, minZ, maxZ, ZscalingFactor;
	protected boolean autoAdjustZ = true;
	protected int alpha = 200;
	protected double yOffset, xOffset, zOffset;
	protected int steps;
	
	
	private double[][] zValues;

	Graph() {
		minX = -10;
		minY = -10;
		maxX = 10;
		maxY = 10;
		ZscalingFactor = 1;
		yOffset = 0;
		xOffset = 0;
		zOffset = 0;
	}
	
	@Override
	public List<Object3D> getObjects() {
		return getObjects(true, true);
	}
	

	public List<Object3D> getObjects(boolean showAxis, boolean showGrid) {
		initializeZValues();
		
		List<Object3D> objects = new ArrayList<Object3D>();
		
		if (showAxis) {			
			objects.addAll(getAxes(minX, maxX, minY, maxY, minZ , maxZ));
		}
		if (showGrid) {			
			objects.addAll(getGrid(minX, maxX, minY, maxY));
		}
		objects.addAll(getGraph());
		
		return objects;
	}

	
	private void initializeZValues() {
		zValues = new double[steps+1][steps+1];
		
		if (autoAdjustZ) {			
			minZ = ZscalingFactor*getZ(
					toX(0),
					toY(0));
			maxZ = minZ;
		}
		
		for (int x=0; x<zValues.length; x++) {
			for (int y=0; y<zValues[0].length; y++) {
				zValues[x][y] = ZscalingFactor*getZ(
						toX(x),
						toY(y));
				if (autoAdjustZ) {					
					if (zValues[x][y] < minZ) minZ = zValues[x][y];
					if (zValues[x][y] > maxZ) maxZ = zValues[x][y];
				}
			}
		}
	}
	
	private double toX(int x) {
		return minX + (maxX-minX)*x/(zValues.length-1);
	}
	
	private double toY(int y) {
		return minY + (maxY-minY)*y/(zValues[0].length-1);
	}
	
	
	abstract protected double getZ(double x, double y);
	

	
	private List<Object3D> getAxes(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
		List<Object3D> axesObjects = new ArrayList<Object3D>();
		
		
		double xDim = maxX - minX;
		double yDim = maxY - minY;
		double maxDim = xDim > yDim ? xDim : yDim;
		
		double LowestZ = minZ < zOffset ? minZ : zOffset;
		double highestZ = maxZ > zOffset ? maxZ : zOffset;
		
		/*
		axesObjects.add(Object3DFactory.createRectangularPrism(minX, yOffset, zOffset, maxX, yOffset, zOffset, maxDim/AXIS_TO_GRAPH_SIZE));
		axesObjects.add(Object3DFactory.createRectangularPrism(xOffset, minY, zOffset, xOffset, maxY, zOffset, maxDim/AXIS_TO_GRAPH_SIZE));
		axesObjects.add(Object3DFactory.createRectangularPrism(xOffset, yOffset, LowestZ, xOffset, yOffset, highestZ, maxDim/AXIS_TO_GRAPH_SIZE));
		*/
		
		
		int steps = 20;
		double stepSize = (maxX-minX)/steps;
		for (double x = minX; x < maxX; x += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(x, yOffset, zOffset, x+stepSize, yOffset, zOffset, maxDim/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (maxY-minY)/steps;
		for (double y = minY; y < maxY; y += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset, y, zOffset, xOffset, y+stepSize, zOffset, maxDim/AXIS_TO_GRAPH_SIZE));
		}
		stepSize = (highestZ-LowestZ)/steps;
		for (double z = LowestZ; z < highestZ; z += stepSize) {
			axesObjects.add(Object3DFactory.createRectangularPrism(xOffset, yOffset, z, xOffset, yOffset, z+stepSize, maxDim/AXIS_TO_GRAPH_SIZE));
		}
		
		
		/*
		double stepSize = maxDim/AXIS_TO_GRAPH_SIZE;
		for (double x = minX; x < maxX; x += stepSize) {
			Object3D o = Object3DFactory.createCube(stepSize, x, 0 + yOffset, 0 + zOffset, Color.DARK_GRAY);
			axesObjects.add(o);
		}
		for (double y = minY; y < maxY; y += stepSize) {
			Object3D o = Object3DFactory.createCube(stepSize, 0 + xOffset, y, 0 + zOffset, Color.DARK_GRAY);
			axesObjects.add(o);
		}
		

		for (double z = LowestZ; z < highestZ; z += stepSize) {
			Object3D o = Object3DFactory.createCube(stepSize, 0 + xOffset, 0 + yOffset, z, Color.DARK_GRAY);
			axesObjects.add(o);
		}
		*/
		
		return axesObjects;
	}
	
	private List<Object3D> getGrid(double minX, double maxX, double minY, double maxY) {
		List<Object3D> gridObjects = new ArrayList<Object3D>();
		
		//minX = Math.round(minX);
		//minY = Math.round(minY);
		
		double xStepSize = (maxX - minX) / steps;
		double yStepSize = (maxY - minY) / steps;
		
		for (double x = minX; x<maxX; x += xStepSize) {
			for (double y=minY; y<maxY; y += yStepSize) {
				Object3D o = Object3DFactory.createPolygon3D(
						new double[] {x, x, x+xStepSize, x+xStepSize}, 
						new double[] {y, y+yStepSize, y+yStepSize, y}, 
						new double[] {0+zOffset,0+zOffset,0+zOffset,0+zOffset}, 
						new Color(0,0,255,10));
				gridObjects.add(o);
			}
		}
		
		return gridObjects;
	}
	
	private List<Object3D> getGraph() {
		List<Object3D> graphObjects = new ArrayList<Object3D>();
		
		for (int x=0; x<zValues.length-1; x++) {
			for (int y=0; y<zValues[0].length-1; y++) {
				Object3D o = Object3DFactory.createPolygon3D(
						new double[] {toX(x), toX(x), toX(x+1), toX(x+1)}, 
						new double[] {toY(y), toY(y+1), toY(y+1), toY(y)}, 
						new double[] {zValues[x][y],zValues[x][y+1],zValues[x+1][y+1],zValues[x+1][y]}, 
						new Color(100,100,255,alpha));
				o.setColorAdjuster(ColorAdjusterFactory.getType4ColorAdjuster(minZ, maxZ));
				graphObjects.add(o);
			}
		}
		
		return graphObjects;
	}
	

}
