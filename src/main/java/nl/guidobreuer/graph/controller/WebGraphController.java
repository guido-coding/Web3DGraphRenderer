package nl.guidobreuer.graph.controller;

import java.awt.Dimension;
import java.awt.Image;
import java.util.Map;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import equationparser.InvalidEquationException;
import graph3d.Graph3DRenderer;
import nl.guidobreuer.graph.exception.InvalidInputException;
import nl.guidobreuer.graph.model.RenderingSettings;
import nl.guidobreuer.graph.service.Graph3DRendererBuilder;
import nl.guidobreuer.graph.util.Util;

import java.time.LocalDateTime;

/*
 */

@RestController
public class WebGraphController {

	
	

	@GetMapping("/image")
	public ResponseEntity<Resource> start(
			@RequestParam Map<String, String> options) throws InvalidEquationException, InvalidInputException {
		
		long start = System.currentTimeMillis();
		
		RenderingSettings settings = new RenderingSettings(options);
		Graph3DRenderer renderer = Graph3DRendererBuilder.createGraph3DRenderer(settings);
		
		Dimension size = settings.getSize();
		Image image = renderer.getGraphImage(size.width, size.height);
		
		long end = System.currentTimeMillis();
		long duration = end-start;
		
		System.out.println(LocalDateTime.now() + ": --- Graph rendered in " + duration + " ms.");
		
		
		return Util.imageToByteOutput(image);
	}

		
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Resource> handleInvalidInputException(Exception ex) {
		System.err.println(ex.getMessage());

		return Util.imageToByteOutput(Util.getErrorImage(ex.getMessage()));
	}

		

	
}
