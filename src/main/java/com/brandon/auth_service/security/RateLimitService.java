package com.brandon.auth_service.security;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

/**
 * Service responsible for managing client-side rate limiting using the Token
 * Bucket algorithm.
 * <p>
 * This service helps prevent brute-force attacks and API abuse by tracking
 * requests counts per IP address. It utilizes the Bucket4j library to maintain
 * in-memory usage state across requests.
 * </p>
 */
@Service
public class RateLimitService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Resolves or creates a rate-limiting bucket for a specific IP address
     * <p>
     * The current configuration allows a <b>maximum of 5 requests</b> per minute.
     * The bucket refills all 5 tokens at once every 60 seconds.
     * </p>
     * 
     * @param ip the remote IP address of the client.
     * @return A {@link Bucket} instance associated with the provided IP.
     */
    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(1))
                        .build())
                .build());
    }
}
