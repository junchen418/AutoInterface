package com.aaron.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class DbExcuteSqlFile {
	private SQLExec sqlExec = new SQLExec();
	private String dbDriver;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	ArrayList<File> fileList = new ArrayList<File>();

	/**
	 * 
	 * @param dbProperties
	 *            数据库配置文件
	 * @param fileSet
	 *            执行sql文件集
	 */
	public DbExcuteSqlFile(String dbProperties, String[] fileSet) {
		Properties properties = new Properties();
		InputStream inStream = DbExcuteSqlFile.class.getClassLoader().getResourceAsStream(dbProperties);
		try {
			properties.load(inStream);
			dbDriver = properties.getProperty("driverClassName");
			dbUrl = properties.getProperty("url");
			dbUser = properties.getProperty("username");
			dbPwd = properties.getProperty("password");
			sqlExec.setDriver(dbDriver);
			sqlExec.setUrl(dbUrl);
			sqlExec.setUserid(dbUser);
			sqlExec.setPassword(dbPwd);
			for (int i = 0; i < fileSet.length; i++) {
				fileList.add(new File(fileSet[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param dbProperties
	 *            数据库配置文件
	 * @param sqlfile
	 *            执行sql文件
	 */
	public DbExcuteSqlFile(String dbProperties, String sqlfile) {
		Properties properties = new Properties();
		InputStream inStream = DbExcuteSqlFile.class.getClassLoader().getResourceAsStream(dbProperties);
		try {
			properties.load(inStream);
			dbDriver = properties.getProperty("driverClassName");
			dbUrl = properties.getProperty("url");
			dbUser = properties.getProperty("username");
			dbPwd = properties.getProperty("password");
			sqlExec.setDriver(dbDriver);
			sqlExec.setUrl(dbUrl);
			sqlExec.setUserid(dbUser);
			sqlExec.setPassword(dbPwd);
			fileList.add(new File(sqlfile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行sql文件
	 */
	public void excute() {
		for (File file : fileList) {
			sqlExec.setSrc(file);
			sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
			sqlExec.setPrint(true);
			sqlExec.setEncoding("utf8");
			sqlExec.setOutput(new File(System.getProperty("user.dir") + "/log/sql_out.log"));
			sqlExec.setProject(new Project());
			sqlExec.execute();
		}
	}

}
