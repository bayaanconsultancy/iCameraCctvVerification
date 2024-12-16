package com.tcs.ion.icamera.cctv.util;

import com.tcs.ion.icamera.cctv.onvif.OnvifException;
import jakarta.xml.soap.SOAPMessage;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest {
	private static final Logger logger = LogManager.getLogger(HttpRequest.class);

	private HttpRequest() {
	}

	public static String postXml(@NotNull String url, @NotNull String body) throws OnvifException {
		Request request = new Request.Builder().url(url).addHeader("Content-Type", "application/soap+xml")
				.post(RequestBody.create(body.getBytes())).build();

		try (Response response = HttpClient.get().newCall(request).execute()) {
			String responseString = response.body() == null ? "" : response.body().string();
			logger.info(" URL: {}, Body: {}, Response: {}, Code: {}", url, body, responseString, response.code());

			if (response.isSuccessful())
				return responseString;
			else
				throw new OnvifException(response.code() + ": " + response.message());

		} catch (Exception e) {
			logger.error("Error making HTTP XML Post request: {}", e.getMessage());
			throw new OnvifException(e.getMessage());
		}
	}

	public static byte[] getFile(@NotNull String url, String authorization) throws OnvifException {
		if (authorization == null) {
			authorization = "NOAuthorization";
		}

		Request request = new Request.Builder().url(url).addHeader("Authorization", authorization).build();

		try (Response response = HttpClient.get().newCall(request).execute()) {
			if (response.isSuccessful()) {
				return response.body() == null ? new byte[0] : response.body().bytes();
			} else {
				throw new OnvifException(response.code() + ": " + response.message());
			}
		} catch (Exception e) {
			logger.error("Error making HTTP File Get request: {}", e.getMessage());
			throw new OnvifException(e.getMessage());
		}
	}

	public static String sendSoapRequest(String soapEndpoint, SOAPMessage soapMessage) throws OnvifException, IOException {
		// Convert SOAPMessage to byte array
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(out);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OnvifException(e);
		}

		byte[] soapMessageBytes = out.toByteArray();

		// Send HTTP request
		URL url = new URL(soapEndpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
		connection.setRequestProperty("SOAPAction", "");

		// Write SOAP message to output stream
		connection.getOutputStream().write(soapMessageBytes);
		connection.getOutputStream().flush();

		// Read the response
		InputStream responseStream = connection.getInputStream();
		StringBuilder response = new StringBuilder();
		int responseChar;
		while ((responseChar = responseStream.read()) != -1) {
			response.append((char) responseChar);
		}

		return response.toString();
	}

}
