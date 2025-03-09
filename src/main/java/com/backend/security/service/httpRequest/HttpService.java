package com.backend.security.service.httpRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.security.util.ConfigEnvUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HttpService {
    
    private final ConfigEnvUtility configEnvUtility;

    @Autowired
	Gson gson;


    	public Hashtable<CloseableHttpResponse, String> sendPOST(int operationId, int stepId, String callDataType, String url,
			String body, ConcurrentHashMap<String, String> headerMap, ConcurrentHashMap<String, String> extraHeaderMap) throws Exception {

		try {

			HttpPost post = new HttpPost("http://localhost:8086/backend/demo-controller");
			if (callDataType.equalsIgnoreCase("xml")) {
				post.addHeader("Accept", "application/xml");
				post.addHeader("Content-Type", "application/xml");
			} else {
				post.addHeader("Accept", "application/json");
				post.addHeader("Content-Type", "application/json");
			}
			if (headerMap != null) {
				for (Map.Entry<String, String> entry : headerMap.entrySet()) {
					post.addHeader(entry.getKey(), entry.getValue());

				}
			}

			if (extraHeaderMap != null) {
				for (Map.Entry<String, String> entry : extraHeaderMap.entrySet()) {

					post.addHeader(entry.getKey(), entry.getValue());

				}
			}
			if (!callDataType.equalsIgnoreCase("xml")) {
				JsonObject json = gson.fromJson(body, JsonObject.class);
				if (json != null) {
					post.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));
				}
			} else {
				post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
			}

			try {

				CloseableHttpResponse response = getHttpClient().execute(post);

				HttpEntity entity = response.getEntity();

				Hashtable<CloseableHttpResponse, String> responseMap = new Hashtable<CloseableHttpResponse, String>();
				// check for null
				if (entity != null) {
					responseMap.put(response, EntityUtils.toString(entity, StandardCharsets.UTF_8));
				} else {
					responseMap.put(response, "");
				}
				return responseMap;

			} finally {
				getHttpClient().close();
			}

		} catch (ClientProtocolException e) {

			log.error("ClientProtocolException occured: " + e);
			throw new Exception("Sorry, the Service You're Looking for is Currently Down. The URL is " + url);
		} catch (IOException e) {

			log.error("IOException occured: " + e);
			throw new Exception("Sorry, the Service You're Looking for is Currently Down. The URL is " + url);
		} finally {
			try {
				getHttpClient().close();
			} catch (IOException e) {

				log.error("IOException occured: " + e);
				throw new Exception("Sorry, the Service You're Looking for is Currently Down. The URL is " + url);
			}
		}

	}

    	private CloseableHttpClient getHttpClient()
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.loadTrustMaterial(null, trustStrategy);
		return HttpClients.custom()
				.setSSLContext(sslContextBuilder.build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setConnectionTimeToLive(100L, TimeUnit.SECONDS)
				.evictExpiredConnections()
				.build();

	}



}
