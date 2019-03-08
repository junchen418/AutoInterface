package com.aaron.base;

public class ResponseBean {

	/**
	 * statusLine信息，如：HTTP/1.1 200 OK
	 */
	public String status;
	
	/**
	 *  响应码
	 */
	public String statusCode;
	
	/**
	 *  响应内容类型
	 */
	public String contentType;
	
	/**
	 *  响应内容
	 */
	public String body;
	
	/**
	 * http协议版本
	 */
	public String version;
	
	/**
	 *  响应cookie信息
	 */
	public String setCookie;
	
	/**
	 * 响应内容长度
	 */
	public Long contentLength;
	
	/**
	 *  响应内容Range值
	 */
	public String contentRange;
	
	/**
	 *  重定向Location值
	 */
	public String location;

	
	/**
	 * 返回http协议版本号
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 返回响应状态信息
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 返回响应码
	 * @return
	 */
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * 返回ContentType
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 返回响应文本内容
	 * @return
	 */
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * 返回cookie
	 * @return
	 */
	public String getSetCookie() {
		return setCookie;
	}

	public void setSetCookie(String setCookie) {
		this.setCookie = setCookie;
	}

	/**
	 * 返回响应内容长度
	 * @return
	 */
	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * 返回range
	 * @return
	 */
	public String getContentRange() {
		return contentRange;
	}

	public void setContentRange(String contentRange) {
		this.contentRange = contentRange;
	}

	/**
	 * 返回location
	 * @return
	 */
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
