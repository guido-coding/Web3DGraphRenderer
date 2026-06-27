package render3d;


public class CameraMovement {
	
	private double theta, phi, r;
	private final Scene scene;

	public enum MoveDirection {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
	}
	
	CameraMovement(Scene scene) {
		r = 10;
		this.scene = scene;
	}
	
	public void initializeAngles(double deltaX, double deltaY, double deltaZ) {
		SphericalCoordinate coor = SphericalCoordinate.toSphericalCoordinate(deltaX, deltaY, deltaZ);
		theta = coor.theta;
		phi = coor.phi;
		updateFocalPoint();
	}
	
	public void setVerticalViewAngle(double phi) {
		this.phi = phi;
		updateFocalPoint();
	}
	
	public void setHorizontalViewAngle(double theta) {
		this.theta = theta;
		updateFocalPoint();
	}
	
	public void advanceHorizontalViewAngle(double theta) {
		this.theta += theta;
		updateFocalPoint();
	}
	
	public Coordinate2D nextMovePoint(double distance) {
		return nextMovePoint(distance, MoveDirection.FORWARD);
	}
	
	public Coordinate2D getCurrentCameraPosition() {
		return new Coordinate2D(scene.getCameraLocation().x, scene.getCameraLocation().y);
	}
	
	public Coordinate2D nextMovePoint(double distance, MoveDirection direction) {
		
		double offset = 0;
		switch (direction) {
		case BACKWARD:
			offset = Math.PI;
			break;
		case LEFT:
			offset = -0.5 * Math.PI;
			break;
		case RIGHT:
			offset = 0.5 * Math.PI;
			break;
		default:
			break;
		}
		
		Point3D camLocation = scene.getCameraLocation();
		
		double x = distance * Math.cos(theta+offset) + camLocation.x;
		double y = distance * Math.sin(theta+offset) + camLocation.y;
		
		return new Coordinate2D(x,y);
	}
	
	public void moveCameraTo(double x, double y, double z) {
		scene.setCamera(x, y, z);
		updateFocalPoint();
	}
	
	private void updateFocalPoint() {
		Point3D camLocation = scene.getCameraLocation();
		
		Point3D focalPoint = SphericalCoordinate.toCartesianCoordinate(r, theta, phi);
		scene.focusOn(
				focalPoint.x + camLocation.x, 
				focalPoint.y + camLocation.y, 
				focalPoint.z + camLocation.z);
	}
	
	
	
	public static class Point2D {
		
		public final double x, y;
		
		Point2D (double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return "x: " + x + "; y: " + y;
		}
	}
	
	public static record Coordinate2D(double x, double y) {}
	
}
