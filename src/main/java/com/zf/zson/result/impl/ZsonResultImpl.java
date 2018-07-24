package com.zf.zson.result.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.zf.zson.ZsonUtils;
import com.zf.zson.object.ZsonObject;
import com.zf.zson.result.ZsonAction;
import com.zf.zson.result.ZsonResultAbstract;

public class ZsonResultImpl extends ZsonResultAbstract {

	public boolean isValid() {
		if (!zResultInfo.isValid() || zResultInfo.getCollections().size() == 0) {
			return zResultInfo.isValid();
		}
		if (!zResultInfo.isAllFinished()) {
			Collection<Map<String, Integer>> values = zResultInfo.getIndex().values();
			for (Map<String, Integer> map : values) {
				if (map.get(ZsonUtils.STATUS) == 0) {
					zResultInfo.setValid(false);
					zResultInfo.setAllFinished(true);
					return zResultInfo.isValid();
				}
			}
		}
		return zResultInfo.isValid();
	}

	public void checkValid() {
		if (!this.isValid()) {
			throw new RuntimeException(ZsonUtils.JSON_NOT_VALID);
		}
	}

	private void beforeHandle(String path) {
		this.checkValid();
		zPath.setPath(path);
		if (!zPath.checkPath()) {
			throw new RuntimeException("[" + path + "]" + "path is not valid!");
		}
	}

	private void resultHandle(ZsonAction za, String path, boolean isSingleResult) {
		this.beforeHandle(path);
		if (!za.before(this)) {
			return;
		}
		if (zPath.checkAbsolutePath()) {
			isSingleResult = true;
		}
		// List<String> levels = zResultInfo.getLevel();
		for (int i = 0; i < zResultInfo.getLevel().size(); i++) {
			ZsonObject<String> pathObject = new ZsonObject<String>();
			pathObject.objectConvert(zResultInfo.getPath().get(i));
			if (pathObject.isList()) {
				// List<String> pathList = pathObject.getZsonList();
				for (int j = 0; j < pathObject.getZsonList().size(); j++) {
					if (zPath.isMatchPath(pathObject.getZsonList().get(j))) {
						ZsonObject<Object> resultObject = new ZsonObject<Object>();
						resultObject.objectConvert(zResultInfo.getCollections().get(i));
						if (!(resultObject.isList())) {
							throw new RuntimeException("parse json error!");
						}
						List<Object> resultList = resultObject.getZsonList();
						Object value = resultList.get(j);
						za.process(this, value, pathObject.getZsonList().get(j));
						i += za.offset(this, value);
						if (isSingleResult) {
							za.after(this);
							return;
						}
					}
				}
			} else if (pathObject.isMap()) {
				// Map<String, String> pathMap = pathObject.getZsonMap();
				for (String k : pathObject.getZsonMap().keySet()) {
					if (zPath.isMatchPath(pathObject.getZsonMap().get(k))) {
						ZsonObject<Object> resultObject = new ZsonObject<Object>();
						resultObject.objectConvert(zResultInfo.getCollections().get(i));
						if (!(resultObject.isMap())) {
							throw new RuntimeException("parse json error!");
						}
						Map<String, Object> resultMap = resultObject.getZsonMap();
						Object value = resultMap.get(k);
						za.process(this, value, pathObject.getZsonMap().get(k));
						i += za.offset(this, value);
						if (isSingleResult) {
							za.after(this);
							return;
						}
					}
				}
			}
		}
		if (isSingleResult) {
			throw new RuntimeException("[" + path + "]" + "path is not valid!");
		}
		za.after(this);
	}

	public Object getValue(String path) {
		ZsonRetrieve zre = new ZsonRetrieve();
		this.resultHandle(zre, path, true);
		return zre.getResult().get(0);
	}

	@Override
	public Object getValue() {
		ZsonRetrieve zre = new ZsonRetrieve();
		this.resultHandle(zre, "", true);
		return zre.getResult().get(0);
	}

	public List<Object> getValues(String path) {
		ZsonRetrieve zre = new ZsonRetrieve();
		this.resultHandle(zre, path, false);
		return zre.getResult();
	}

	@Override
	public int getInteger(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof Long) {
			return new Long((Long) obj).intValue();
		} else {
			throw new RuntimeException("can not get int with path: " + path);
		}
	}

	@Override
	public long getLong(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof Long) {
			return (Long) obj;
		} else {
			throw new RuntimeException("can not get long with path: " + path);
		}
	}

	@Override
	public double getDouble(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof BigDecimal) {
			BigDecimal bigDecimal = (BigDecimal) obj;
			return bigDecimal.doubleValue();
		} else {
			throw new RuntimeException("can not get double with path: " + path);
		}
	}

	@Override
	public float getFloat(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).floatValue();
		} else {
			throw new RuntimeException("can not get float with path: " + path);
		}
	}

	@Override
	public boolean getBoolean(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else {
			throw new RuntimeException("can not get boolean with path: " + path);
		}
	}

	@Override
	public String getString(String path) {
		Object obj = this.getValue(path);
		if (obj instanceof String) {
			return (String) obj;
		} else {
			throw new RuntimeException("can not get String with path: " + path);
		}
	}

	@Override
	public void addValue(String path, int index, Object json) {
		ZsonAdd add = new ZsonAdd();
		add.setAddIndex(index);
		add.setAddJson(json);
		this.resultHandle(add, path, false);
	}

	@Override
	public void addValue(String path, String key, Object json) {
		ZsonAdd add = new ZsonAdd();
		add.setAddKey(key);
		add.setAddJson(json);
		this.resultHandle(add, path, false);
	}

	@Override
	public void addValue(int index, Object json) {
		ZsonAdd add = new ZsonAdd();
		add.setAddIndex(index);
		add.setAddJson(json);
		this.resultHandle(add, "", false);
	}

	@Override
	public void addValue(String key, Object json) {
		ZsonAdd add = new ZsonAdd();
		add.setAddKey(key);
		add.setAddJson(json);
		this.resultHandle(add, "", false);
	}

	@Override
	public Object getResult() {
		return this.getResultByKey(ZsonUtils.BEGIN_KEY);
	}

	@Override
	public void deleteValue(String path) {
		ZsonDelete delete = new ZsonDelete();
		this.resultHandle(delete, path, false);
	}

	@Override
	public void updateValue(String path, Object json) {
		ZsonUpdate update = new ZsonUpdate();
		update.setUpdateJson(json);
		this.resultHandle(update, path, false);
	}

}
