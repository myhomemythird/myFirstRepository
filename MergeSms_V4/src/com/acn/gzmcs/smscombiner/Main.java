package com.acn.gzmcs.smscombiner;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.acn.gzmcs.smscombiner.cmd.ProcessUtils;
import com.acn.gzmcs.smscombiner.common.PropertyLoader;
import com.acn.gzmcs.smscombiner.common.EmailSender;
import com.acn.gzmcs.smscombiner.distributor.SmsDistributor;
import com.acn.gzmcs.smscombiner.file.FileBatch;
import com.acn.gzmcs.smscombiner.file.FileZipper;

/**
 * 20180126
 * @author liao.ruizhou.act
 *
 */
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class);
	private static final PropertyLoader PROPLOADER = new PropertyLoader();
	private static final String BACKUP_FOLDER = PROPLOADER
			.getValue("hk.txtxml.backup");
	private static final int NUM_OF_POLICY_PERBATCH = Integer.parseInt(PROPLOADER
			.getValue("number.of.policy.perbatch"));
	private static Date nowDate = null;
	private static SmsDistributor smsDistributor = null;
	private static EmailSender emailSender = null;
	private final ArrayBlockingQueue<File> fileProcessed = new ArrayBlockingQueue<File>(100);
	private final BackUpFileThread backupFileThread = new BackUpFileThread();
	private volatile boolean isMergingEnd = false;
	
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static void init() {
		nowDate = new Date();
		smsDistributor = new SmsDistributor(nowDate);
		emailSender = new EmailSender(nowDate);
	}
	
	public void doBatches() {
		this.backupFileThread.start();
		nowDate = new Date();
		boolean isNoSms = true;
		FileBatch fb = new FileBatch(NUM_OF_POLICY_PERBATCH, nowDate);
		fb.scan();
		while(fb.hasNextBatch()) {
			isNoSms = false;
			doBatch(fb.getNextBatch());
		}
		if (isNoSms) {
			emailSender.send();
			logger.info("no sms exist!");
		}
		this.isMergingEnd = true;
		if (!fileProcessed.isEmpty()) {
			try {
				logger.info("Backuping remaining files...");
				this.backupFileThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!this.backupFileThread.isInterrupted()) {
			this.backupFileThread.interrupt();
		}
	}
	
	public static void main(String[] args) {
		logger.info("Schedule Job SMS Merge Start!");
		Main main = new Main();
		Main.init();
		main.doBatches();
		logger.info("Schedule Job SMS Merge End!");
	}
	
	private void doBatch(File[] files) {
		Date date = new Date();
		smsDistributor.refresh(date);
		emailSender.refresh(date);
		for (int i = 0; i < files.length; i++) {
			logger.info("Merging " + (i + 1) + " of " + files.length + 
					" Sms File ["+ files[i].getName() + "]...");
			if (smsDistributor.save(files[i])) {
				try {
					fileProcessed.put(files[i]);
					File xmlFile = FileBatch.getXmlFile(files[i]);
					if (null != xmlFile && xmlFile.exists())
						fileProcessed.put(xmlFile);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		smsDistributor.wrap();
		zipAndSendFile(smsDistributor.getOutputFolder(), smsDistributor.getOutputFolder() + ".zip");
	}
	
	private void zipAndSendFile(String fileOrDirToBeZipped, String zipFilePath) {
		if (fileOrDirToBeZipped != null && zipFilePath != null) {
			FileZipper fz = new FileZipper(zipFilePath);
			boolean zipSucess = fz.zipWithoutTopFolder(fileOrDirToBeZipped);
			if (zipSucess) {
				String attachment = zipFilePath;
				emailSender.send(attachment);
			} else {
				logger.info("zip failed!");
			}
		} else {
			emailSender.send();
			logger.info("no sms exist!");
		}
	}

	private static void copyFile(String from, String to) {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("cmd");
		cmds.add("/c");
		cmds.add("copy");
		cmds.add(from);
		cmds.add(to);
		logger.debug(cmds.toString());
		boolean res = ProcessUtils.execute(10L, cmds);
		if (!res) {
			logger.error("The command is failed or timeout.");
			return;
		}
	}

	private static void delFile(String file) {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("cmd");
		cmds.add("/c");
		cmds.add("del");
		cmds.add(file);
		logger.debug(cmds.toString());
		boolean res = ProcessUtils.execute(10L, cmds);
		if (!res) {
			logger.error("The command is failed or timeout.");
			return;
		}
	}

	class BackUpFileThread extends Thread {
		public void run() {
			while (true) {
				try {
					if (isMergingEnd && fileProcessed.isEmpty())
						break;
					File file = fileProcessed.poll(1, TimeUnit.MINUTES);
					if (null != file) {
						copyFile(file.getAbsolutePath(), BACKUP_FOLDER);
						delFile(file.getAbsolutePath());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch blocks
					logger.debug("Thread Interrupted.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.debug("Thread Interrupted.");
				}
			}
		}
	}
}
