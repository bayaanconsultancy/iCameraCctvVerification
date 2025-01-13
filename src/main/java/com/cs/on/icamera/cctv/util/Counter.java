package com.cs.on.icamera.cctv.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	private final int total;
	private final AtomicInteger count;

	public Counter(int total) {
		this.total = total;
		this.count = new AtomicInteger(0);
	}

	public int increment() {
		return count.incrementAndGet();
	}

	public int count() {
		return count.get();
	}

	public int total() {
		return total;
	}

	public int getPercentage() {
		if (total == 0) {
			return 0;
		}
		return (count.get() * 100) / total;
	}

	public boolean isComplete() {
		return count.get() >= total;
	}
}
