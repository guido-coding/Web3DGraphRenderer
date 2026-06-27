package render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Object3DFactory {

	
	
	public static Object3D mergeObjects(Object3D[] objects) {
		Point3D location = objects[0].getLocation();
		double diameter = 0;
		for (Object3D o : objects) {
			if (o.diameter > diameter) diameter = o.diameter;
			if (!location.equals(o.getLocation())) throw new IllegalArgumentException("Location of objects must match to be able to merge objects");
		}
		
		Object3D object = new Object3D(location.x, location.y, location.z, diameter);
		
		for (Object3D o : objects) {
			for (Face3D f : o.getFaces()) {
				object.addFace(f);
			}
		}
		
		return object;
	}
	
	public static Object3D createSphere(double x, double y, double z) {
		double r = 5;
		Color color = new Color(100,100,255,255);
		return createSphere(x, y, z, r, color);
	}
	
	public static Object3D createDisk(double x, double y, double z, double innerR, double outerR, Color color) {
		Object3D object = new Object3D(x, y, z, outerR);
		
		int steps = 50;
		Point3D previousOuterPoint = null;
		Point3D previousInnerPoint = null;
		
		for (int i=0; i<=steps; i++) {
			double theta = -1 * Math.PI + i*2*Math.PI/steps;
			//calculate position
			double xpOuter = outerR * Math.cos(theta);
			double ypOuter = outerR * Math.sin(theta);
			Point3D pOuter = new Point3D(xpOuter, ypOuter, z);
			
			double xpInner = innerR * Math.cos(theta);
			double ypInner = innerR * Math.sin(theta);
			Point3D pInner = new Point3D(xpInner, ypInner, z);
			
			if (i != 0) {
				Face3D face = new Face3D(new Point3D[] {
						previousInnerPoint,
						previousOuterPoint,
						pOuter,
						pInner
				});
				face.setColor(color);
				object.addFace(face);
			}
			previousOuterPoint = pOuter;
			previousInnerPoint = pInner;
		}
		
		return object;
	}
	
	public static Object3D createSphere(double x, double y, double z, double r, Color color) {
		
		Object3D object = new Object3D(x, y, z, r);
		
		
		int steps = 20;
		Point3D[] previouspoints = null;
		//Iterate from bottom to top
		for (int i=0; i<=steps; i++) {
			double phi = i*Math.PI/steps;
			
			//interate over horizontal plane
			Point3D[] points = new Point3D[steps+1];
			for (int j=0; j<=steps; j++) {
				double theta = -1 * Math.PI + j*2*Math.PI/steps;
				
				//calculate position
				double xp = r * Math.sin(phi)*Math.cos(theta);
				double yp = r * Math.sin(phi)*Math.sin(theta);
				double zp = r * Math.cos(phi);
				
				points[j] = new Point3D(xp, yp, zp);
			}
			if (i != 0) {
				for (int j=0; j<steps; j++) {
					Face3D face = new Face3D(new Point3D[] {
							previouspoints[j],
							previouspoints[j+1],
							points[j+1],
							points[j]
					});
					face.setColor(color);
					object.addFace(face);
				}
			}
			previouspoints = points;
		}
		
		return object;
	}
	
	
	public static Object3D createPolygon3D(double x[], double y[], double z[], Color color) {
		return createPolygon3D(0,0,0,x, y, z, color);
	}
	
	public static Object3D createPolygon3D(double xRef, double yRef, double zRef, double x[], double y[], double z[], Color color) {
		if (x.length!= y.length || y.length != z.length) {
			throw new IllegalArgumentException("Dimensions of point vectors do not match");
		}
		
		Point3D[] points = new Point3D[x.length];
		for (int i=0; i<points.length; i++) {
			points[i] = new Point3D(x[i], y[i], z[i]);
		}
		
		Face3D face = new Face3D(points);
		face.setColor(color);
		
		
		Object3D object = new Object3D(xRef, yRef, zRef);
		object.addFace(face);
		
		return object;
		
	}
	
	
	public static Object3D createCube(double size, double x, double y, double z, Color color) {
		List<Face3D> faces = new ArrayList<Face3D>();
		Face3D face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x, y+size, z),
				new Point3D(x, y+size, z+size),
				new Point3D(x, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y+size, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x, y+size, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x+size, y, z),
				new Point3D(x+size, y, z+size),
				new Point3D(x, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x+size, y, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x+size, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z),
				new Point3D(x, y+size, z),
				new Point3D(x+size, y+size, z),
				new Point3D(x+size, y, z),
		});
		face.setColor(color);
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(x, y, z+size),
				new Point3D(x, y+size, z+size),
				new Point3D(x+size, y+size, z+size),
				new Point3D(x+size, y, z+size),
		});
		face.setColor(color);
		faces.add(face);
		
		Object3D object = new Object3D();
		for (Face3D f : faces) {			
			object.addFace(f);
		}
		return object;
	}
	
	public static Object3D createCube(double x, double y, double z) {
		List<Face3D> faces = new ArrayList<Face3D>();
		Face3D face = new Face3D(new Point3D[] {
				new Point3D(5 + x, -5 + y, -5+z),
				new Point3D(5 + x, -5 + y, 5+z),
				new Point3D(5 + x, 5 + y, 5+z),
				new Point3D(5 + x, 5 + y, -5+z)
		});
		face.setColor(new Color(255,100,100,200));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, 5+y, -5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, -5+z)
		});
		face.setColor(new Color(255,255,100,200));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(5+x, -5+y, 5+z),
				new Point3D(5+x, -5+y, -5+z)
		});
		face.setColor( new Color(100,255,255,200));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(-5+x, 5+y, -5+z)
		});
		face.setColor( new Color(100,100,255,200));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, -5+z),
				new Point3D(-5+x, 5+y, -5+z),
				new Point3D(5+x, 5+y, -5+z),
				new Point3D(5+x, -5+y, -5+z)
		});
		face.setColor( new Color(100,100,100,200));
		faces.add(face);
		
		face = new Face3D(new Point3D[] {
				new Point3D(-5+x, -5+y, 5+z),
				new Point3D(-5+x, 5+y, 5+z),
				new Point3D(5+x, 5+y, 5+z),
				new Point3D(5+x, -5+y, 5+z)
		});
		face.setColor( new Color(100,100,100,200));
		faces.add(face);
		
		Object3D object = new Object3D();
		for (Face3D f : faces) {			
			object.addFace(f);
		}
		return object;
	}
}
