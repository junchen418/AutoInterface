package com.aaron.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * @author shenbing
 */

public class DbPoolUtil {
	private static Logger logger = LoggerFactory.getLogger(DbPoolUtil.class);
	private static DbPoolUtil dbPoolConnection = null;
	// 已加载数据源集合
	private static HashMap<String, DruidDataSource> ddsMap = new HashMap<String, DruidDataSource>();

	/**
	 * 返回DbPoolUtil实例
	 * 
	 * @return
	 */
	public static synchronized DbPoolUtil getInstance() {
		if (null == dbPoolConnection) {
			dbPoolConnection = new DbPoolUtil();
		}
		return dbPoolConnection;
	}

	/**
	 * 返回数据库连接
	 * 
	 * @param fullFile
	 *            数据库配置信息
	 * @param autoCommit
	 *            设置是否自动提交
	 * @return
	 */
	public DruidPooledConnection getConnection(String fullFile, boolean autoCommit) {
		DruidDataSource druidDataSource = null;
		DruidPooledConnection druidPooledConnection = null;
		Properties properties = loadProperties(fullFile);
		String name = properties.getProperty("name");
		try {
			if (ddsMap.containsKey(name)) {
				druidDataSource = ddsMap.get(name);
			} else {
				druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
				druidDataSource.setName(name);
				ddsMap.put(name, druidDataSource);
			}
			druidPooledConnection = druidDataSource.getConnection();
			druidPooledConnection.setAutoCommit(autoCommit);
			showPoolInfo(druidDataSource);
		} catch (Exception e) {
			logger.error("初始化数据库连接失败！");
			e.printStackTrace();
		}
		return druidPooledConnection;
	}

	/**
	 * 返回数据库连接,默认自动提交
	 * 
	 * @param fullFile
	 *            数据库配置信息
	 * @return
	 */
	public DruidPooledConnection getConnection(String fullFile) {
		return this.getConnection(fullFile, true);
	}

	/**
	 * 导入数据库配置信息,返回Properties实例
	 * 
	 * @param fullFile
	 * @return
	 */
	private static Properties loadProperties(String fullFile) {
		Properties properties = new Properties();
		if (fullFile == "" || "".equals(fullFile)) {
			logger.info("属性文件为空!");
		} else {
			InputStream inStream = DbPoolUtil.class.getClassLoader().getResourceAsStream(fullFile);
			try {
				properties.load(inStream);
			} catch (IOException e) {
				logger.error("数据库配置文件导入失败！");
				e.printStackTrace();
			}
		}
		return properties;
	}

	/**
	 * 日记记录数据池状态
	 * 
	 * @param dds
	 */
	public void showPoolInfo(DruidDataSource dds) {
		logger.debug("current dbAlias:" + dds.getName());
		logger.debug("active connection count:" + dds.getActiveCount());
		logger.debug("created connection count:" + dds.getCreateCount());
		logger.debug("max connection count:" + dds.getMaxActive());
	}
}
