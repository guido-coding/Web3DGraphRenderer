package nl.guidobreuer.graph.model.graph3d;

import nl.guidobreuer.graph.model.Graph3DDatapoint;

public class DatasetGraph extends Graph {
	
	private final Graph3DDatapoint[][] data;
	
	public DatasetGraph(Graph3DDatapoint[][] data) {
		if (data == null) throw new IllegalArgumentException("Data cannot be null");
		
		this.data = data;
	}

	@Override
	protected Graph3DDatapoint[][] getGraphDatapoints(double minX, double maxX, double minY, double maxY, int steps,
			double ZscalingFactor) {
		
		Graph3DDatapoint[][] datapoints = new Graph3DDatapoint[data.length][data[0].length];
		
		for (int x=0; x<datapoints[0].length; x++) {
			for (int y=0; y<datapoints.length; y++) {
				datapoints[y][x] = new Graph3DDatapoint(
						 data[y][x].x(),
						 data[y][x].y(),
						 data[y][x].z() * ZscalingFactor
					);
			}
		}
		
		return datapoints;
	}

}
