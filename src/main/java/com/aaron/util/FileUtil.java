package com.aaron.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * 文件工具类
 * 
 * @author shenbing
 */
public class FileUtil {
	private static final Logger log = Logger.getLogger(FileUtil.class);

	/**
	 * 创建目录
	 * 
	 * @param dir 目录
	 */
	public static void mkdir(String dir) {
		try {
			String dirTemp = dir;
			File dirPath = new File(dirTemp);
			if (!dirPath.exists()) {
				dirPath.mkdir();
			}
		} catch (Exception e) {
			log.error("创建目录操作出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param fileName String 包含路径的文件名 如:E:\phsftp\src\123.txt
	 * @param content  String 文件内容
	 * 
	 */
	public static void createNewFile(String fileName, String content) {
		try {
			String fileNameTemp = fileName;
			File filePath = new File(fileNameTemp);
			if (!filePath.exists()) {
				filePath.createNewFile();
			}
			FileWriter fw = new FileWriter(filePath);
			PrintWriter pw = new PrintWriter(fw);
			String strContent = content;
			pw.println(strContent);
			pw.flush();
			pw.close();
			fw.close();
		} catch (Exception e) {
			log.error("新建文件操作出错: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 删除文件
	 * 
	 * @param fileName 包含路径的文件名
	 */
	public static void delFile(String fileName) {
		try {
			String filePath = fileName;
			File delFile = new File(filePath);
			delFile.delete();
		} catch (Exception e) {
			log.error("删除文件操作出错: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath 文件夹路径
	 */
	public static void delFolder(String folderPath) {
		try {
			// 删除文件夹里面所有内容
			delAllFile(folderPath);
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			// 删除空文件夹
			myFilePath.delete();
		} catch (Exception e) {
			log.error("删除文件夹操作出错" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path 文件夹路径
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] childFiles = file.list();
		File temp = null;
		for (int i = 0; i < childFiles.length; i++) {
			// File.separator与系统有关的默认名称分隔符
			// 在UNIX系统上，此字段的值为'/'；在Microsoft Windows系统上，它为 '\'。
			if (path.endsWith(File.separator)) {
				temp = new File(path + childFiles[i]);
			} else {
				temp = new File(path + File.separator + childFiles[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + childFiles[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + childFiles[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param srcFile 包含路径的源文件 如：E:/phsftp/src/abc.txt
	 * @param dirDest 目标文件目录；若文件目录不存在则自动创建 如：E:/phsftp/dest
	 */
	public static void copyFile(String srcFile, String dirDest) {
		try {
			FileInputStream in = new FileInputStream(srcFile);
			mkdir(dirDest);
			FileOutputStream out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());
			int len;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			log.error("复制文件操作出错:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param oldPath String 源文件夹路径 如：E:/phsftp/src
	 * @param newPath String 目标文件夹路径 如：E:/phsftp/dest
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			// 如果文件夹不存在 则新建文件夹
			mkdir(newPath);
			File file = new File(oldPath);
			String[] files = file.list();
			File temp = null;
			for (int i = 0; i < files.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + files[i]);
				} else {
					temp = new File(oldPath + File.separator + files[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] buffer = new byte[1024 * 2];
					int len;
					while ((len = input.read(buffer)) != -1) {
						output.write(buffer, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
				}
			}
		} catch (Exception e) {
			log.error("复制文件夹操作出错:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 移动文件到指定目录
	 * 
	 * @param oldPath 包含路径的文件名 如：E:/phsftp/src/ljq.txt
	 * @param newPath 目标文件目录 如：E:/phsftp/dest
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 移动文件到指定目录，不会删除文件夹
	 * 
	 * @param oldPath 源文件目录 如：E:/phsftp/src
	 * @param newPath 目标文件目录 如：E:/phsftp/dest
	 */
	public static void moveFiles(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delAllFile(oldPath);
	}

	/**
	 * 移动文件到指定目录，会删除文件夹
	 * 
	 * @param oldPath 源文件目录 如：E:/phsftp/src
	 * @param newPath 目标文件目录 如：E:/phsftp/dest
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 解压zip文件
	 * 
	 * @param srcFile 压缩文件
	 * @param destDir 解压文件存放目录
	 * @throws Exception
	 */
	public static void unZip(String srcFile, String destDir) throws Exception {
		File source = new File(srcFile);
		if (source.exists()) {
			ZipInputStream zis = null;
			BufferedOutputStream bos = null;
			try {
				zis = new ZipInputStream(new FileInputStream(source), Charset.forName("UTF-8"));
				ZipEntry entry = null;
				File targetDir = new File(destDir);
				if (targetDir.exists() && targetDir.isDirectory()) {
					delFolder(destDir);
				}
				while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
					if (!targetDir.exists() || !targetDir.isDirectory()) {
						targetDir.mkdir();
					}
					File target = new File(targetDir, entry.getName());
					// 写入文件
					bos = new BufferedOutputStream(new FileOutputStream(target));
					int read = 0;
					byte[] buffer = new byte[1024 * 10];
					while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
						bos.write(buffer, 0, read);
					}
					bos.flush();
				}
				zis.closeEntry();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (zis != null) {
					zis.close();
				}
				if (bos != null) {
					bos.close();
				}
			}
		}
	}

	/**
	 * 压缩文件
	 * 
	 * @param srcDir     压缩前存放的目录
	 * @param targetName 压缩后的文件名
	 * @throws Exception
	 * @return 文件对象
	 */
	public static File zip(String srcDir, String targetName) throws Exception {
		File target = null;
		File source = new File(srcDir);
		if (source.exists()) {
			// 压缩文件名=源文件名.zip
			String zipName = targetName + ".zip";
			target = new File(source.getParent(), zipName);
			if (target.exists()) {
				target.delete(); // 删除旧的文件
			}
			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			try {
				fos = new FileOutputStream(target);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				// 添加对应的文件Entry
				addEntry(source, zos);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
		}
		return target;
	}

	/**
	 * 扫描添加文件Entry
	 * 
	 * @param source 源文件
	 * @param zos    Zip文件输出流
	 * @throws IOException
	 */
	private static void addEntry(File source, ZipOutputStream zos) throws IOException {
		// 按目录分级，形如：/aaa/bbb.txt
		if (source.isDirectory()) {
			File[] files = source.listFiles();
			for (File file : files) {
				// 递归列出目录下的所有文件，添加文件Entry
				addEntry(file, zos);
			}
		} else {
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				byte[] buffer = new byte[1024 * 10];
				fis = new FileInputStream(source);
				bis = new BufferedInputStream(fis, buffer.length);
				int read = 0;
				zos.putNextEntry(new ZipEntry(source.getName()));
				while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
					zos.write(buffer, 0, read);
				}
				zos.closeEntry();
			} finally {
				if (bis != null) {
					bis.close();
				}
				if (fis != null) {
					fis.close();
				}
			}
		}
	}

	/**
	 * 读取数据
	 * 
	 * @param inSream
	 * @param charsetName
	 * @return 内容
	 * @throws Exception
	 */
	public static String readData(InputStream inSream, String charsetName) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inSream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inSream.close();
		return new String(data, charsetName);
	}

	/**
	 * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static Set<String> readFile(String fileName) throws Exception {
		Set<String> datas = new HashSet<String>();
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while ((line = br.readLine()) != null) {
			datas.add(line);
		}
		br.close();
		fr.close();
		return datas;
	}

	/**
	 * 返回文件内容数据
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String readToString(String fileName) throws IOException {
		URL in = FileUtil.class.getClassLoader().getResource(fileName);
		File file = new File(in.getPath());
		List<String> lines = Files.readLines(file, Charsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		lines.forEach(line -> sb.append(line));
		return sb.toString();
	}

}