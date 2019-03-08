package com.aaron.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Aaron
 *
 */
public class StreamAssert {

	private static Logger logger = LoggerFactory.getLogger(StreamAssert.class);
	private StringBuilder stringBuilder = new StringBuilder();
	private Boolean result = true;

	/**
	 * 断言对象为null
	 * 
	 * @param actual
	 */
	public StreamAssert assertIsNull(Object actual) {
		try {
			assertThat(actual).isNull();
		} catch (Error e) {
			handleResutl(false, e.getMessage());
		}
		return this;
	}

	/**
	 * 断言actual为true
	 * 
	 * @param actual
	 */
	public StreamAssert assertIsTrue(Boolean actual) {
		try {
			assertThat(actual).isTrue();
		} catch (Error e) {
			handleResutl(false, e.getMessage());
		}
		return this;

	}

	private void handleResutl(Boolean bol, String message) {
		result = result && bol;
		stringBuilder.append(message).append(
				"\n###################################################################################################\n");
	}

	public String close() {
		String msg = stringBuilder.toString();
		if (!result) {
			logger.error(msg);
			throw new AssertionError(msg);
		}
		return msg;
	}

	public static void main(String[] args) {
		new StreamAssert().assertIsNull(1).assertIsNull(null).assertIsNull(2).close();
	}
}
