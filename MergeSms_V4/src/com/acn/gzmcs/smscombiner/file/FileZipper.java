package com.acn.gzmcs.smscombiner.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * 20180208
 * @author Richard Liao
 *
 */
public class FileZipper {

	private static final Logger logger = Logger.getLogger(FileZipper.class);
	private static final int BUFFER_LEN = 8192;
	
	private File zipFile;
	
	public FileZipper(String strZipFile) {
		try {
			this.zipFile = new File(new String(strZipFile.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean zipWithoutTopFolder(String strFolder) {
		if (null == strFolder)
			return false;
		File folder = new File(strFolder);
		if (!folder.exists()) {
			logger.info("Not found path: ["+ folder + "].");
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(this.zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			ZipOutputStream zos = new ZipOutputStream(cos);
			String baseDir = "";
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; ++i)
				zip(files[i], zos, baseDir);
			zos.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		} 
		return true;
	}
	
	public boolean zip(String strPath) {
		File file = new File(strPath);
		if (!file.exists()) {
			logger.info("Not found path: ["+ strPath + "].");
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(this.zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fos, new CRC32());
			ZipOutputStream zos = new ZipOutputStream(cos);
			String baseDir = "";
			zip(file, zos, baseDir);
			zos.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return false;
		} 
		return true;
	}
	
	public static void main(String[] args) {
		String folder = "Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\2018-01-26-19.47.09";
		String zipFile = "Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\2018-01-26-19.47.09.zip";
		System.out.println("Process Begin...");
		FileZipper fz = new FileZipper(zipFile);
		fz.zipWithoutTopFolder(folder);
		System.out.println("Process End");
	}
	
	private void zip(File file, ZipOutputStream zos, String baseDir) {
		logger.info("zipping: " + baseDir + file.getName());
		if (file.isDirectory()) 
			zipDirectory(file, zos, baseDir);
		else
			zipFile(file, zos, baseDir);
	}
	
	private void zipDirectory(File dir, ZipOutputStream zos, String baseDir) {
		if (!dir.exists())
			return;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; ++i)
			zip(files[i], zos, baseDir + dir.getName() + "/");
	}
	
	private void zipFile(File file, ZipOutputStream zos, String baseDir) {
		if (!file.exists())
			return;
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ZipEntry entry = new ZipEntry(baseDir + file.getName());
			zos.putNextEntry(entry);
			int len = 0;
			byte[] data = new byte[BUFFER_LEN];
			while ((len = bis.read(data, 0, BUFFER_LEN)) != -1)
				zos.write(data, 0, len);
			bis.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
