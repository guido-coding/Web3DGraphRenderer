package graph3d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import equationparser.InvalidEquationException;
import render3d.Scene;


@SuppressWarnings("serial")
public class Graph3D extends JPanel {

	public static void main(String arg[]) throws InvalidEquationException {
		new Graph3D();
	}
	
	
	
	private final Scene scene;
	private BufferedImage image;
	private volatile boolean running = true;
	private final Graph graph;

	
	private double r, theta, phi;
	private final double rotationStepSize = 0.05;
	
	private final String equation;
	
	Graph3D() throws InvalidEquationException {
		scene = new Scene();
		
		equation = "(0.1*y^2 - 0.1*x^2 + 0.1*x*y + 0.1)";
		
		graph = new CustomGraph2(equation);

		
		JFrame frame = new JFrame("3D Graph viewer");
		frame.add(this);
		frame.setSize(1500, 1000);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				scene.close();
				running = false;
			}
		});
		
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				handleKeyInput(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		frame.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() < 0) {
					r *= 0.9;
				} else { 
					r *= 1.1;
				}
				//updateCamera();
				repaint();
			}
			
		});
		
		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				//updateCamera();
				//repaint();
			}
			
		});
		
		r = 100;
		theta = 0.26;
		phi = 0.3;
		
		//loadGraph();
		frame.setVisible(true);
		
		//updateCamera();
		
		
		//startAnimation();;
	}
	
	private void startAnimation() {
		scene.setCameraOrbit(70, 0, 0, 50);
		scene.setOrbitParameters(0.05, 0.02, 0.3);
		scene.focusOn(0, 0, 0);
		Thread t = new Thread(() -> {
			
			while (running) {
				image = scene.next3DView(getSize().width, getSize().height);
				repaint();
				try {
					Thread.sleep(10);
				} catch(Exception e) {}
			}
			
			
		});
		t.start();
	}

	private boolean isBusy = false;
	
	private void handleKeyInput(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) theta += rotationStepSize;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) theta -= rotationStepSize;		
		if (e.getKeyCode() == KeyEvent.VK_UP) phi -= rotationStepSize/2;
		if (e.getKeyCode() == KeyEvent.VK_DOWN) phi += rotationStepSize/2;
		
		if (phi < 0.01) phi = 0.01;
		if (phi > Math.PI*.99) phi = Math.PI*.99;
		
		/*
		if (!isBusy) {	
			Thread t = new Thread(() -> {				
				updateCamera();
			});
			t.start();
		}
		*/
		repaint();
	}
	
	private void updateCamera() {
		isBusy = true;
		scene.setCamera(0, 0, 0, r, theta, phi);
		image = scene.next3DView(getSize().width, getSize().height);
		repaint();
		isBusy = false;
	}

	private void loadGraph() {
		
		scene.addObjects(graph.getObjects(true, false));
		scene.MAX_DRAW_DISTANCE = -1;
		scene.DRAW_POLYGON_COUNTOUR = true;
		scene.REJECT_FACES_BEHIND = false;
		scene.ANTI_ALIAS = true;
		
		r = 100;
		theta = 0.26*Math.PI;
		phi = 0.3*Math.PI;
		
		
		
	}
	
	public void paint(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		
		Graph3DRenderer graphRen = null;
		try {
			graphRen = new Graph3DRenderer("0.1*y^2 - 0.1*x^2 + 0.1*x*y + 20");
			
			graphRen.setRotation(theta);
			graphRen.setPhi(phi);
			graphRen.setR(r);
			
			graphRen.setBounds(-10, 10, -10, 10);
			graphRen.setOffsets(0,0,0);
			graphRen.setZScalingFactor(0.5);
			graphRen.setSteps(25);
			
			graphRen.setTransparency(200);
			
			graphRen.showLabels(true);
			graphRen.drawAxis(true);
			graphRen.drawGrid(true);
			
			image = graphRen.getGraphImage(1200,800);
			g.drawImage(image, 0, 0, null);
		} catch (InvalidEquationException e) {
			e.printStackTrace();
		} finally {
			if (graphRen != null) {				
				graphRen.dispose();			
			}
		}
		/*
		g.setColor(Color.BLACK);
		g.setFont(new Font("verdana", Font.BOLD, 24));
		g.drawString("z = " + equation, 40, 40);
		*/

		/*
		g.drawImage(image, 0, 0, null);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("verdana", Font.BOLD, 24));
		
		
		Point p = scene.getLocationOnScreen(getSize().width, getSize().height, 0, 0, 0);
		g.drawString("0", p.x, p.y);
		
		p = scene.getLocationOnScreen(getSize().width, getSize().height, -11.5, 0, 0);
		g.drawString("x", p.x, p.y);
		
		p = scene.getLocationOnScreen(getSize().width, getSize().height, 0, -11.5, 0);
		g.drawString("y", p.x, p.y);
		
		p = scene.getLocationOnScreen(getSize().width, getSize().height, 0, 0, 11.5);
		g.drawString("z", p.x, p.y);
		

		g.drawString("z = " + equation, 40, 40);
		*/
	}
	
}
