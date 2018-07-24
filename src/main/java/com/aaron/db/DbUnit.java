package com.aaron.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 
 * @author shenbing
 * 
 */
public class DbUnit {
	private Logger logger = LoggerFactory.getLogger(DbUnit.class);

	private String fileName = null;

	/**
	 * 构造函数，数据库配置文件默认config.properties
	 */
	public DbUnit() {
		this.fileName = "config.properties";
	}

	/**
	 * 构造函数
	 * 
	 * @param fileName
	 *            数据库配置文件
	 */
	public DbUnit(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 执行更新sql
	 * 
	 * @param sql
	 * @return
	 */
	public int update(String sql) {
		DruidPooledConnection connection = null;
		Statement sqlStament = null;
		try {
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.createStatement();
			logger.info(formatSql(sql));
			return sqlStament.executeUpdate(sql);
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return -1;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return -1;
		} finally {
			try {
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			;
		}
	}

	/**
	 * 批量执行更新sql
	 * 
	 * @param sqlList
	 * @return
	 */
	public int batchUpdate(ArrayList<String> sqlList) {
		DruidPooledConnection connection = null;
		Statement sqlStament = null;
		try {
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.createStatement();
			for (String sql : sqlList) {
				logger.info(formatSql(sql));
				sqlStament.executeUpdate(sql);
			}
			connection.commit();
			return sqlList.size();
		} catch (SQLException e) {
			logger.error("数据库操作失败：", e);
			return -1;
		} catch (Exception e) {
			logger.error("数据库操作失败：", e);
			return -1;
		} finally {
			try {
				connection.rollback();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 返回查询记录列名称
	 * 
	 * @param resultSet
	 *            数据库查询结果集
	 * @param column
	 *            查询结果列索引，从1开始
	 * @return
	 * @throws SQLException
	 */
	private String getColumnTypeName(ResultSet resultSet, int column) throws SQLException {
		String columnType = resultSet.getMetaData().getColumnTypeName(column);
		return columnType;
	}

	/**
	 * 返回查询记录指定列的值
	 * 
	 * @param resultSet
	 *            数据库查询结果集
	 * @param column
	 *            查询结果列索引，从1开始
	 * @return
	 * @throws SQLException
	 */
	private String getCellValue(ResultSet resultSet, int column) throws SQLException {
		String result = null;
		String columnType = null;
		columnType = getColumnTypeName(resultSet, column);
		if (columnType.equalsIgnoreCase("VARCHAR2") || columnType.equalsIgnoreCase("VARCHAR")
				|| columnType.equalsIgnoreCase("CHAR") || columnType.equalsIgnoreCase("mediumtext")) {
			result = resultSet.getString(column) == null ? "" : resultSet.getString(column);
		} else if (columnType.equalsIgnoreCase("TIMESTAMP")) {
			result = resultSet.getTimestamp(column) == null ? "" : String.valueOf(resultSet.getTimestamp(column));
		} else if (columnType.equalsIgnoreCase("CLOB")) {
			result = resultSet.getClob(column) == null ? "" : String.valueOf(resultSet.getClob(column));
		} else if (columnType.equalsIgnoreCase("NUMBER") || columnType.equalsIgnoreCase("INTEGER")
				|| columnType.equalsIgnoreCase("int")) {
			result = String.valueOf(resultSet.getInt(column)) == null ? "" : String.valueOf(resultSet.getInt(column));
		} else if (columnType.equalsIgnoreCase("DATE") || columnType.equalsIgnoreCase("datetime")) {
			result = resultSet.getDate(column) == null ? "" : String.valueOf(resultSet.getDate(column));
		} else if (columnType.equalsIgnoreCase("BLOB")) {
			result = resultSet.getDate(column) == null ? "" : String.valueOf(resultSet.getBlob(column));
		} else if (columnType.equalsIgnoreCase("bigint") || columnType.equalsIgnoreCase("decimal")) {
			result = resultSet.getBigDecimal(column) == null ? "" : String.valueOf(resultSet.getBigDecimal(column));
		}
		return result;

	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 * @return 返回List结果集
	 */
	public ArrayList<ArrayList<String>> queryOfList(String sql) {
		DruidPooledConnection connection = null;
		Statement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.createStatement();
			set = sqlStament.executeQuery(sql);
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
			while (set != null && set.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int i = 0; i < colCount; i++) {
					temp.add(getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 * @return 返回Map结果集
	 */
	public ArrayList<HashMap<String, String>> queryOfMap(String sql) {
		DruidPooledConnection connection = null;
		Statement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.createStatement();
			set = sqlStament.executeQuery(sql);
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			while (set != null && set.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 0; i < colCount; i++) {
					temp.put(rsmd.getColumnName(i + 1).toUpperCase(), getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param param
	 *            sql中的参数
	 * @return 返回List结果集
	 */
	public ArrayList<ArrayList<String>> queryOfList(String sql, String param) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, param));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setString(1, param);
			set = sqlStament.executeQuery();

			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
			while (set != null && set.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int i = 0; i < colCount; i++) {
					temp.add(getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param param
	 *            sql中的参数
	 * @return 返回Map结果集
	 */
	public ArrayList<HashMap<String, String>> queryOfMap(String sql, String param) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, param));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setString(1, param);
			set = sqlStament.executeQuery();

			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			while (set != null && set.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 0; i < colCount; i++) {
					temp.put(rsmd.getColumnName(i + 1).toUpperCase(), getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param param
	 *            sql中的参数
	 * @return 返回List结果集
	 */
	public ArrayList<ArrayList<String>> queryOfList(String sql, int param) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, param));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setInt(1, param);
			set = sqlStament.executeQuery();
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
			while (set != null && set.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int i = 0; i < colCount; i++) {
					temp.add(getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param param
	 *            sql中的参数
	 * @return 返回Map结果集
	 */
	public ArrayList<HashMap<String, String>> queryOfMap(String sql, int param) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, param));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setInt(1, param);
			set = sqlStament.executeQuery();

			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			while (set != null && set.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 0; i < colCount; i++) {
					temp.put(rsmd.getColumnName(i + 1).toUpperCase(), getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, param), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param x
	 *            sql中的参数
	 * @param y
	 *            sql中的参数
	 * @return 返回List结果集
	 */
	public ArrayList<ArrayList<String>> queryOfList(String sql, Object x, Object y) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, x, y));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setObject(1, x);
			sqlStament.setObject(2, y);
			set = sqlStament.executeQuery();
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
			while (set != null && set.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int i = 0; i < colCount; i++) {
					temp.add(getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, x, y), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, x, y), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param x
	 *            sql中的参数
	 * @param y
	 *            sql中的参数
	 * @return 返回Map结果集
	 */
	public ArrayList<HashMap<String, String>> queryOfMap(String sql, Object x, Object y) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, x, y));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			sqlStament.setObject(1, x);
			sqlStament.setObject(2, y);
			set = sqlStament.executeQuery();

			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			while (set != null && set.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 0; i < colCount; i++) {
					temp.put(rsmd.getColumnName(i + 1).toUpperCase(), getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, x, y), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, x, y), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param list
	 *            sql中的参数结合
	 * @return 返回List结果集
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<String>> queryOfList(String sql, ArrayList list) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, list.toArray()));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			for (int i = 0; i < list.size(); i++) {
				sqlStament.setObject(i + 1, list.get(i));
			}
			set = sqlStament.executeQuery();
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();
			while (set != null && set.next()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int i = 0; i < colCount; i++) {
					temp.add(getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, list.toArray()), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, list.toArray()), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 执行查询sql
	 * 
	 * @param sql
	 *            预编译sql
	 * 
	 * @param list
	 *            sql中的参数结合
	 * @return 返回Map结果集
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<HashMap<String, String>> queryOfMap(String sql, ArrayList list) {
		DruidPooledConnection connection = null;
		PreparedStatement sqlStament = null;
		ResultSet set = null;
		try {
			logger.info(formatSql(sql, list.toArray()));
			connection = DbPoolUtil.getInstance().getConnection(fileName);
			sqlStament = connection.prepareStatement(sql);
			for (int i = 0; i < list.size(); i++) {
				sqlStament.setObject(i + 1, list.get(i));
			}
			set = sqlStament.executeQuery();
			ResultSetMetaData rsmd = set.getMetaData();
			int colCount = rsmd.getColumnCount();
			ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
			while (set != null && set.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				for (int i = 0; i < colCount; i++) {
					temp.put(rsmd.getColumnName(i + 1).toUpperCase(), getCellValue(set, i + 1));
				}
				resultList.add(temp);
			}
			return resultList;
		} catch (SQLException e) {
			logger.error("数据库操作失败：" + formatSql(sql, list.toArray()), e);
			return null;
		} catch (Exception e) {
			logger.error("数据库操作失败：" + formatSql(sql, list.toArray()), e);
			return null;
		} finally {
			try {
				set.close();
				sqlStament.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 返回结果集中指定行列值
	 * 
	 * @param resultList
	 *            数据结果集
	 * @param colName
	 *            指定列名
	 * @param rowNum
	 *            指定行号，从0开始
	 * @return
	 */
	public String getValue(ArrayList<HashMap<String, String>> resultList, String colName, int rowNum) {
		try {
			HashMap<String, String> tempMap = resultList.get(rowNum);
			return tempMap.get(colName.toUpperCase());
		} catch (Exception e) {
			logger.error("从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + resultList.size() + "]请求行[" + rowNum + "]请求列" + colName,
					e);
			return null;
		}
	}

	/**
	 * 返回结果集中指定行列值
	 * 
	 * @param sql
	 *            查询sql
	 * @param colName
	 *            指定列名
	 * @param rowNum
	 *            指定行号，从0开始
	 * @return
	 */
	public String getValue(String sql, String colName, int rowNum) {
		ArrayList<HashMap<String, String>> listOfMap = new ArrayList<HashMap<String, String>>();
		try {
			listOfMap = queryOfMap(sql);
			HashMap<String, String> tempMap = listOfMap.get(rowNum);
			String value = tempMap.get(colName);
			return value;
		} catch (Exception e) {
			logger.error(formatSql(sql) + "从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + listOfMap.size() + "]请求行[" + rowNum
					+ "]请求列" + colName, e);
			return null;
		}
	}

	/**
	 * 返回结果集中指定行列值
	 * 
	 * @param resultList
	 *            数据结果集
	 * @param rowNum
	 *            指定列号，从0开始
	 * @param rowNum
	 *            指定行号，从0开始
	 * @return
	 */
	public Object getValue(ArrayList<ArrayList<String>> resultList, int rowNum, int colNum) {
		try {
			ArrayList<String> tempList = resultList.get(rowNum);
			return tempList.get(colNum);
		} catch (Exception e) {
			logger.error("从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + resultList.size() + "]请求行[" + rowNum + "]请求列" + colNum,
					e);
			return null;
		}
	}

	/**
	 * 返回结果集中指定行列值
	 * 
	 * @param sql
	 *            查询sql
	 * @param rowNum
	 *            指定列号，从0开始
	 * @param rowNum
	 *            指定行号，从0开始
	 * @return
	 */
	public String getValue(String sql, int rowNum, int colNum) {
		ArrayList<ArrayList<String>> listOfList = new ArrayList<ArrayList<String>>();
		try {
			listOfList = queryOfList(sql);
			ArrayList<String> tempList = listOfList.get(rowNum);
			String value = tempList.get(colNum);
			return value;
		} catch (Exception e) {
			logger.error(formatSql(sql) + "从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + listOfList.size() + "]请求行[" + rowNum
					+ "]请求列" + colNum, e);
			return null;
		}
	}

	/**
	 * 返回结果集中第一列指定行值
	 * 
	 * @param resultList
	 *            数据结果集
	 * @param colNum
	 *            指定列号，从0开始
	 * @return
	 */
	public String getValue(ArrayList<ArrayList<String>> resultList, int colNum) {
		try {
			return getValue(resultList, 0, colNum) == null ? "" : getValue(resultList, 0, colNum).toString();
		} catch (Exception e) {
			logger.error("从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + resultList.size() + "]请求列" + colNum, e);
			return null;
		}
	}

	/**
	 * 返回结果集中第一行指定列值
	 * 
	 * @param resultList
	 *            数据结果集
	 * @param colname
	 *            指定列名
	 * @return
	 */
	public String getValue(ArrayList<HashMap<String, String>> resultList, String colname) {
		try {

			return getValue(resultList, colname, 0) == null ? "" : getValue(resultList, colname, 0).toString();
		} catch (Exception e) {
			logger.error("从数据库查询到的结果集中获取指定列的值发生异常，结果集size[" + resultList.size() + "]请求列" + colname, e);
			return null;
		}
	}

	/**
	 * 格式化sql
	 * 
	 * @param presql
	 *            预编译sql
	 * @param args
	 *            sql中参数值数组
	 * @return
	 */
	public String formatSql(String presql, Object... args) {
		for (int i = 0; i < args.length; i++) {
			presql = presql.replaceFirst("\\?", String.valueOf(args[i]));
		}
		return presql;
	}

}
