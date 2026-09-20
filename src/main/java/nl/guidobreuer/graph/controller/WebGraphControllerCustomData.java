package nl.guidobreuer.graph.controller;

import java.awt.Image;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import nl.guidobreuer.graph.exception.InvalidInputException;
import nl.guidobreuer.graph.model.CustomGraphData;
import nl.guidobreuer.graph.model.graph3d.Graph3DRenderer;
import nl.guidobreuer.graph.service.Graph3DRendererBuilder;
import nl.guidobreuer.graph.service.RequestCache;
import nl.guidobreuer.graph.util.Util;

@RestController
public class WebGraphControllerCustomData {
	
	private static final long MAX_DURATION = 300;
	
	// bucket with capacity 20 tokens and with refilling speed 1 token per each 2 second
	private Bucket createBucket(String ip) {
		return Bucket.builder()
			      .addLimit(limit -> limit.capacity(20).refillGreedy(30, Duration.ofMinutes(1)))
			      .build();
	}
	
	private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();

	/*
	 * TODO
	 * implement controller - done
	 * refactor Graph object to leave implemenation of creating scene objects to implementation - done
	 * create new Graph object implementation for custom data - done
	 * update Graph3DRendererBuilder to create appropriate Graph type - done
	 * input validation
	 */
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/customdataimage")
	public ResponseEntity<String> customDataImage(
			HttpServletRequest request,
			@RequestBody CustomGraphData customGraphData) 
					throws InvalidInputException {
		
		
		String ip = extractClientIp(request);
		Bucket bucket = bucketMap.computeIfAbsent(ip, this::createBucket);
		if (!bucket.tryConsume(1)) {
			//throw new IllegalStateException("Server timeout. Too many requests. Only 1 request per second allowed.");
			System.err.println("Server timeout. Too many requests. Only 1 request per second allowed.");
			return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Server timeout. Too many requests. Only 1 request per second allowed.");
		}
		
		long start = System.currentTimeMillis();
		
		/*
		System.out.println("test");
		System.out.println(customGraphData);
		System.out.println(customGraphData.data()[0].length);
		System.out.println(customGraphData.data()[0][0]);
		*/
		
		Graph3DRenderer renderer = Graph3DRendererBuilder.createGraph3DRendererCustomData(customGraphData);
		
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
		Image image = renderer.getGraphImage(customGraphData.width(), customGraphData.height());
		running.set(false);
		
		long end = System.currentTimeMillis();
		long duration = end-start;
		
		System.out.println(ip + " --- " + LocalDateTime.now() + ": --- Graph rendered in " + duration + " ms.");
		
		String key = RequestCache.put(image);
		
		//return Util.imageToByteOutput(image);
		return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(key);
	}
	
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@GetMapping("/retrieve/{key}")
	public ResponseEntity<Resource> retrieve(
			HttpServletRequest request,
			@PathVariable String key) {
		
		return ResponseEntity.ok( Util.imageToByteOutput(RequestCache.get(key)) );
		
	}
	
	private String extractClientIp(HttpServletRequest request) {
	    String header = request.getHeader("X-Forwarded-For");
	    if (header != null && !header.isBlank()) {
	        String[] ips = header.split(",");
	        return ips[0].trim();
	    }

	    String fallback = request.getHeader("X-Real-IP");
	    if (fallback != null && !fallback.isBlank()) {
	        return fallback.trim();
	    }

	    return request.getRemoteAddr();
	}
		
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleInvalidInputException(Exception ex) {
		//System.err.println(ex.getMessage());
		ex.printStackTrace();

		return ResponseEntity.internalServerError().body((ex.getMessage()) );
	}
	
}
