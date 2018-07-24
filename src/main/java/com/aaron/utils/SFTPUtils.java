package com.aaron.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;

/**
 * SSH Utils
 * 
 * @author shenbing
 *
 */
public class SFTPUtils {
	Logger logger = Logger.getLogger(this.getClass());
	private String host = "";
	private String user = "";
	private int port = 22;
	private String password = "";
	private static final String PROTOCOL = "sftp";
	private JSch jsch = new JSch();
	private Session session;
	private Channel channel;
	private ChannelSftp sftp;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public SFTPUtils() {
	}

	public SFTPUtils(String host, int port, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	/**
	 * 建立ssh连接
	 * 
	 * @throws JSchException
	 */
	public SFTPUtils connect() throws JSchException {
		if (session == null) {
			session = jsch.getSession(user, host, port);
			UserInfo ui = new UserInfo() {

				@Override
				public void showMessage(String arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean promptYesNo(String arg0) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public boolean promptPassword(String arg0) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean promptPassphrase(String arg0) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public String getPassword() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getPassphrase() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			session.setUserInfo(ui);
			session.setPassword(password);
			session.connect();
			channel = session.openChannel(PROTOCOL);
			channel.connect();
			sftp = (ChannelSftp) channel;
		}
		return this;
	}

	/**
	 * 关闭ssh连接
	 */
	public void disconnect() {
		if (session != null) {
			session.disconnect();
			session = null;
		}
		if (channel != null) {
			channel.disconnect();
			channel = null;
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param localFileName
	 *            本地文件路径
	 * @param remoteFileName
	 *            服务器文件路径
	 * @return
	 */
	public boolean uploadFileToFile(String localFileName, String remoteFileName) throws Exception {
		boolean bSucc = false;
		try {
			if (isFile(remoteFileName)) {
				int index = remoteFileName.lastIndexOf("/");
				deteleFile(remoteFileName.substring(0, index), remoteFileName.substring(index + 1));
			}
			if (!isDir(remoteFileName.substring(0, remoteFileName.lastIndexOf("/")))) {
				mkdirs(remoteFileName.substring(0, remoteFileName.lastIndexOf("/")));
			}
			SftpProgressMonitor monitor = new SftpProgressMonitor() {

				@Override
				public void init(int arg0, String arg1, String arg2, long arg3) {
				}

				@Override
				public void end() {
					// TODO Auto-generated method stub
				}

				@Override
				public boolean count(long arg0) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			int mode = ChannelSftp.OVERWRITE;
			sftp.put(localFileName, remoteFileName, monitor, mode);
			bSucc = true;
		} catch (Exception e) {
			logger.error(e);
		}
		return bSucc;
	}

	/**
	 * 上传文件
	 * 
	 * @param localFileName
	 *            本地文件路径
	 * @param remoteFileDir
	 *            服务器目录
	 * @return
	 */
	public boolean uploadFileToDir(String localFileName, String remoteFileDir) throws Exception {
		boolean bSucc = false;
		try {
			if (!isDir(remoteFileDir)) {
				mkdir(remoteFileDir);
			}
			SftpProgressMonitor monitor = new SftpProgressMonitor() {

				@Override
				public void init(int arg0, String arg1, String arg2, long arg3) {
				}

				@Override
				public void end() {
					// TODO Auto-generated method stub
				}

				@Override
				public boolean count(long arg0) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			int mode = ChannelSftp.OVERWRITE;
			sftp.put(localFileName, remoteFileDir, monitor, mode);
			bSucc = true;
		} catch (Exception e) {
			logger.error(e);
		}
		return bSucc;
	}

	/**
	 * 上传目录
	 * 
	 * @param srcdir
	 *            本地文件目录
	 * @param destdir
	 *            远程服务器目录
	 * @throws Exception
	 * 
	 */
	public boolean uploadDirToDir(String srcdir, String destdir) throws Exception {
		boolean bSucc = false;
		try {
			if (!isDir(destdir)) {
				mkdir(destdir);
			}
			SftpProgressMonitor monitor = new SftpProgressMonitor() {

				@Override
				public void init(int arg0, String arg1, String arg2, long arg3) {
				}

				@Override
				public void end() {
					// TODO Auto-generated method stub
				}

				@Override
				public boolean count(long arg0) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			int mode = ChannelSftp.OVERWRITE;
			for (File file : new File(srcdir).listFiles()) {
				sftp.put(file.getAbsolutePath(), destdir, monitor, mode);
			}
			bSucc = true;
		} catch (Exception e) {
			logger.error(e);
		}
		return bSucc;
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 * @param fileName
	 * @return
	 */
	public boolean deteleFile(String directory, String fileName) {
		boolean flag = false;
		try {
			sftp.cd(directory);
			sftp.rm(fileName);
			flag = true;
		} catch (SftpException e) {
			flag = false;
			logger.error(e);
		}
		return flag;
	}

	/**
	 * 删除目录
	 * 
	 * @param directory
	 *            dir to be delete
	 * @return
	 */
	public String deleteDir(String directory) {
		String command = "rm -rf " + directory;
		String result = null;
		try {
			result = execCommand(command, true);
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * compress the files and sub-dir of directory into a zip named compressName
	 * 
	 * @param directory
	 *            the content directory to be compress
	 * @param compressName
	 *            the name in directory after it is compressed
	 * @throws SftpException
	 */
	public boolean compressDir(String directory, String compressName) throws SftpException {
		boolean flag = false;
		String command = "cd " + directory + "\nzip -r " + compressName + " ./"
				+ compressName.substring(0, compressName.lastIndexOf("."));
		try {
			execCommand(command, false);
			flag = true;
		} catch (Exception e) {
		}
		return flag;
	}

	/**
	 * download
	 * 
	 * @param localFileName
	 * @param remoteFileName
	 * @return
	 */
	public boolean download(String localFileName, String remoteFileName) {
		boolean bSucc = false;
		try {
			SftpProgressMonitor monitor = new SftpProgressMonitor() {

				@Override
				public void init(int arg0, String arg1, String arg2, long arg3) {
					// TODO Auto-generated method stub

				}

				@Override
				public void end() {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean count(long arg0) {
					// TODO Auto-generated method stub
					return true;
				}
			};
			sftp.get(remoteFileName, localFileName, monitor, ChannelSftp.OVERWRITE);
			bSucc = true;
		} catch (Exception e) {
			logger.error(e);
		}
		return bSucc;
	}

	/**
	 * execute command
	 * 
	 * @param command
	 * @param flag
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public String execCommand(String command, boolean flag) throws Exception {
		Channel channel = null;
		InputStream in = null;
		StringBuffer sb = new StringBuffer();
		try {
			channel = session.openChannel("exec");
			// logger.info("command:" + command);
			((ChannelExec) channel).setCommand("export TERM=ansi && " + command);
			((ChannelExec) channel).setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();
			if (flag) {
				byte[] tmp = new byte[10240];
				while (true) {
					while (in.available() > 0) {
						int i = in.read(tmp, 0, 10240);
						if (i < 0) {
							break;
						}
						sb.append(new String(tmp, 0, i, "UTF-8"));
					}
					if (channel.isClosed()) {
						break;
					}
				}
			}
			in.close();
		} finally {
		}
		return sb.toString();
	}

	/**
	 * get cpu info
	 * 
	 * @return
	 */
	public String[] getCpuInfo() {
		Channel channel = null;
		InputStream in = null;
		StringBuffer sb = new StringBuffer("");
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("export TERM=ansi && top -bn 1");// ansi一定要加
			in = channel.getInputStream();
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();
			byte[] tmp = new byte[10240];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 10240);
					if (i < 0) {
						break;
					}
					sb.append(new String(tmp, 0, i, "UTF-8"));
				}
				if (channel.isClosed()) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		String buf = sb.toString();
		if (buf.indexOf("Swap") != -1) {
			buf = buf.substring(0, buf.indexOf("Swap"));
		}
		if (buf.indexOf("Cpu") != -1) {
			buf = buf.substring(buf.indexOf("Cpu"), buf.length());
		}
		buf.replaceAll(" ", " ");
		return buf.split("\\n");
	}

	/**
	 * get hard disk info
	 * 
	 * @return
	 */
	public String getHardDiskInfo() throws Exception {
		Channel channel = null;
		InputStream in = null;
		StringBuffer sb = new StringBuffer("");
		try {
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("df -lh");
			in = channel.getInputStream();
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();

			byte[] tmp = new byte[10240];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 10240);
					if (i < 0) {
						break;
					}
					sb.append(new String(tmp, 0, i, "UTF-8"));
				}
				if (channel.isClosed()) {
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String buf = sb.toString();
		String[] info = buf.split("\n");
		if (info.length > 2) {// first line: Filesystem Size Used Avail Use%
								// Mounted on
			String tmp = "";
			for (int i = 1; i < info.length; i++) {
				tmp = info[i];
				String[] tmpArr = tmp.split("%");
				if (tmpArr[1].trim().equals("/")) {
					boolean flag = true;
					while (flag) {
						tmp = tmp.replaceAll(" ", " ");
						if (tmp.indexOf(" ") == -1) {
							flag = false;
						}
					}

					String[] result = tmp.split(" ");
					if (result != null && result.length == 6) {
						buf = result[1] + " total, " + result[2] + " used, " + result[3] + " free";
						break;
					} else {
						buf = "";
					}
				}
			}
		} else {
			buf = "";
		}
		return buf;
	}

	/**
	 * 返回空闲字节数
	 * 
	 * @return
	 * @throws Exception
	 */
	public double getFreeDisk() throws Exception {
		String hardDiskInfo = getHardDiskInfo();
		if (hardDiskInfo == null || hardDiskInfo.equals("")) {
			logger.error("get free harddisk space failed.....");
			return -1;
		}
		String[] diskInfo = hardDiskInfo.replace(" ", "").split(",");
		if (diskInfo == null || diskInfo.length == 0) {
			logger.error("get free disk info failed.........");
			return -1;
		}
		String free = diskInfo[2];
		free = free.substring(0, free.indexOf("free"));
		// System.out.println("free space:" + free);
		String unit = free.substring(free.length() - 1);
		// System.out.println("unit:" + unit);
		String freeSpace = free.substring(0, free.length() - 1);
		double freeSpaceL = Double.parseDouble(freeSpace);
		// System.out.println("free spaceL:" + freeSpaceL);
		if (unit.equals("K")) {
			return freeSpaceL * 1024;
		} else if (unit.equals("M")) {
			return freeSpaceL * 1024 * 1024;
		} else if (unit.equals("G")) {
			return freeSpaceL * 1024 * 1024 * 1024;
		} else if (unit.equals("T")) {
			return freeSpaceL * 1024 * 1024 * 1024 * 1024;
		} else if (unit.equals("P")) {
			return freeSpaceL * 1024 * 1024 * 1024 * 1024 * 1024;
		}
		return 0;
	}

	/**
	 * 获取指定目录下的所有子目录及文件
	 * 
	 * @param directory
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public List<String> listFiles(String directory) throws Exception {
		Vector fileList = null;
		List<String> fileNameList = new ArrayList<String>();
		fileList = sftp.ls(directory);
		Iterator it = fileList.iterator();
		while (it.hasNext()) {
			String fileName = ((ChannelSftp.LsEntry) it.next()).getFilename();
			if (fileName.startsWith(".") || fileName.startsWith("..")) {
				continue;
			}
			fileNameList.add(fileName);
		}
		return fileNameList;
	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 * @return
	 */
	public boolean mkdir(String path) {
		boolean flag = false;
		try {
			sftp.mkdir(path);
			flag = true;
		} catch (SftpException e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 创建级联目录
	 * 
	 * @param path
	 * @return
	 */
	public boolean mkdirs(String path) {
		boolean flag = false;
		try {
			execCommand("mkdir -p " + path, true);
			flag = true;
		} catch (SftpException e) {
			flag = false;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 判断是否是文件
	 * 
	 * @param path
	 * @return
	 */
	public boolean isFile(String path) {
		Boolean flag = false;
		try {
			String result = execCommand("find " + path + " -type f", true);
			if (result.equals(path + "\n")) {
				flag = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	/**
	 * 判断是否是目录
	 * 
	 * @param path
	 * @return
	 */
	public boolean isDir(String path) {
		Boolean flag = false;
		try {
			String result = execCommand("find " + path + " -type d", true);
			if (result.contains(path + "\n")) {
				flag = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	/**
	 * 重命名文件
	 * 
	 * @param src
	 *            原文件名
	 * @param dest
	 *            目标文件名
	 * @return
	 */
	public boolean rename(String src, String dest) {
		Boolean flag = false;
		try {
			execCommand("mv " + src + " " + dest, true);
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	/**
	 * 返回文件大小，单位byte
	 */
	public long getFileSize(String filePath) {
		long size = 0L;
		try {
			String result = execCommand("ls -lt " + filePath + " | awk '{print int($5)}'", true);
			size = Long.parseLong(result.replaceAll("\r|\n", ""));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return size;
	}
}