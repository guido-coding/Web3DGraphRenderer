package nl.guidobreuer.graph.controller;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.exception.InvalidInputException;
import nl.guidobreuer.graph.model.Graph3DRenderer;
import nl.guidobreuer.graph.model.Graph3DRendererBuilder;
import nl.guidobreuer.graph.model.RenderingSettings;
import nl.guidobreuer.graph.util.BucketManager;
import nl.guidobreuer.graph.util.ImageToStream;

import java.time.LocalDateTime;


@RestController
public class WebGraphController {

	private static final long MAX_DURATION = 300; //ms
	



	/**
	 * Entry point to render an image using an equation
	 * Equation and options are added as a key-value pair map. These are then mapped to a RenderingSettings object.
	 * 
	 * @param request
	 * @param options
	 * @return
	 * @throws InvalidEquationException
	 * @throws InvalidInputException
	 */
	@GetMapping("/image")
	public ResponseEntity<Resource> start(
			HttpServletRequest request,
			@RequestParam Map<String, String> options) throws InvalidEquationException, InvalidInputException {
		
		//restrict number of requests
		String ip = BucketManager.tryBucket(request);
		
		long start = System.currentTimeMillis();
		
		RenderingSettings settings = new RenderingSettings(options); //extract settings
		Graph3DRenderer renderer = Graph3DRendererBuilder.createGraph3DRenderer(settings); //create renderer
		Dimension size = settings.getSize();
		
		//Start threat to interrupt thread and stop rendering when the maximum duration is exceeded.
		final Thread thread = Thread.currentThread();
		final AtomicBoolean running = new AtomicBoolean(true);
		Thread.ofVirtual().start(() -> {
			try {
				Thread.sleep(MAX_DURATION);
			} catch (InterruptedException e) {}
			if (running.get()) {
				thread.interrupt();
			}
		});
		
		Image image = renderer.getGraphImage(size.width, size.height); //render image
		
		running.set(false);
		long end = System.currentTimeMillis();
		long duration = end-start;
		System.out.println(ip + " --- " + LocalDateTime.now() + ": --- Graph rendered in " + duration + " ms.");
		
		//return the image as a byte stream
		return ResponseEntity.ok( ImageToStream.imageToByteOutput(image));
	}

	

	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Resource> handleInvalidInputException(Exception ex) {
		//System.err.println(ex.getMessage());
		ex.printStackTrace();

		return ResponseEntity.internalServerError().body(
				ImageToStream.imageToByteOutput(ImageToStream.getErrorImage(ex.getMessage()) ));
	}

		

	
}
