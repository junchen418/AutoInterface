package com.zf.zson.result;

import java.util.List;
import java.util.Map;
import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.path.ZsonPath;
import com.zf.zson.result.info.ZsonResultInfo;
import com.zf.zson.result.utils.ZsonResultRestore;
import com.zf.zson.result.utils.ZsonResultToString;

public abstract class ZsonResultAbstract implements ZsonResult {

	protected ZsonResultInfo zResultInfo;

	protected ZsonPath zPath;

	protected ZsonResultToString zsonResultToString;

	protected ZsonResultRestore zsonResultRestore;

	public ZsonResultAbstract() {
		zResultInfo = new ZsonResultInfo();
		zPath = new ZsonPath();
		zsonResultToString = new ZsonResultToString();
		zsonResultRestore = new ZsonResultRestore(this);
	}

	protected abstract void checkValid();

	public ZsonResultInfo getzResultInfo() {
		return zResultInfo;
	}

	public ZsonPath getzPath() {
		return zPath;
	}

	public ZsonResultToString getZsonResultToString() {
		return zsonResultToString;
	}

	public ZsonResultRestore getZsonResultRestore() {
		return zsonResultRestore;
	}

	public String getElementKey(Object value) {
		ZsonObject<String> keyObj = new ZsonObject<String>();
		keyObj.objectConvert(value);
		String key = null;
		if (keyObj.isMap()) {
			key = keyObj.getZsonMap().get(ZsonUtils.LINK);
		} else if (keyObj.isList()) {
			key = keyObj.getZsonList().get(0);
		} else {
			key = null;
		}
		return key;
	}

	/**
	 * 将在collections中获取到的值给重新的还原，并返回出去
	 * 
	 * @param value
	 * 
	 * @return
	 */
	public Object getCollectionsObjectAndRestore(Object value) {
		if (value instanceof Map || value instanceof List) {
			String key = this.getElementKey(value);
			Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
			Object elementObj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
			value = zsonResultToString.toJsonString(zsonResultRestore.restoreObject(elementObj));
		} else if (value instanceof String) {
			value = ZsonUtils.convert((String) value);
		}
		return value;
	}

	public Object getResultByKey(String key) {
		Map<String, Integer> elementStatus = zResultInfo.getIndex().get(key);
		Object obj = zResultInfo.getCollections().get(elementStatus.get(ZsonUtils.INDEX));
		return zsonResultRestore.restoreObject(obj);
	}

}
