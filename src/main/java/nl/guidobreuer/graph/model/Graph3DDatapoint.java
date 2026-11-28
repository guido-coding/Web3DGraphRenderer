package nl.guidobreuer.graph.model;

public record Graph3DDatapoint(
		double x,
		double y,
		double z) {

	public Graph3DDatapoint {
		if (Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)) {
			
		} else {
			throw new IllegalStateException("Non finate numebers encountered");
		}
	}
	
}
