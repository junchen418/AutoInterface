package com.aaron.assertion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.AbstractComparator;
import org.skyscreamer.jsonassert.comparator.JSONCompareUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.*;
import static org.skyscreamer.jsonassert.comparator.JSONCompareUtil.formatUniqueKey;

public class FuzzyComparator extends AbstractComparator {

    JSONCompareMode mode;

    protected Pattern replaceParamPattern = Pattern.compile("\\$\\{(.*)\\}");

    public FuzzyComparator(JSONCompareMode mode) {
        this.mode = mode;
    }

    @Override
    public void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result)
            throws JSONException {
        checkJsonObjectKeysExpectedInActual(prefix, expected, actual, result);
        if (!mode.isExtensible()) {
            checkJsonObjectKeysActualInExpected(prefix, expected, actual, result);
        }
    }

    protected void checkJsonObjectKeysExpectedInActual(String prefix, JSONObject expected, JSONObject actual,
                                                       JSONCompareResult result) throws JSONException {
        Set<String> expectedKeys = getKeys(expected);
        for (String key : expectedKeys) {
            Object expectedValue = expected.get(key);
            if (actual.has(key)) {
                Object actualValue = actual.get(key);
                compareValues(qualify(prefix, key), expectedValue, actualValue, result);
            } else {
                result.missing(prefix, key);
            }
        }
    }

    protected void checkJsonObjectKeysActualInExpected(String prefix, JSONObject expected, JSONObject actual,
                                                       JSONCompareResult result) {
        Set<String> actualKeys = getKeys(actual);
        for (String key : actualKeys) {
            if (!expected.has(key)) {
                result.unexpected(prefix, key);
            }
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
            throws JSONException {
        if (isSimpleValue(actualValue) && isSimpleValue(expectedValue)) {
            Matcher m = replaceParamPattern.matcher(String.valueOf(expectedValue));
            if (m.find()) {
                String replaceKey = m.group(1);
                if (!Pattern.compile(replaceKey).matcher(String.valueOf(actualValue)).matches()) {
                    result.fail(prefix + " Expected " + replaceKey + " matched with " + actualValue);
                }
                return;
            }
        }
        if (areNumbers(expectedValue, actualValue)) {
            if (areNotSameDoubles(expectedValue, actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
            } else if (expectedValue instanceof JSONObject) {
                compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
            } else if (!expectedValue.equals(actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else {
            result.fail(prefix, expectedValue, actualValue);
        }
    }

    @Override
    public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result)
            throws JSONException {
        if (mode == JSONCompareMode.STRICT || mode == JSONCompareMode.NON_EXTENSIBLE) {
            if (expected.length() != actual.length()) {
                result.fail(prefix + "[]: Expected " + expected.length() + " values but got " + actual.length());
                return;
            } else if (expected.length() == 0) {
                return; // Nothing to compare
            }
        }
        if (mode.hasStrictOrder()) {
            compareJSONArrayWithStrictOrder(prefix, expected, actual, result);
        } else if (allSimpleValues(expected)) {
            compareJSONArrayOfSimpleValues(prefix, expected, actual, result);
        } else if (allJSONObjects(expected)) {
            compareJSONArrayOfJsonObjects(prefix, expected, actual, result);
        } else {
            recursivelyCompareJSONArray(prefix, expected, actual, result);
        }
    }

    protected void compareJSONArrayOfSimpleValues(String key, JSONArray expected, JSONArray actual,
                                                  JSONCompareResult result) throws JSONException {
        Map<Object, Integer> expectedCount = JSONCompareUtil.getCardinalityMap(jsonArrayToList(expected));
        Map<Object, Integer> actualCount = JSONCompareUtil.getCardinalityMap(jsonArrayToList(actual));
        if (expectedCount.size() == 1 && isCountFun(String.valueOf(expectedCount.entrySet().iterator().next().getKey()))) {
            int count = 0;
            Matcher m = Pattern.compile("\\$\\{count\\((\\d+)\\)\\}").matcher(String.valueOf(expectedCount.entrySet().iterator().next().getKey()));
            if (m.find()) {
                count = Integer.valueOf(m.group(1));
            }
            if (count != actual.length()) {
                result.fail(key + "[]: Expected " + actual.toString() + " has " + count + " elements, but fount " + actual.length());
            }
            return;
        }
        if (expectedCount.size() == 1 && isRegex(String.valueOf(expectedCount.entrySet().iterator().next().getKey()))
                && mode.isExtensible()) {
            String replaceKey = null;
            for (Object o : actualCount.keySet()) {
                Matcher m = replaceParamPattern
                        .matcher(String.valueOf(expectedCount.entrySet().iterator().next().getKey()));
                if (m.find()) {
                    replaceKey = m.group(1);
                }
                if ("".equals(replaceKey) || replaceKey == null
                        || !Pattern.compile(replaceKey).matcher(String.valueOf(o)).find()) {
                    result.fail(key + "[]: Expected " + replaceKey + " matched with " + o);
                }
            }
            return;
        }
        for (Object o : expectedCount.keySet()) {
            if (!actualCount.containsKey(o)) {
                result.missing(key + "[]", o);
            } else if (!actualCount.get(o).equals(expectedCount.get(o))) {
                result.fail(key + "[]: Expected " + expectedCount.get(o) + " occurrence(s) of " + o + " but got "
                        + actualCount.get(o) + " occurrence(s)");
            }
        }
        for (Object o : actualCount.keySet()) {
            if (!expectedCount.containsKey(o)) {
                result.unexpected(key + "[]", o);
            }
        }
    }

    private boolean isCountFun(String valueOf) {
        Pattern countPatten = Pattern.compile("\\$\\{count\\((\\d+)\\)\\}");
        return countPatten.matcher(valueOf).find();
    }

    protected void compareJSONArrayOfJsonObjects(String key, JSONArray expected, JSONArray actual,
                                                 JSONCompareResult result) throws JSONException {
        String uniqueKey = findUniqueKey(expected);
        if (uniqueKey == null || !isUsableAsUniqueKey(uniqueKey, actual) || isRegex(expected.toString())) {
            recursivelyCompareJSONArray(key, expected, actual, result);
            return;
        }
        Map<Object, JSONObject> expectedValueMap = arrayOfJsonObjectToMap(expected, uniqueKey);
        Map<Object, JSONObject> actualValueMap = arrayOfJsonObjectToMap(actual, uniqueKey);
        for (Object id : expectedValueMap.keySet()) {
            if (!actualValueMap.containsKey(id)) {
                result.missing(formatUniqueKey(key, uniqueKey, id), expectedValueMap.get(id));
                continue;
            }
            JSONObject expectedValue = expectedValueMap.get(id);
            JSONObject actualValue = actualValueMap.get(id);
            compareValues(formatUniqueKey(key, uniqueKey, id), expectedValue, actualValue, result);
        }
        for (Object id : actualValueMap.keySet()) {
            if (!expectedValueMap.containsKey(id)) {
                result.unexpected(formatUniqueKey(key, uniqueKey, id), actualValueMap.get(id));
            }
        }
    }

    protected void recursivelyCompareJSONArray(String key, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
        Set<Integer> matched = new HashSet();
        for (int i = 0; i < expected.length(); ++i) {
            Object expectedElement = expected.get(i);
            boolean matchFound = false;
            StringBuilder build = new StringBuilder();
            for (int j = 0; j < actual.length(); ++j) {
                Object actualElement = actual.get(j);
                if (!matched.contains(j) && actualElement.getClass().equals(expectedElement.getClass())) {
                    if (expectedElement instanceof JSONObject) {
                        JSONCompareResult tmp = compareJSON((JSONObject) expectedElement, (JSONObject) actualElement);
                        if (tmp.passed()) {
                            matched.add(j);
                            matchFound = true;
                            break;
                        }
                        build.append(tmp.getMessage());
                    } else if (expectedElement instanceof JSONArray) {
                        JSONCompareResult tmp = compareJSON((JSONObject) expectedElement, (JSONObject) actualElement);
                        if (tmp.passed()) {
                            matched.add(j);
                            matchFound = true;
                            break;
                        }
                        build.append(tmp.getMessage());
                    } else if (expectedElement.equals(actualElement)) {
                        matched.add(j);
                        matchFound = true;
                        break;
                    }
                    else {
                        build.append("Expect " + expectedElement + " equals to " + actualElement);
                    }
                }
            }
            if (!matchFound) {
                result.fail(key + "[" + i + "] Could not find match for element " + expectedElement + "\n" + build.toString());
                return;
            }
        }
    }

    protected boolean isRegex(String id) {
        if (replaceParamPattern.matcher(id).find()) {
            return true;
        }
        return false;
    }

    protected boolean areNumbers(Object expectedValue, Object actualValue) {
        return expectedValue instanceof Number && actualValue instanceof Number;
    }

    protected boolean areNotSameDoubles(Object expectedValue, Object actualValue) {
        return ((Number) expectedValue).doubleValue() != ((Number) actualValue).doubleValue();
    }
}
