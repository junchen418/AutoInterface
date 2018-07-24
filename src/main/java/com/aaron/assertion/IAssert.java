package com.aaron.assertion;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import org.assertj.core.api.Condition;
import org.assertj.core.internal.Failures;

import com.zf.zson.ZSON;
import com.zf.zson.result.ZsonResult;

public class IAssert {

	/**
	 * 断言对象为null
	 * 
	 * @param actual
	 */
	public static void assertIsNull(Object actual) {
		assertThat(actual).isNull();
	}

	/**
	 * 断言actual为true
	 * 
	 * @param actual
	 */
	public static void assertIsTrue(Boolean actual) {
		assertThat(actual).isTrue();
	}

	/**
	 * 断言actual中包含expected
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertContainsSubsequence(String actual, String expected) {
		assertThat(actual).contains(expected);
	}

	/**
	 * 断言actual与expected相同
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertEqualse(String actual, String expected) {
		assertThat(actual).isEqualTo(expected);
	}

	/**
	 * 断言actual与expected相等
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertEqualse(Long actual, Long expected) {
		assertThat(actual).isEqualTo(expected);
	}

	/**
	 * 断言actual与expected布尔值相同
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertEqualse(boolean actual, boolean expected) {
		assertThat(actual).isEqualTo(expected);
	}

	/**
	 * 断言actual与expected相等
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertEqualse(int actual, int expected) {
		assertThat(actual).isEqualTo(expected);
	}

	/**
	 * 断言actual小于expected
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertLess(int actual, int expected) {
		assertThat(actual).isLessThan(expected);
	}

	/**
	 * 断言actual小于等于expected
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertLessOrEqualse(int actual, int expected) {
		assertThat(actual).isLessThanOrEqualTo(expected);
	}

	/**
	 * 断言actual大于expected
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertGreater(int actual, int expected) {
		assertThat(actual).isGreaterThan(expected);
	}

	/**
	 * 断言actual大于等于expected
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertGreaterOrEqualse(int actual, int expected) {
		assertThat(actual).isGreaterThanOrEqualTo(expected);
	}

	/**
	 * 断言actual与expected相同（忽略大小写）
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertIgnoringEqualse(String actual, String expected) {
		assertThat(actual).isEqualToIgnoringCase(expected);
	}

	/**
	 * 断言actual与expected相同（忽略空格）
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertIgnoringWhitespaceEqualse(String actual, String expected) {
		assertThat(actual).isEqualToIgnoringWhitespace(expected);
	}

	/**
	 * 断言actual Json值与expecte Json值相同
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertJsonAllEntity(String actual, String expected) {
		assertThat(ZSON.parseJson(actual)).isEqualToComparingFieldByFieldRecursively(ZSON.parseJson(expected));
	}

	/**
	 * 断言actual Json值包含expecte Json值
	 * 
	 * @param actual
	 * @param expected
	 */
	public static void assertJsonContainsEntity(String actual, Map<String, Object> expected) {
		ZsonResult zsonResult = ZSON.parseJson(actual);
		assertThat(zsonResult).has(new Condition<ZsonResult>(new Predicate<ZsonResult>() {
			@Override
			public boolean test(ZsonResult t) {
				boolean result = false;
				for (Entry<String, Object> entry : expected.entrySet()) {
					List<Object> list = t.getValues("//" + entry.getKey());
					for (Object temp : list) {
						result = String.valueOf(temp).equals(String.valueOf(entry.getValue()));
						if (!result) {
							continue;
						} else {
							break;
						}
					}
					if (result) {
						continue;
					} else {
						break;
					}
				}
				return result;
			}
		}, "actual is not contains expected!", null));
	}

	/**
	 * 断言actual Map值与expecte Map值相同
	 * 
	 * @param actual
	 * @param expected
	 */
	public static <T> void assertMapAllEntity(Map<String, T> actual, Map<String, T> expected) {
		assertThat(actual).containsAllEntriesOf(expected);
	}

	/**
	 * 断言actual Map值包含expecte Map值
	 * 
	 * @param actual
	 * @param expected
	 */
	public static <T> void assertMapContainsEntity(Map<String, T> actual, Map<String, T> expected) {
		if (expected.isEmpty()) {
			throw Failures.instance().failure("expected is empty");
		}
		for (Entry<String, T> entry : expected.entrySet()) {
			assertThat(actual).contains(entry);
		}
	}

}
