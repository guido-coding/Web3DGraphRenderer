package render3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;




/*
 * TODO
 *  
 */

public class Scene {
	
	private final Camera cam;
	private final List<Object3D> objects;
	private CameraOrbit orbit;
	private Background background;
	
	private BufferedImage view;
	
	private volatile boolean hasShutDown = false;
	private final ExecutorService exec = 
			//Executors.newSingleThreadExecutor();
			Executors.newCachedThreadPool();
	/*
	 * Drawing thread should be a single thread to ensure correct sequence of drawing polygons and to avoid thread safety issues of Graphics2D (will deadlock when used by multiple threads)
	 */
	private final ExecutorService drawingExec = 
			//Executors.newFixedThreadPool(6);
			Executors.newSingleThreadExecutor();
	
	/*
	private final ThreadLocal<Graphics2D> graphicsObjects = new ThreadLocal<Graphics2D>();
	private final ThreadLocal<Integer> frameCounter = new ThreadLocal<Integer>();
	private int currentFrame = 0;
	*/
	
	private static final double RADIANS_PER_PIXEL = (0.08*2*Math.PI)/1000;
	public double MIN_DRAW_DISTANCE = 0;
	public double MAX_DRAW_DISTANCE = 100;
	public boolean REJECT_FACES_BEHIND = true;
	public boolean DRAW_POLYGON_COUNTOUR = false;
	public boolean ANTI_ALIAS = true;
	
	
	public Scene() {
		cam = new Camera();
		objects = new CopyOnWriteArrayList<Object3D>();
	}
	
	public CameraMovement createCameraMovement() {
		return new CameraMovement(this);
	}
	
	public void setBackground(String filename) throws IOException {
		background = new MovingBackground(filename);
		//background = new ColoredBackground(new Color(0,0,100));
	}
	
	public void setBackground(Background background) {
		this.background = background;
	}
	
	/**
	 * sets parameters for camera orbit in which the camera orbits around a point in cartesian coordinates
	 * @param r radius of sphere
	 * @param orbitAroundX
	 * @param orbitAroundY
	 * @param orbitAroundZ
	 */
	public void setCameraOrbit(double r, double orbitAroundX, double orbitAroundY, double orbitAroundZ) {
		orbit = new CameraOrbit(cam, r, new Point3D(orbitAroundX,orbitAroundY,orbitAroundZ));
	}
	
	public void cancelCameraOrbit() {
		orbit = null;
	}
	
	/**
	 * Sets additional orbit parameters
	 * @param thetaStepSize step size (speed) at which camera orbits in the horizontal plane. Step size is in radians per step. step size typically is << 1.
	 * @param phiStepSize step size (speed) at which the camera orbits in the vertical plane. Step size is in radians per step. step size typically is << 1.
	 * @param minPhi tilt angle in which the camera orbits in the vertical plane. 0.5 is in the fully horizontal plane. 0 is fully in the vertical plane.
	 */
	public void setOrbitParameters(double thetaStepSize, double phiStepSize, double minPhi) {
		orbit.setOrbitParameters(thetaStepSize, phiStepSize, minPhi);
	}
	
