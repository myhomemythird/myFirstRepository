package com.acn.gzmcs.smscombiner.distributor;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.acn.gzmcs.smscombiner.common.PropertyLoader;
import com.acn.gzmcs.smscombiner.common.Time;
import com.acn.gzmcs.smscombiner.common.TimeOfMergeAndSendSMS;
import com.acn.gzmcs.smscombiner.file.SmsUpdater;

/**
 * 20180126
 * @author Richard Liao
 *
 */
public class SmsDistributor {

	private static final Logger logger = Logger.getLogger(SmsDistributor.class);
	private static final PropertyLoader propLoader = new PropertyLoader();
	private static final int MSG_CAPACITY = Integer.parseInt(propLoader.getValue("message.count"));
	private static final String OUTPUT_LOCATION = propLoader.getValue("hk.txt.outputFolder");
	private static String[] SMSGroupNames;
	private static HashMap<String, String> hashSMSGroupMembers = new HashMap<String, String>();
	private ArrayList<SmsBox> boxes = new ArrayList<SmsBox>();
	private Date date = null;
	private SmsUpdater smsUpdater = null;
	
	static {
		String strSMSGroup = propLoader.getValue("SMS.Group");
		SMSGroupNames = strSMSGroup.split(",");
		for (int i = 0; i < SMSGroupNames.length; i++) {
			SMSGroupNames[i] = SMSGroupNames[i].trim();
		}
		for (String group : SMSGroupNames) {
			if ((group != null) && (!"".equalsIgnoreCase(group))) {
				hashSMSGroupMembers.put(group, propLoader.getValue(group));
			}
		}
	}
	
	public SmsDistributor(Date date) {
		this.date = date;
		TimeOfMergeAndSendSMS today = new TimeOfMergeAndSendSMS(date);
		this.smsUpdater = new SmsUpdater(today.getStrToday());
	}
	
	public boolean save(File smsFile) {
		boolean res = false;
		String fileName = smsFile.getName();
		if (fileName.split("\\.").length > 2) {
			String fileType = fileName.split("\\.")[1];
			String letterType = fileName.split("\\.")[2];
			if ((fileType.equalsIgnoreCase("txt")) && (letterType.contains("-"))) {
				SmsBox box = null;
				int idx = indexOf(letterType);
				if (idx < 0) 
					box = getNewSmsBox(letterType);
				else
					box = this.boxes.get(idx);
				res = box.merge(smsFile);
			} else {
				logger.info("This isn't txt file");
				return false;
			}
		} else {
			logger.info("Subfolder or filename format error");
			return false;
		}
		return res;
	}
	
	public void refresh(Date date) {
		this.date = date;
		this.boxes.clear();
	}
	
	public void wrap() {
		for (int i = 0; i < this.boxes.size(); ++i)
			this.boxes.get(i).wrap();
	}
	
	public String getOutputFolder() {
		return OUTPUT_LOCATION + File.separator + Time.getStrTime(this.date);
	}
	
	public static void main(String[] args) {
		SmsDistributor sd = new SmsDistributor(new Date());
		File file1 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\2_544210_1_4917178_715000020_-2.txt.STA-C2");
		File file2 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\2_544207_1_4917172_715000020_-2.txt.REM-C2");
		File file3 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\1_544210_1_4917178_715000020_-2.txt.STA-C2");
		File file4 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\544209_1_4917174_715000020_-2.txt.PRE-C1");
		File file5 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\5004_544209_1_4917174_715000020_-2.txt.PRE-C1");
		File file6 = new File("Y:\\Richard\\javaWorkSpace_2\\MergeSms_V4\\workbench\\5003_544209_1_4917174_715000020_-2.txt.PRE-C1");
		sd.save(file1);
		sd.save(file2);
		sd.save(file3);
		sd.save(file4);
		sd.save(file5);
		sd.save(file6);
		sd.wrap();
	}
	
	private SmsBox getNewSmsBox(String type) {
		if (null == type)
			return null;
		String name = groupOf(type);
		String members = hashSMSGroupMembers.get(name);
		String[] aryMembers = new String[0];
		if (null != members) {
			aryMembers = members.split(",");
			for (int i = 0; i < aryMembers.length; ++i)
				aryMembers[i] = aryMembers[i].trim();
		}
		SmsBox res = new SmsBox(this.smsUpdater, name, aryMembers);
		SmsBox.setOutputLocation(OUTPUT_LOCATION);
		res.setCapacity(MSG_CAPACITY);
		res.setFolderName(Time.getStrTime(this.date));
		res.setFileName(Time.getStrTime(this.date));
		this.boxes.add(res);
		return res;
	}
	
	private int indexOf(String type) {
		if (null == type)
			return -1;
		for (int i = 0; i < this.boxes.size(); ++i) {
			if (this.boxes.get(i).isMember(type))
				return i;
		}
		return -1;
	}

	private static String groupOf(String member) {
		if (hashSMSGroupMembers.isEmpty()) {
			return "Default";
		}
		for (String group : SMSGroupNames) {
			if (((String) hashSMSGroupMembers.get(group)).contains(member)) {
				return group;
			}
		}
		return "Default";
	}

}
