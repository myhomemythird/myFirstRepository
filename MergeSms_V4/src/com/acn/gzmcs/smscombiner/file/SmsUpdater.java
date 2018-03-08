package com.acn.gzmcs.smscombiner.file;

public class SmsUpdater {
	private String strNextWorkingDate;

	public SmsUpdater(String nextWorkingDate) {
		this.strNextWorkingDate = nextWorkingDate;
	}

	public String getStrNextWorkingDate() {
		return this.strNextWorkingDate;
	}

	public String process(String str) {
		String newStr = str.substring(0, 51);
		str = str.substring(51);
		newStr = newStr.substring(8, 43);
		return this.strNextWorkingDate + newStr + this.strNextWorkingDate + str;
	}
}
