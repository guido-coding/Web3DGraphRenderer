package nl.guidobreuer.graph.model.graph3d;

import java.util.HashMap;
import java.util.Map;

import equationparser.EquationParser;
import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.model.Graph3DDatapoint;


public class CustomGraph extends Graph {
	
	private final EquationParser parser;
	
	//private double[][] zValues;
	
	public CustomGraph(String equation) throws InvalidEquationException {
		parser = new EquationParser(equation);
		
		
	}

	private double getZ(double x, double y) {
		Map<String, Double> varValues = new HashMap<String, Double>();
		varValues.put("x", x);
		varValues.put("y", y);
		try {
			double value = parser.resolveEquation(varValues);
			if (Double.isFinite(value)) {
				return value;
			} else {
				return 0;
			}
		} catch (InvalidEquationException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	

	@Override
	protected Graph3DDatapoint[][] getGraphDatapoints(double minX, double maxX, double minY, double maxY, int steps, double ZscalingFactor) {

		Graph3DDatapoint[][] datapoints = new Graph3DDatapoint[steps+1][steps+1];

		for (int x=0; x<=steps; x++) {
			for (int y=0; y<=steps; y++) {
				
				double xValue = toX(x, minX, maxX, steps);
				double yValue = toY(y, minY, maxY, steps);
				
				datapoints[y][x] = new Graph3DDatapoint(
						xValue, 
						yValue, 
						ZscalingFactor*getZ(xValue, yValue));

			}
		}
		
		return datapoints;
	}
	
	/*
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
	*/
	
	private double toX(int x, double minX, double maxX, int steps) {
		return minX + (maxX-minX)*x/(steps);
	}
	
	private double toY(int y, double minY, double maxY, int steps) {
		return minY + (maxY-minY)*y/(steps);
	}
	
}
