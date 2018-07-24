package com.aaron.base;

public class ResponseBean {

	// statusLine信息，如：HTTP/1.1 200 OK
	public String status;
	// 响应码
	public String statusCode;
	// 响应内容类型
	public String contentType;
	// 响应内容
	public String body;
	// http版本
	public String version;
	// 响应cookie信息
	public String setCookie;
	// 响应内容长度
	public Long contentLength;
	// 响应内容Range值
	public String contentRange;
	// 重定向Location值
	public String location;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSetCookie() {
		return setCookie;
	}

	public void setSetCookie(String setCookie) {
		this.setCookie = setCookie;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentRange() {
		return contentRange;
	}

	public void setContentRange(String contentRange) {
		this.contentRange = contentRange;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
