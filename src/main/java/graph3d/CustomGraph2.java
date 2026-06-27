package graph3d;

import java.util.HashMap;
import java.util.Map;

import equationparser.EquationParser;
import equationparser.InvalidEquationException;

public class CustomGraph2 extends Graph {
	
	private final EquationParser parser;
	
	public CustomGraph2(String equation) throws InvalidEquationException {
		parser = new EquationParser(equation);
		
		
	}

	@Override
	protected double getZ(double x, double y) {
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
	
	
}