	/**
	 * Sets the camera focal point, the point to which the camera is pointed too. camera angles will be automatically updated if the camera location changes.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void focusOn(double x, double y, double z) {
		cam.centerOn(new Point3D(x,y,z));
	}
	
	
	
	public void setCamera(double x, double y, double z) {
		cam.setLocation(x, y, z);
	}
	
	public void setCamera(double x, double y, double z, double r, double theta, double phi) {
		Point3D p = SphericalCoordinate.toCartesianCoordinate(r, theta, phi);
		cam.setLocation(x+p.x, y+p.y, z+p.z);
		cam.centerOn(new Point3D(x,y,z));
	}
	
	public Point3D getCameraLocation() {
		return new Point3D(cam.getX(), cam.getY(), cam.getZ());
	}
	
	public void close() {
		exec.shutdownNow();
		drawingExec.shutdownNow();
		hasShutDown = true;
	}
	
	
	public void addObject(Object3D object) {
		objects.add(object);
	}
	
	public void addObjects(Collection<Object3D> objects) {
		this.objects.addAll(objects);
	}
	
	public void removeObject(Object3D object) {
		objects.remove(object);
	}
	
	
	
	
	public Point getLocationOnScreen(int width, int height, double x, double y, double z) {
		Point3D point = new Point3D(x,y,z);
		point.setSphericalCoordinate(SphericalCoordinate.updatePositionRelativeToCamera(point.x, point.y, point.z, cam.getX(), cam.getY(), cam.getZ()));
		RelativeScreenCoordinate c = cam.getRelativeScreenCoordinate(point);
		
		int maxDimension = Math.max(width, height);
		int xOffset = (width - maxDimension) / 2;
		int yOffset = (height - maxDimension) / 2;
		
		int xP = (int)(maxDimension * c.x + xOffset); 
		int yP = (int)(maxDimension * c.y + yOffset);
		
		return new Point(xP, yP);
	}
	
	
	
	/**
	 * update the camera location according to its orbit
	 */
	public void updateScene() {
		if (orbit != null) {			
			orbit.updateCamera();
		}
	}
	
	/**
	 * updates the camera position and then returns a rendered image
	 * @param width image width
	 * @param height height
	 * @return rendered image
	 */
	public BufferedImage next3DView(final int width, final int height) {
		updateScene();
		return get3DView(width, height);
	}
	
