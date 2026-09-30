package nl.guidobreuer.graph.service;

import java.awt.Image;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestCache {

	
	private static Map<String, Image> map = new ConcurrentHashMap<>();
	
	public static String put(Image image) {
		String key = "" + image.hashCode();
		map.put(key, image);
		return key;
	}
	
	public static Image get(String key) {
		return map.remove(key);
	}
	
}
