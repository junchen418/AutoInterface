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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAssert {

    private static Logger logger = LoggerFactory.getLogger(IAssert.class);

    /**
     * 断言对象为null
     *
     * @param actual
     */
    public static void assertIsNull(Object actual) {
        try {
            assertThat(actual).isNull();
        } catch (AssertionError e) {
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }

    }

    /**
     * 断言actual为true
     *
     * @param actual
     */
    public static void assertIsTrue(Boolean actual) {
        try {
            assertThat(actual).isTrue();
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual.toString());
            logger.info("###################################### expected ######################################");
            logger.info("true");
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }

    }

    /**
     * 断言actual中包含expected
     *
     * @param actual
     * @param expected
     */
    public static void assertContainsSubsequence(String actual, String expected) {
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
     * 断言actual与expected相同
     *
     * @param actual
     * @param expected
     */
    public static void assertEqualse(String actual, String expected) {
        try {
            assertThat(actual).isEqualTo(expected);
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
     * 断言actual与expected相等
     *
     * @param actual
     * @param expected
     */
    public static void assertEqualse(Long actual, Long expected) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual与expected相等
     *
     * @param actual
     * @param expected
     */
    public static void assertEqualse(Float actual, Float expected) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual与expected布尔值相同
     *
     * @param actual
     * @param expected
     */
    public static void assertEqualse(boolean actual, boolean expected) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual与expected相等
     *
     * @param actual
     * @param expected
     */
    public static void assertEqualse(int actual, int expected) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual小于expected
     *
     * @param actual
     * @param expected
     */
    public static void assertLess(int actual, int expected) {
        try {
            assertThat(actual).isLessThan(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual小于等于expected
     *
     * @param actual
     * @param expected
     */
    public static void assertLessOrEqualse(int actual, int expected) {
        try {
            assertThat(actual).isLessThanOrEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual大于expected
     *
     * @param actual
     * @param expected
     */
    public static void assertGreater(int actual, int expected) {
        try {
            assertThat(actual).isGreaterThan(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual大于等于expected
     *
     * @param actual
     * @param expected
     */
    public static void assertGreaterOrEqualse(int actual, int expected) {
        try {
            assertThat(actual).isGreaterThanOrEqualTo(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(String.valueOf(actual));
            logger.info("###################################### expected ######################################");
            logger.info(String.valueOf(expected));
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual与expected相同（忽略大小写）
     *
     * @param actual
     * @param expected
     */
    public static void assertIgnoringEqualse(String actual, String expected) {
        try {
            assertThat(actual).isEqualToIgnoringCase(expected);
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
     * 断言actual与expected相同（忽略空格）
     *
     * @param actual
     * @param expected
     */
    public static void assertIgnoringWhitespaceEqualse(String actual, String expected) {
        try {
            assertThat(actual).isEqualToIgnoringWhitespace(expected);
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
     * 断言actual Json值与expecte Json值相同
     *
     * @param actual
     * @param expected
     */
    public static void assertJsonAllEntity(String actual, String expected) {
        try {
            assertThat(ZSON.parseJson(actual)).isEqualToComparingFieldByFieldRecursively(ZSON.parseJson(expected));
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
     * 断言actual Json值包含expecte Json值
     *
     * @param actual
     * @param expected
     */
    public static void assertJsonContainsEntity(String actual, Map<String, Object> expected) {
        try {
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
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual);
            logger.info("###################################### expected ######################################");
            logger.info(expected.toString());
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual Map值与expecte Map值相同
     *
     * @param actual
     * @param expected
     */
    public static <T> void assertMapAllEntity(Map<String, T> actual, Map<String, T> expected) {
        try {
            assertThat(actual).containsAllEntriesOf(expected);
        } catch (AssertionError e) {
            logger.info("###################################### actual ######################################");
            logger.info(actual.toString());
            logger.info("###################################### expected ######################################");
            logger.info(expected.toString());
            logger.info("###################################### assert result ######################################");
            logger.info(e.getMessage());
            throw new AssertionError(e);
        }
    }

    /**
     * 断言actual Map值包含expecte Map值
     *
     * @param actual
     * @param expected
     */
    public static <T> void assertMapContainsEntity(Map<String, T> actual, Map<String, T> expected) {
        if (expected.isEmpty()) {
            logger.info("###################################### actual ######################################");
            logger.info(actual.toString());
            logger.info("###################################### expected ######################################");
            logger.info(expected.toString());
            logger.info("###################################### assert result ######################################");
            logger.info("expected is empty");
            throw Failures.instance().failure("expected is empty");
        }
        for (Entry<String, T> entry : expected.entrySet()) {
            try {
                assertThat(actual).contains(entry);
            } catch (AssertionError e) {
                logger.info("###################################### actual ######################################");
                logger.info(actual.toString());
                logger.info("###################################### expected ######################################");
                logger.info(entry.toString());
                logger.info("###################################### assert result ######################################");
                logger.info(e.getMessage());
                throw new AssertionError(e);
            }
        }
    }

    /**
     * 断言是否json串
     *
     * @param string
     */
    public static void assertIsJsonStr(String string) {
        boolean result = true;
        try {
            ZSON.parseJson(string);
        } catch (Exception e) {
            result = false;
        }
        assertThat(result).isTrue();
    }

}
