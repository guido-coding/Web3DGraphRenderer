package nl.guidobreuer.graph.model;

import java.util.HashMap;
import java.util.Map;

import equationparser.EquationParser;
import equationparser.InvalidEquationException;

/**
 * Implementation of Graph to render graphs based on equations as input.
 */
public class CustomGraph extends Graph {
	
	private final EquationParser parser;
	
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
	/**
	 * Calculate grid for x-y pairs using the min/max values and number of setps.
	 * Calculate the z value for each x-y pair using the provided equation.
	 */
	protected Graph3DDatapoint[][] getGraphDatapoints(double minX, double maxX, double minY, double maxY, int steps) {

		Graph3DDatapoint[][] datapoints = new Graph3DDatapoint[steps+1][steps+1];

		for (int x=0; x<=steps; x++) {
			for (int y=0; y<=steps; y++) {
				
				double xValue = toX(x, minX, maxX, steps);
				double yValue = toY(y, minY, maxY, steps);
				
				datapoints[y][x] = new Graph3DDatapoint(
						xValue, 
						yValue, 
						getZ(xValue, yValue));

			}
		}
		
		return datapoints;
	}
	
	
	private double toX(int x, double minX, double maxX, int steps) {
		return minX + (maxX-minX)*x/(steps);
	}
	
	private double toY(int y, double minY, double maxY, int steps) {
		return minY + (maxY-minY)*y/(steps);
	}
	
}
