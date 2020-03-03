package com.example.demo;

import java.time.Duration;
import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;

@RestController
public class BulkheadController {
	private Bulkhead bulkhead;
	public static String str;

	@GetMapping("/bh")
	public String greeting() {
		Runnable runnable = () -> decomp((int) (Math.random() * 18) + 2);
		bulkhead.executeRunnable(runnable);
		return str;
	}

	@Bean
	public void client() {
		BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(5).maxWaitDuration(Duration.ofMillis(500))
				.build();
		bulkhead = Bulkhead.of("my", config);
	}

	public void decomp(int n) {
		StringBuilder str1 = new StringBuilder();
		HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
		int count = 0;
		for (int j = 2; j <= n; j++) {
			int num = j;
			for (int i = 2; i <= num; i++) {
				count = 0;
				while (num % i == 0) {
					count++;
					num = num / i;
				}
				if (hmap.containsKey(i)) {
					count += hmap.get(i);
				}
				hmap.put(i, count);
			}
		}
		for (int k = 2; k <= n; k++)
			if (hmap.containsKey(k) && hmap.get(k) != 0) {
				if (hmap.get(k) == 1) {
					str1.append(k + " * ");
				} else {
					str1.append(k + "^" + hmap.get(k) + " * ");
				}
			}
		str = "prime factor decomposition of "+ n +" are " + str1.toString().substring(0, str1.length() - 3);
	}
}