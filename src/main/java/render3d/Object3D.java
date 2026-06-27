package render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * TODO
 * 
 */

class Point3D {
	//coordinate relative to center of mass of an Object3D
	final double x, y, z;
	
	
	Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//location in spherical coordinates relative to camera
	private double r, theta, phi;
	//private double viewtheta;
	
	synchronized void setSphericalCoordinate(SphericalCoordinate relativeToCam) {
		r = relativeToCam.r;
		theta = relativeToCam.theta;
		phi = relativeToCam.phi;
		//viewtheta = relativeToCam.viewtheta;
	}
	
	synchronized double getR() { return r;}
	synchronized double getTheta() { return theta;}
	synchronized double getPhi() { return phi;}
	//synchronized double getViewTheta() { return viewtheta;}
	
	@Override
	public boolean equals(Object p) {
		if (p instanceof Point3D) {
			Point3D point = (Point3D) p;
			return (x == point.x && y == point.y && point.z == z);
		} else { 
			return false;
		}
	}
	
	
}








class Face3D implements Comparable<Face3D> {
	
	private final List<Point3D> points;
	private Color color;
	private double averageDistance;

	private Object3D parentObject;
	
	
	Face3D(Point3D[] points) {
		this.points = new ArrayList<Point3D>();
		for (Point3D point : points) {
			this.points.add(point);
		}
	}
	
	
	Collection<Point3D> getPoints() {
		return points;
	}
	
	synchronized void setParent(Object3D object) {
		parentObject = object;
	}
	

	
	synchronized void setColor(Color color) {
		this.color = color;
	}
	
	synchronized Color getFillColor() {
		ColorAdjuster colorAdjuster = parentObject.getColorAdjuster();
		if (colorAdjuster != null) {
			return colorAdjuster.getAdjustedColor(color, this, parentObject);
		} else {			
			return color;
		}
	}
	
	private synchronized void updateAverageDistance() {
		double sum = 0;
		for (Point3D point : points) {
			sum += point.getR();
		}
		averageDistance = sum/points.size();
	}
	
	synchronized double getAverageDistance() {
		return averageDistance;
	}

	@Override
	public int compareTo(Face3D o) {
		if (o.getAverageDistance() == getAverageDistance()) return 0; 
		return o.getAverageDistance() <= getAverageDistance() ? -1 : 1;
	}
	
	void updatePositionRelativeToCamera(double xOffset, double yOffset, double zOffset, double camX, double camY, double camZ) {
		for (Point3D point : points) {
			point.setSphericalCoordinate(SphericalCoordinate.updatePositionRelativeToCamera(point.x + xOffset, point.y + yOffset, point.z + zOffset, camX, camY, camZ));
		}
		updateAverageDistance();
	}
	
}








public class Object3D implements Comparable<Object3D> {
	
	private final List<Face3D> faces;
	private double averageDistance;
	private ColorAdjuster colorAdjuster;
	
	private double x, y, z;
	final double diameter;
	
	Object3D() {
		this(0);
	}
	
	Object3D( double diameter) {
		faces = new ArrayList<Face3D>();
		this.diameter = diameter;
	}
	
	Object3D(double x, double y, double z, double diameter) {
		this(diameter);
		setLocation(x, y, z);
	}
	
	Object3D(double x, double y, double z) {
		this (x, y, z, 0);
	}
	
	public synchronized void setColorAdjuster(ColorAdjuster adjuster) {
		colorAdjuster = adjuster;
	}
	
	synchronized ColorAdjuster getColorAdjuster() {
		return colorAdjuster;
	}
	
	synchronized Point3D getLocation() {
		return new Point3D(x, y, z);
	}
	
	public synchronized void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	synchronized Collection<Face3D> getFaces() {
		return faces;
	}
	
	synchronized void addFace(Face3D face) {
		faces.add(face);
		face.setParent(this);
	}
	
	private synchronized void updateAverageDistance() {
		double sum = 0;
		for (Face3D face : faces) {
			sum += face.getAverageDistance();
		}
		averageDistance = sum / faces.size();
	}
	
	
	synchronized double getAverageDistance() {
		return averageDistance;
	}

	@Override
	public int compareTo(Object3D o) {
		if (o.getAverageDistance() == getAverageDistance()) return 0; 
		return o.getAverageDistance() <= getAverageDistance() ? -1 : 1;
	}

	
	synchronized void updatePositionRelativeToCamera(double x, double y, double z) {
		for (Face3D face : faces) {
			face.updatePositionRelativeToCamera(this.x, this.y, this.z, x, y, z);
		}
		updateAverageDistance();
		Collections.sort(faces);

	}
	
	
	
	
}