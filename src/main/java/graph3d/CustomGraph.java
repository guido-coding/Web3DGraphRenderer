package graph3d;

public class CustomGraph extends Graph {
	
	private final double A, B, C, D, E;
	
	
	public CustomGraph(double A, double B, double C, double D, double E) {
		super();
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
		this.E = E;
	}
	
	@Override
	protected double getZ(double x, double y) {
		return A * x*x - B * y*y + C*x + D*y + E;
	}

}
