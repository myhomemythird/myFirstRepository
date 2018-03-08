package com.acn.gzmcs.smscombiner.distributor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.acn.gzmcs.smscombiner.common.Time;
import com.acn.gzmcs.smscombiner.file.CodeTransitionOfFile;
import com.acn.gzmcs.smscombiner.file.SmsUpdater;

/**
 * 20180126
 * @author Ricahrd Liao
 *
 */
public class SmsBox {

	private static final Logger logger = Logger.getLogger(SmsBox.class);
	private static String outputLocation = System.getProperty("user.dir") + File.separator + "output";
	private SmsUpdater smsUpdater = null;
	private int numOfFile = 1;
	private String[] members = new String[0];
	private String name = "Default";
	private int capacity = 5000;
	private int numOfSmsCur = 0;
	private String folderName = "sub-folder";
	private String fileName = "file";
	
	public SmsBox() {
		String str = Time.getNowStrTime();
		this.setFolderName(str);
		this.setFileName(str);
	}
	
	public SmsBox(SmsUpdater smsUpdater, String name, String[] members) {
		this.smsUpdater = smsUpdater;
		this.name = name;
		this.members = members;
		String str = Time.getNowStrTime();
		this.setFolderName(str);
		this.setFileName(str);
	}
	
	public boolean isMember(String str) {
		if (null == str)
			return false;
		for (int i = 0; i < this.members.length; ++i) {
			if (str.trim().equals(this.members[i]))
				return true;
		}
		return false;
	}
	
	public boolean merge(File smsFile) {
		if (null == smsFile)
			return false;
		String strMergedFile = getNextMergedFile();
		merge(smsFile, strMergedFile);
		this.numOfSmsCur ++;
		return true;
	}
	
	public void wrap() {
		String strMergedFile = getNextMergedFile();
		if (this.numOfSmsCur > 0)
			wrap(strMergedFile);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getMembers() {
		return this.members;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public static String getOutputLocation() {
		return outputLocation;
	}

	public static void setOutputLocation(String outputLocation) {
		SmsBox.outputLocation = outputLocation;
	}
	
	private String getNextMergedFile() {
		String res = outputLocation + File.separator + this.folderName;
		mkDirs(res);
		res = res + File.separator + this.fileName;
		if (this.numOfSmsCur >= this.capacity) {
			wrap(res + "_" + this.name + "_" + this.numOfFile + ".txt");
			this.numOfFile ++;
			this.numOfSmsCur = 0;
		}
		return res + "_" + this.name + "_" + this.numOfFile + ".txt";
	}

	private void wrap(String mergedFile) {
		append(getLastLineOfMergedFile(this.numOfSmsCur), mergedFile);
	}

	private void merge(File smsFile, String smsMergeFileName) {
		File outSMSMergeFile = new File(smsMergeFileName);
		try {
			CodeTransitionOfFile ctof = new CodeTransitionOfFile();
			String smsLine = ctof.readSMSFile(smsFile);
			if (smsLine == null) {
				logger.info("There is no content in :  "
						+ smsFile.getAbsolutePath());
				return;
			}
			logger.debug("Read :  " + smsLine);
			smsLine = smsUpdater.process(smsLine);
			String smsLineBeWrite = ctof
					.appendSMSLine(outSMSMergeFile, smsLine);
			logger.debug("write :  " + smsLineBeWrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getLastLineOfMergedFile(int numOfMsg) {
		return "TRAIL001" + smsUpdater.getStrNextWorkingDate() + 
				String.format("%1$08d", new Object[] {numOfMsg});
	}

	private static void append(String content, String fileName) {
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.append("\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void mkDirs(String path) {
		if (null == path)
			return;
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}
	
}
