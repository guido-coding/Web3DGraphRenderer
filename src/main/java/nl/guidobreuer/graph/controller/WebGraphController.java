package nl.guidobreuer.graph.controller;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import equationparser.InvalidEquationException;
import nl.guidobreuer.graph.exception.InvalidInputException;
import nl.guidobreuer.graph.model.RenderingSettings;
import nl.guidobreuer.graph.model.graph3d.Graph3DRenderer;
import nl.guidobreuer.graph.service.Graph3DRendererBuilder;
import nl.guidobreuer.graph.util.Util;

import java.time.Duration;
import java.time.LocalDateTime;

/*
 */

@RestController
public class WebGraphController {

	private static final long MAX_DURATION = 300;
	
	// bucket with capacity 20 tokens and with refilling speed 1 token per each 2 second
	private Bucket createBucket(String ip) {
		return Bucket.builder()
			      .addLimit(limit -> limit.capacity(20).refillGreedy(30, Duration.ofMinutes(1)))
			      .build();
	}
	
	private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();

	
	
	
	
	@GetMapping("/image")
	public ResponseEntity<Resource> start(
			HttpServletRequest request,
			@RequestParam Map<String, String> options) throws InvalidEquationException, InvalidInputException {
		

		String ip = extractClientIp(request);
		Bucket bucket = bucketMap.computeIfAbsent(ip, this::createBucket);
		if (!bucket.tryConsume(1)) {
			throw new IllegalStateException("Server timeout. Too many requests. Only 1 request per second allowed.");
		}
		
		long start = System.currentTimeMillis();
		
		RenderingSettings settings = new RenderingSettings(options);
		Graph3DRenderer renderer = Graph3DRendererBuilder.createGraph3DRenderer(settings);
		
		Dimension size = settings.getSize();
		
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
		Image image = renderer.getGraphImage(size.width, size.height);
		running.set(false);
		
		long end = System.currentTimeMillis();
		long duration = end-start;
		
		System.out.println(ip + " --- " + LocalDateTime.now() + ": --- Graph rendered in " + duration + " ms.");
		
		
		return ResponseEntity.ok( Util.imageToByteOutput(image));
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
	public ResponseEntity<Resource> handleInvalidInputException(Exception ex) {
		//System.err.println(ex.getMessage());
		ex.printStackTrace();

		return ResponseEntity.internalServerError().body(
				Util.imageToByteOutput(Util.getErrorImage(ex.getMessage()) ));
	}

		

	
}
