package com.aaron.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 两个json字符串断言
 *
 * @author Aaron
 */
public class IJsonAssert {

    private static Logger logger = LoggerFactory.getLogger(IJsonAssert.class);

    /**
     * 两个json完全相同
     *
     * @param actual
     * @param expected
     */
    public static void assertEquals(String actual, String expected) {
        try {
            assertThat(actual).contains(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值在实际json中key-value值相等，忽略数组顺序
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsLenient(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, false);
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值完全相同,验证数组顺序
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsStrictWithArrayOrder(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, true);
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值在实际key-value值中相同，且验证数组顺序
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsLenientWithArrayOrder(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT_ORDER);
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值完全相同,忽略数组顺序
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsStrict(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值相同,忽略数组顺序。expected中value值支持正则，比如${[0-9]*}。
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsLenientByRegx(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, new FuzzyComparator(JSONCompareMode.LENIENT));
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值相同,验证数组顺序。expected中value值支持正则，比如${[0-9]*}。
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsLenientWithArrayOrderByRegx(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, new FuzzyComparator(JSONCompareMode.STRICT_ORDER));
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值完全相同,忽略数组顺序。expected中value值支持正则，比如${[0-9]*}。
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsStrictByRegx(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, new FuzzyComparator(JSONCompareMode.NON_EXTENSIBLE));
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 预期json中的值和实际json中key-value值完全相同,验证数组顺序。expected中value值支持正则，比如${[0-9]*}。
     *
     * @param actual
     * @param expected
     * @throws JSONException
     */
    public static void assertEqualsStrictWithArrayOrderByRegx(String actual, String expected) {
        try {
            JSONAssert.assertEquals(expected, actual, new FuzzyComparator(JSONCompareMode.STRICT));
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * jsonPath值匹配正确
     *
     * @param actual
     * @param expected
     * @param jsonPath 支持简单jsonPath，不支持属性过滤等高级用法。如：phoneNumbers[0].type
     * @param regex
     * @throws JSONException
     * @throws IllegalArgumentException
     */
    public static void assertEqualsByRegex(String actual, String expected, String jsonPath, String regex) {
        try {
            JSONAssert.assertEquals(expected, actual, new CustomComparator(JSONCompareMode.STRICT,
                    new Customization(jsonPath, new RegularExpressionValueMatcher<Object>(regex))));
        } catch (JSONException | AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected);
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    public static void main(String[] args) throws JSONException {
        String actual = "{\"code\":\"100\",\"status\":true,\"data\":{\"patent_id\":\"ff6bc27d-ac2c-4bec-9094-debb19a40f70\",\"mlt\":[\"ff6bc27d-ac2c-4bec-9094-debb19a40f70\",\"4980b0d0-28e6-44f8-b830-429c7b026d7f\"]},\"num\":1}";
        String expect = "{\"code\":\"${[0-9]{0,3}}\",\"status\":\"${(true|false)}\",\"data\":{\"patent_id\":\"ff6bc27d-ac2c-4bec-9094-debb19a40f70\",\"mlt\":[\"${[0-9a-z]+-[0-9a-z]+-[0-9a-z]+-[0-9a-z]+-[0-9a-z]}\"]}}";
        IJsonAssert.assertEqualsLenientByRegx(actual, expect);
        String actual2 = "[\"ff6bc27d-ac2c-4bec-9094-debb19a40f70\",\"4980b0d0-28e6-44f8-b830-429c7b026d7f\"]";
        String expect2 = "[\"${.*}\"]";
        IJsonAssert.assertEqualsStrictByRegx(actual2, expect2);
        String expect3 = "{\"code\":\"${[0-9]{0,3}}\",\"status\":\"${(true|false)}\",\"data\":{\"patent_id\":\"ff6bc27d-ac2c-4bec-9094-debb19a40f70\",\"mlt\":[\"${count(2)}\"]}}";
        IJsonAssert.assertEqualsLenientByRegx(actual, expect3);
    }

}
