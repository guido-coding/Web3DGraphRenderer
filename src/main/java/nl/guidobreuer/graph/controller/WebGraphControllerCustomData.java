package nl.guidobreuer.graph.controller;

import java.awt.Image;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import nl.guidobreuer.graph.exception.InvalidInputException;
import nl.guidobreuer.graph.model.CustomGraphData;
import nl.guidobreuer.graph.model.Graph3DRendererBuilder;
import nl.guidobreuer.graph.model.graph3d.Graph3DRenderer;
import nl.guidobreuer.graph.service.RequestCache;
import nl.guidobreuer.graph.util.BucketManager;
import nl.guidobreuer.graph.util.ImageToStream;

@RestController
public class WebGraphControllerCustomData {
	
	private static final long MAX_DURATION = 300; //ms
	

	/**
	 * Entry point  to create image from custom data (tabulated data with x, y, z coordinates).
	 * Request body is mapped to a CustomGraphData object.
	 * Once the image is rendered, a key is returned to the requester that can be used to retrieve the image. This is done to allow the image to be retrieved using a GET request whereas all data is submitted using a POST request.
	 * 
	 * @param request
	 * @param customGraphData
	 * @return key that can be used to retrieve the image using the /retrieve/{key} entrypoint
	 * @throws InvalidInputException
	 */
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/customdataimage")
	public ResponseEntity<String> customDataImage(
			HttpServletRequest request,
			@RequestBody CustomGraphData customGraphData) 
					throws InvalidInputException {
		
		//limit number of requests per IP address
		String ip;
		try {
			ip = BucketManager.tryBucket(request);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Server timeout. Too many requests. Only 1 request per second allowed.");
		}
		
		long start = System.currentTimeMillis();
		
		
		Graph3DRenderer renderer = Graph3DRendererBuilder.createGraph3DRendererCustomData(customGraphData);
		
		//Check if maximum duration is exceeded and interupt thread to stop rendering if maximum time is exceeded.
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
		
		
		Image image = renderer.getGraphImage(customGraphData.width(), customGraphData.height()); //render image
		String key = RequestCache.put(image); //add image to map and return key to requester.
		
		running.set(false);
		long end = System.currentTimeMillis();
		long duration = end-start;
		System.out.println(ip + " --- " + LocalDateTime.now() + ": --- Graph rendered in " + duration + " ms.");

		
		//return key to requester
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(key);
	}
	
	/**
	 * Entry point to retrieve image
	 * Use the key as obtained from the /customdataimage entry point.
	 * 
	 * @param request
	 * @param key
	 * @return bytestream containing image data
	 * @throws InvalidInputException 
	 */
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@GetMapping("/retrieve/{key}")
	public ResponseEntity<Resource> retrieve(
			HttpServletRequest request,
			@PathVariable String key) throws InvalidInputException {
		
		return ResponseEntity.ok( ImageToStream.imageToByteOutput(RequestCache.get(key)) );
		
	}
	
		
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleInvalidInputException(Exception ex) {
		//System.err.println(ex.getMessage());
		ex.printStackTrace();

		return ResponseEntity.internalServerError().body((ex.getMessage()) );
	}
	
}
