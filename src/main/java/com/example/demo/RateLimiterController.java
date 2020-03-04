package com.example.demo;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

@RestController
public class RateLimiterController {
	private final AtomicLong counter = new AtomicLong();
	private RateLimiter rateLimiter;
	public static String str1;

	@GetMapping("/rl")
	public String callService(@RequestParam(value = "val", defaultValue = "30") String val) {
		// Wrap service call in ratelimiter & call service.
		Runnable runnable = () -> callService();
		rateLimiter.executeRunnable(runnable);
		return str1;
	}

	@Bean
	public void LowRateServiceCallerClient() {
		/*
		 * Create rate limiter to allow 5 calls every 5 seconds & keep other calls
		 * waiting until maximum of 10 seconds.
		 */
		RateLimiterConfig config = RateLimiterConfig.custom().limitRefreshPeriod(Duration.ofMillis(5000))
				.limitForPeriod(10).timeoutDuration(Duration.ofMillis(30000)).build();

		// Create registry
		RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.of(config);

		// Use registry
		rateLimiter = rateLimiterRegistry.rateLimiter("externalConcurrentService");
	}

	public void callService() {
		str1 = LocalTime.now() + " Call processing finished in Rate Limiter, call number: " + counter.incrementAndGet();
	}
}
