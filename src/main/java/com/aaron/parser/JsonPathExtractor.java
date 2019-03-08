package com.aaron.parser;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import java.math.BigInteger;
import java.util.LinkedHashMap;

/**
 * JsonPath解析类
 *
 * @author Aaron
 */
public class JsonPathExtractor {

    /**
     * DocumentContext实例
     */
    private DocumentContext dc = null;

    /**
     * @param json
     */
    public JsonPathExtractor(String json) {
        this.dc = JsonPath.parse(json);
    }

    /**
     * 返回Object
     *
     * @param jsonPath
     * @return
     */
    public Object getObject(String jsonPath) {
        return dc.read(jsonPath);
    }

    /**
     * 返回String
     *
     * @param jsonPath
     * @return
     */
    public String getString(String jsonPath) {
        return String.valueOf(getObject(jsonPath));
    }

    /**
     * 返回Integer
     *
     * @param jsonPath
     * @return
     */
    public Integer getInteger(String jsonPath) {
        return (Integer) getObject(jsonPath);
    }

    /**
     * 返回Double
     *
     * @param jsonPath
     * @return
     */
    public Double getDouble(String jsonPath) {
        return (Double) getObject(jsonPath);
    }

    /**
     * 返回BigInteger
     *
     * @param jsonPath
     * @return
     */
    public BigInteger getBigInteger(String jsonPath) {
        return (BigInteger) getObject(jsonPath);
    }

    /**
     * 返回Boolean
     *
     * @param jsonPath
     * @return
     */
    public Boolean getBoolean(String jsonPath) {
        return (Boolean) getObject(jsonPath);
    }

    /**
     * 返回LinkedHashMap
     *
     * @param jsonPath
     * @return
     */
    public LinkedHashMap<?, ?> getMap(String jsonPath) {
        return (LinkedHashMap<?, ?>) getObject(jsonPath);
    }

    /**
     * 返回JSONArray
     *
     * @param jsonPath
     * @return
     */
    public JSONArray getJSONArray(String jsonPath) {
        return (JSONArray) getObject(jsonPath);
    }
    
}
