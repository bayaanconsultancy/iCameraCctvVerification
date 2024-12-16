package com.cs.on.icamera.cctv.util;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class HttpClient {
	private static final long TIMEOUT_IN_SECONDS = 5;
	private static volatile OkHttpClient okHttpClient;

	private HttpClient() {
	}

	public static OkHttpClient get() {
		if (okHttpClient == null) {
			synchronized (HttpClient.class) {
				if (okHttpClient == null) {
					okHttpClient = createOkHttpClient();
				}
			}
		}
		return okHttpClient;
	}

	private static OkHttpClient createOkHttpClient() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
				.writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS).retryOnConnectionFailure(true);
		return builder.build();
	}
}
