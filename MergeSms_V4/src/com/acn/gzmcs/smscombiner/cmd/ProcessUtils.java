package com.acn.gzmcs.smscombiner.cmd;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 20180126
 * @author liao.ruizhou.act
 *
 */
public class ProcessUtils {

	private static final Logger logger = Logger.getLogger(ProcessUtils.class);
	private static boolean isStop = false;
	
	public static boolean execute(final long timeout, List<String> cmds) {
		boolean res = false;
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(cmds);
		String cmd = pb.command().toString();
		logger.debug(cmd);
		Process pc = null;
		try {
			pc = pb.start();
			if (logger.isDebugEnabled()) {
				StreamHandler outHandler = new StreamHandler(cmd, pc.getInputStream());
				outHandler.start();
				StreamHandler errHandler = new StreamHandler(cmd, pc.getErrorStream());
				errHandler.start();
			}
			WatchDog watchDog = new WatchDog(pc, timeout);
			watchDog.start();
			int exitValue = -1;
			logger.debug("Wait CMD to process for " + timeout + " seconds...");
			exitValue = pc.waitFor();
			if (0 == exitValue)
				res = true;
			if (null != watchDog)
				watchDog.stop();
		} catch(InterruptedException e) {
			pc.destroy();
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} 
		return res;
	}
	
	public static void main(String[] args) {
//		String str = "[SQLPLUS, CDEMY1/Qbz1$Edc3#@RCS1PRD, @Y:\\Richard\\javaWorkSpace\\SQLExecutor\\workbench\\SQL_Files\\test.sql, Y:\\Richard\\javaWorkSpace\\SQLExecutor\\workbench\\Data\\Temporary\\Script_43_PRD_CDEMY1.csv];";
//		System.out.println(maskPassword(str));
	}
	
	static class WatchDog implements Runnable {

		private Process process = null;
		private long timeout = 0;
		
		public WatchDog(Process process, long timeout) {
			this.process = process;
			this.timeout = timeout;
		}
		
		public synchronized void start() {
			isStop = false;
			Thread watchDog = new Thread(this);
			watchDog.setDaemon(true);
			watchDog.start();
		}
		
		public synchronized void stop() {
			isStop = true;
			notifyAll();
		}
		
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			long limitTime = 0;
			synchronized (this) {
				limitTime = this.timeout * 1000L - (System.currentTimeMillis() - startTime);
				while (!isStop && limitTime > 0) {
					try {
						wait(limitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					limitTime = this.timeout * 1000L - (System.currentTimeMillis() - startTime);
				}
			}
			
			if (limitTime <= 0 && null != process) {
				try {
					process.exitValue();
				} catch (IllegalThreadStateException e) {
					process.destroy();
				}
			}
		}
	}

}
