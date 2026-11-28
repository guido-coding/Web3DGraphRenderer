package nl.guidobreuer.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Rendering of images is done via the following steps:
 * -Entry points are in controller package
 * -Obtain settings objects from requester input.
 * -Create model.graph3d.Graph3DRenderer object using model.Graph3DRendererBuilder and settings object.
 * -A model.graph3d.Graph object (member variable of Graph3DRenderer) is created.
 * -Graph3DRendererBuilder sets all settings in the Graph3DRenderer object and the Graph3DRenderer in turn sets the appropriate settings in the Graph object.
 * -The controller calls the getGraphImage method in the Graph3DRenderer object to render an image.
 * -The Grah3DRenderer objects obtains all objects to render from the Graph object's getObjects method.
 * -The image is returned to the requester.
 */

@SpringBootApplication
public class WebGraph {
	

	public static void main(String[] args) {
		SpringApplication.run(WebGraph.class, args);
	}


	
}
