package com.acn.gzmcs.smscombiner.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * 20180126
 * @author liao.ruizhou.act
 *
 */
public class StreamHandler extends Thread {
	
	private static final Logger logger = Logger.getLogger(StreamHandler.class);
	private String cmd = null;
	private InputStream is = null;
	
	public StreamHandler(String cmd, InputStream is) {
		this.cmd = cmd;
		this.is = is;
	}
	
	public void run() {
		try {
			logger.debug(this.cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
			String line = br.readLine();
			for (;line != null; line = br.readLine()) {
				logger.debug(line);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
}
