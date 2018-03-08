package com.acn.gzmcs.smscombiner.file;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;

/**
 * 20180125
 * @author liao.ruizhou.act
 *
 */
public class FileFilter implements IOFileFilter {

	private String[] filters = null;
	private Date cutOffTime = null;
	private String pattern = "";
	
	public FileFilter(String p) {
		this.pattern = p;
	}
	
	@Override
	public boolean accept(File file) {
		long lLastModified = file.lastModified();
		Date dLastModified = new Date(lLastModified);
		if (dLastModified.after(this.cutOffTime))
			return false;
		String strFileName = file.getName();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(strFileName);
		boolean nameMatch = false;
		if (m.find()) {
			nameMatch = true;
			for (int j = 0; j < this.filters.length; ++j) {
				if (!this.filters[j].equals(m.group(j + 1))) {
					nameMatch = false;
					break;
				}
			}
		}
		return nameMatch;
	}

	@Override
	public boolean accept(File dir, String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setFilters(String[] filters) {
		this.filters = filters;
	}
	
	public void setCutOffTime(Date time) {
		this.cutOffTime = time;
	}
	
}
