package render3d;

/*
 * TODO

 * 
 */

class Camera {
	private double x, y, z;
	private double theta, viewphi, r;
	private Point3D focalPoint = new Point3D(0,0,0);
	private double viewangle = 0.1 * Math.PI*2;
	//private double viewtheta;
	
	
	Camera(double x, double y, double z) {		
		setLocation(x,y,z);
	}
	
	Camera() {
		this(50,0,0);
	}
	
	synchronized double getX() {
		return x;
	}
	
	synchronized double getY() {
		return y;
	}
	
	synchronized double getZ() {
		return z;
	}
	
	/**
	 * 
	 * @return camera angle relative to focal point in the horizontal plane
	 */
	synchronized double getTheta() {
		return theta;
	}
	
	/**
	 * 
	 * @return camera angle relative to the focal point in the vertical plane
	 */
	synchronized double getPhi() {
		return viewphi;
	}
	
	synchronized void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		updateAngles();
	}
	
	synchronized void setViewAngle(double viewangle) {
		this.viewangle = viewangle;
	}
	
	private synchronized double getViewAngle() {
		return viewangle;
	}
	
	synchronized void centerOnOrigin() {
		centerOn(new Point3D(0,0,0));
	}
	
	synchronized void centerOn(Point3D point) {
		focalPoint = point;
		updateAngles();
	}
	
	private synchronized void updateAngles() {
		double deltaX = focalPoint.x - this.x;
		double deltaY = focalPoint.y - this.y;
		double deltaZ = focalPoint.z - this.z;
		
		r = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ); //distance from camera to focal point
		viewphi = Math.acos(deltaZ / r); // vertical camera angle to focal point
		
		theta = Math.atan2(deltaY, deltaX); //horizontal camera angle to focal point (angle in horizontal plane)
		//viewtheta = Math.atan2(deltaY, Math.sqrt(deltaZ*deltaZ + deltaX*deltaX));
		
		if (viewphi < 0) viewphi += 2* Math.PI;
		//if (viewtheta < 0) viewtheta += 2* Math.PI;
	}
	
	
	
	
	
	
	private double getVerticalCameraAngle(Point3D point, Object3D object) {
		//instead of calculating the vertical angle to the point, the vertical angle with the horizontal plane of view is needed.
		//therefore the point needs to be transposed perpendicular to the viewing direction such that it is exactly in the center of the horizontal view plane.
		//a triangle can be made between the camera, point, and this transposed point, as the transposed point is perpendicular to the viewing direction, the angle between the camera, transposedpoint, object is 90 degrees.
		//the angle from the camera to the ponit can be calculated using the cosine rule using a triangle from the origin  object and camera. The length of all those sides can be calculated
		//using that angle and the length from the object to the camera and using the 90degrees angle, the length from the camera to the transposed point can be culculated
		//the ratio between that length and the length of the origin to the camera can be used to calculate the x and y coordinate of the transposed point
		//the z coordinate remains the same
		//the vertical angle to that point can than be calcualted.
		//the vertical camera angle can be subtracted to get the vertical viewing angle
		
		Point3D p = object.getLocation();
		double dx = (point.x+p.x) - this.x; //distance from object to camera in x direction
		double dy = (point.y + p.y)  - this.y; //distance from object to camera in y direction
		double C = Math.sqrt(dx*dx + dy*dy); //distance from camera to point in horizontal plane
		
		double camdX = focalPoint.x - x;
		double camdY = focalPoint.y - y;
		double B = Math.sqrt(camdX*camdX + camdY*camdY); //distance from camera to origin (point of focus for camera) in horizontal plane
		
		double objdx = focalPoint.x - (point.x + p.x);
		double objdy = focalPoint.y - (point.y + p.y);
		double A = Math.sqrt(objdx*objdx + objdy*objdy); //distance from origin to point in horizontal plane
		
		//double alpha = Math.acos((C*C+B*B-A*A)/(2*B*C)); //angle between camera and object in horizontal plane
		//double transposeFactor = Math.cos(alpha)* C/B; //distance from transposed object position to camera / distance from camera to origin
		//Above the actual equations for the angle and transpose factor but the inverse cosine and cosine can be cancelled
		double alpha = (C*C+B*B-A*A)/(2*B*C); //angle between camera and object in horizontal plane
		double transposeFactor = alpha* C/B; //distance from transposed object position to camera / distance from camera to origin
		
		//alpha and therefore transposeFactor will return NaN in cases where the point is exactly in the center of the horizontal view plane
		if (Double.isNaN(transposeFactor)) {
			transposeFactor = 1;
		}
		
		//adjusted x and y location of object in line of sight 
		double correctedXObject = this.x - transposeFactor * (this.x - focalPoint.x);
		double correctedYObject = this.y - transposeFactor * (this.y - focalPoint.y);
		
		
		//calculate vertical angle of transposed object
		double deltaX = correctedXObject - this.x;
		double deltaY = correctedYObject - this.y;		
		double deltaZ = (point.z + p.z) - this.z;
		double correctedR = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
		double correctedPhi = Math.acos(deltaZ / correctedR);
		
		return (correctedPhi - viewphi);
	}
	
	private double getTotalCameraAngle(Point3D point, Object3D object) {
		//Calculate total camera angle (horizontal + vertical screen component)
		//angle alpha = arccos (C^2 + B^2  -A^2 / 2 BC) Law of cosines
		double B = r;//Math.sqrt(x*x + y*y + z*z); //distance from camera to origin (point of focus for camera)
		double C = point.getR(); //distance from camera to point
		
		Point3D p = object.getLocation();
		double x = focalPoint.x - (point.x + p.x);
		double y = focalPoint.y - (point.y + p.y);
		double z = focalPoint.z - (point.z + p.z);
		
		double A = Math.sqrt(x*x + y*y + z*z); //distance from origin to point
		
		double alpha = Math.acos((C*C+B*B-A*A)/(2*B*C));
		return alpha;
	}
	

	
	synchronized boolean isBehind(Point3D point, Object3D object) {
		double check = (point.getTheta() - theta)/Math.PI;
		if (check > 2) check -= 2;
		if (check < 0) check += 2;
		return (check > 0.5 && check < 1.5); // point is past vertical plane
	}
	
	
	synchronized RelativeScreenCoordinate getRelativeScreenCoordinate(Point3D point) {
		return getRelativeScreenCoordinate(point,  new Object3D(0,0,0));
	}
	
	
	/**
	 * calculates where the object should be rendered on the screen. 
	 * Calculated on a scale of 0 to 1 in which 0 is on the left or top and 1 is on the right or bottom. 
	 * < 0 or > 1 is offscreen.
	 *  
	 * @param point
	 * @param object
	 * @return
	 */
	synchronized RelativeScreenCoordinate getRelativeScreenCoordinate(Point3D point, Object3D object) {
		
		
		//Point3D p = object.getLocation();
		double viewangle = getViewAngle();

		double totalCameraAngle = getTotalCameraAngle(point, object);
		double relativeR = totalCameraAngle / viewangle;
		
		//calculate vertical screen component
		double vangle = getVerticalCameraAngle(point, object)/viewangle; //negative if up, positive if doown

		//check if past vertical plane and correct angle accordingly
		double check = (point.getTheta() - theta)/Math.PI;
		if (check > 2) check -= 2;
		if (check < 0) check += 2;
		if (check > 0.5 && check < 1.5) { // point is past vertical plane
			if (viewphi > 0.5*Math.PI) { // looking down
				if (point.z + object.getLocation().z < this.z) { //only correct when looking down and object is below							
					vangle += 2*(((Math.PI - viewphi) / viewangle) - vangle);
				}
			} else { //looking up
				if (point.z + object.getLocation().z > this.z) { //only correct when looking up and object is above					
					vangle -= 2  * (viewphi/viewangle + vangle);
				}
			}
			
		} 

		
		double angleToLeft = (point.getTheta() < theta) ? theta - point.getTheta() : theta - point.getTheta() + 2*Math.PI;
		
		//calculate horizontal screen component
		double r2 = relativeR*relativeR;
		double v2 = vangle*vangle;
		double xangle = Math.sqrt(r2 - v2);
		//r2-v2 should be >= 0 and should be 0 when in the center of the screen.  
		//but due to floating point impression it can result in very tiny negative numbers resulting in the square root to return NaN
		//therefore check and set to 0 if this is the case
		if (Double.isNaN(xangle)) {
			//System.err.print("Camera.class: xangle NaN corrected. " + angleToLeft + " ");
			
			if (Math.abs(angleToLeft) < 0.05 || Math.abs(angleToLeft - 2*Math.PI)< 0.05 ) {//point is in the center
				xangle = 0;
				//System.err.print(" case A");
			} else if (angleToLeft < Math.PI) { //point is offscreen to the left
				xangle = 1; 
				//System.err.print(" case B");
			} else if (angleToLeft > Math.PI) { // point is offscreen to the right
				xangle = 1;
				//System.err.print(" case C");
			}
			//System.err.println();
		}
		
		
		//TODO
		//correct horizontal angle for points that are directly to the left or right of the camera
		if (
				(Math.abs(angleToLeft/Math.PI - 0.5) < 0.00001 || Math.abs(angleToLeft/Math.PI - 1.5) < 0.000001) 
						&& xangle >= 0 && xangle <= 0.5) {
			xangle = 1;
			//System.err.println("Camera.class: angle perpendicular to camera view corrected.");
		}
		
		
		
		//Adjust angle to relative screen coordinate
		if (angleToLeft < 0 || angleToLeft > Math.PI) {
			xangle = 0.5 + xangle; // object on right hand side
		} else {
			xangle = 0.5 - xangle; //object on left hand side
		}
		
		//System.out.println(xangle);
		vangle = vangle+0.5; //center vertically
		
		return new RelativeScreenCoordinate(xangle, vangle);
	}
	

}









class RelativeScreenCoordinate {
	final double x, y;
	
	RelativeScreenCoordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "x: " + x + " y: " + y; 
	}
}
