package com.aaron.base;

import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRedirectStrategy extends DefaultRedirectStrategy {

	private static Logger logger = LoggerFactory.getLogger(MRedirectStrategy.class);

	private static final String[] REDIRECT_METHODS = new String[] { HttpGet.METHOD_NAME, HttpPost.METHOD_NAME,
			HttpHead.METHOD_NAME, HttpDelete.METHOD_NAME };

	@Override
	protected boolean isRedirectable(final String method) {
		for (final String m : REDIRECT_METHODS) {
			if (m.equalsIgnoreCase(method)) {
				return true;
			}
		}
		return false;
	}

	public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
			throws ProtocolException {
		URI uri = getLocationURI(request, response, context);
		String method = request.getRequestLine().getMethod();
		if (HttpPost.METHOD_NAME.equalsIgnoreCase(method)) {
			try {
				HttpRequestWrapper httpRequestWrapper = (HttpRequestWrapper) request;
				httpRequestWrapper.setURI(uri);
				httpRequestWrapper.removeHeaders("Content-Length");
				return httpRequestWrapper;
			} catch (Exception e) {
				logger.error("强转为HttpRequestWrapper出错");
			}
			return new HttpPost(uri);
		} else {
			return new HttpGet(uri);
		}
	}

}
