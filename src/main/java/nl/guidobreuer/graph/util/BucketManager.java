package nl.guidobreuer.graph.util;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;


public class BucketManager {

	private static final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();
	
	
	// bucket with capacity 20 tokens and with refilling speed 1 token per each 2 second
	private static Bucket createBucket(String ip) {
		return Bucket.builder()
			      .addLimit(limit -> limit.capacity(20).refillGreedy(30, Duration.ofMinutes(1)))
			      .build();
	}
	
	/**
	 * 
	 * @param request
	 * @return ip address of requester
	 */
	public static String tryBucket(HttpServletRequest request) {
		String ip = extractClientIp(request);
		Bucket bucket = bucketMap.computeIfAbsent(ip, BucketManager::createBucket);
		if (!bucket.tryConsume(1)) {
			throw new IllegalStateException("Server timeout. Too many requests. ");		
		}
		return ip;
	}
	
	
	
	private static String extractClientIp(HttpServletRequest request) {
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
		
	
}
