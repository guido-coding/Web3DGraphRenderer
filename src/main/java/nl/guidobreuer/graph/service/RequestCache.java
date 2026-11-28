package nl.guidobreuer.graph.service;

import java.awt.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nl.guidobreuer.graph.exception.InvalidInputException;

public class RequestCache {

	
	private final static Map<String, Image> map = new ConcurrentHashMap<>();
	
	/**
	 * Add image to cache
	 * @param image
	 * @return generated key
	 */
	public static String put(Image image) {
		String key = "" + image.hashCode();
		map.put(key, image);
		return key;
	}
	
	/**
	 * Retrieve image based on key
	 * @param key
	 * @return image from cache
	 * @throws InvalidInputException in case key does not exist.
	 */
	public static Image get(String key) throws InvalidInputException {
		Image image = map.remove(key);
		if (image == null) {
			throw new InvalidInputException("Key does not exist.");
		}
		return image;
	}
	
}