	/**
	 * returns a rendered image without updating camera position
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage get3DView(final int width, final int height) {
		if (hasShutDown) {
			throw new IllegalStateException("Scene has shut down");
		}
		
		if (view == null || view.getWidth() != width || view.getHeight() != height) {			
			view = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
		
		final int maxDimension = Math.max(width, height);
		final int xOffset = (width - maxDimension) / 2;
		final int yOffset = (height - maxDimension) / 2;
		cam.setViewAngle(maxDimension * RADIANS_PER_PIXEL);
		
		
		final Graphics2D g = view.createGraphics();
		if (ANTI_ALIAS) {			
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		drawingExec.submit(() -> {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			
			if (background != null) {
				BufferedImage bg = background.getBackground(cam.getTheta(), cam.getPhi(), width * RADIANS_PER_PIXEL, height*RADIANS_PER_PIXEL);
				g.drawImage(bg,
						0, 0 , width, height,
						0, 0, bg.getWidth(), bg.getHeight(),
						null);
			}
			
		});
		
		
		//Calculate relative location of all objects to camera
		//copy reference to list in case objects are added/remove concurrently
		final List<Object3D> objects = this.objects;
		final CountDownLatch latch = new CountDownLatch(objects.size());

		for (final Object3D object : objects) {
			exec.submit(() -> {				
				try {					
					object.updatePositionRelativeToCamera(cam.getX(), cam.getY(), cam.getZ());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {					
					latch.countDown();
				}
			});
		}
		try {
			latch.await();
		} catch (InterruptedException e) {}

		//Sort objects based on distance
		Collections.sort(objects);
		
		//draw all objects (farest away first)
		
		
		final CountDownLatch drawingLatches = new CountDownLatch(objects.size());
		
		for (final Object3D object : objects) {
			
			//draw each face of object (fartest away first)
			final ConcurrentHashMap<Face3D, DrawingTask> map = new ConcurrentHashMap<Face3D, DrawingTask>();	
			final CountDownLatch latchFaces = new CountDownLatch(object.getFaces().size());
			
			//Reject tasks that are not within the drawing distance range
			if (object.getAverageDistance() < MIN_DRAW_DISTANCE || (object.getAverageDistance() > MAX_DRAW_DISTANCE && MAX_DRAW_DISTANCE > 0)) {
				drawingLatches.countDown();
				continue;
			}
			
			for (final Face3D face : object.getFaces()) {	
				
				exec.submit(() -> {
					try {						
						//calculate polygon coordinates
						Polygon p = new Polygon();
						
						boolean hasPointOnLeft = false;
						boolean hasPointOnRight = false;
						boolean hasPointOnTop = false;
						boolean hasPointOnBottom = false;
						boolean xInView = false;
						boolean yInView = false;
						boolean allBehind = true;
						
						
						for (Point3D point : face.getPoints()) {
							//Calculate relative coordinates and actual coordinates and add to polygon
							final RelativeScreenCoordinate relCoor2 = cam.getRelativeScreenCoordinate(point, object);
							p.addPoint((int)(maxDimension * relCoor2.x + xOffset), (int)(maxDimension * relCoor2.y + yOffset));
							
							if (relCoor2.x < 0) hasPointOnLeft = true;
							if (relCoor2.x > 1) hasPointOnRight = true;
							if (relCoor2.y < 0) hasPointOnTop = true;
							if (relCoor2.y > 1) hasPointOnBottom = true;
							if (relCoor2.x > 0 && relCoor2.x < 1) xInView = true;
							if (relCoor2.y > 0 && relCoor2.y < 1) yInView = true;
							if (!cam.isBehind(point, object)) allBehind = false;

							
						}
						
						if (allBehind && REJECT_FACES_BEHIND) {
							return;
						}
						
						//Check if the faces that do not have points on screen have points on all sides of the screen in which case the center of the face might also be on screen
						//if not, then return early
						if (xInView && yInView) { 
							//point can be on screen
						} else if (hasPointOnLeft && hasPointOnRight && hasPointOnTop && hasPointOnBottom) {	
							//points on all side of screen, possible in view
						} else if (xInView && hasPointOnTop && hasPointOnBottom) {
							//possibly in view in vertical plane
						} else if (yInView && hasPointOnLeft && hasPointOnRight) {
							//possibly in view in horizontal plane
						} else {
							//System.out.println("Scene.class: Drawing of face rejected");
							return;
						}
							
						

						
						final DrawingTask task = new DrawingTask(
								p, 
								face.getFillColor());

						map.put(face, task);
					} finally {						
						latchFaces.countDown();
					}
					
				});

			}
			
			try {
				latchFaces.await();
			} catch (InterruptedException e) {
				return view;
			}
			
			
			final Collection<Face3D> faces = object.getFaces();
			final AtomicInteger dLatch = new AtomicInteger(faces.size());

			for (final Face3D face : faces) {		
				final DrawingTask task = map.get(face);
				
				if (task == null) {
					if (dLatch.decrementAndGet() <= 0) {
						drawingLatches.countDown();
					}
				} else {
					drawingExec.submit(() -> {
						try {							
							Graphics2D gr = g;
							/*
						if (frameCounter.get() == null) {
							frameCounter.set(0);
						}
						
						if (currentFrame != frameCounter.get()) {
							frameCounter.set(currentFrame);
							gr = graphicsObjects.get();
							if (gr != null) {
								gr.dispose();
							}
							gr = view.createGraphics();
							graphicsObjects.set(gr);
						} else {
							gr = graphicsObjects.get();
							if (gr == null) {
								gr = view.createGraphics();
								graphicsObjects.set(gr);

							}
						}
							 */
							gr.setColor(task.color);
							
							//draw object face
							gr.fillPolygon(task.polygon);
							
							/*
							gr.setColor(new Color(
									face.getColor().getRed(),
									face.getColor().getGreen(),
									face.getColor().getBlue()));
									*/
							if (DRAW_POLYGON_COUNTOUR) {
								gr.setColor(Color.BLACK);
								gr.drawPolygon(task.polygon);								
							}
						} finally {
							if (dLatch.decrementAndGet() <= 0) {
								drawingLatches.countDown();
							}							
						}
						

					});
					
				}
				
			}

		}
		


		try {
			drawingLatches.await();
		} catch (InterruptedException e) {}
		
		g.dispose();
		//currentFrame++;
		
		return view;	
	}
	
	
	
	
	

	private static class DrawingTask {
		
		private final Polygon polygon;
		private final Color color;
		
		private DrawingTask(Polygon polygon, Color color) {
			this.polygon = polygon;
			this.color = color;
		}
		
	}
	
	
}








