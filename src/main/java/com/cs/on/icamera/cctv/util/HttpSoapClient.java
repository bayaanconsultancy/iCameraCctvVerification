package com.cs.on.icamera.cctv.util;

import com.cs.on.icamera.cctv.onvif.OnvifException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpSoapClient {

    /**
     * Post a SOAP XML request to the given URL.
     *
     * @param url   The URL to post to.
     * @param xml   The SOAP XML request.
     * @return The response from the server.
     * @throws OnvifException If there is an error posting the request.
     */
    public static String postXml(String url, String xml) throws OnvifException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Set up the HTTP POST request
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/soap+xml; charset=UTF-8");

            // Set the request body to the SOAP XML
            StringEntity entity = new StringEntity(xml, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity responseEntity = response.getEntity();

                // If there is no response, return null
                if (responseEntity == null) return null;
                else {
                    // Otherwise return the response as a string
                    return EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            // If there is an error, throw an OnvifException
            throw new OnvifException(e);
        }
    }
}
