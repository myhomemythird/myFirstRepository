package com.acn.gzmcs.smscombiner.file;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.acn.gzmcs.smscombiner.common.PropertyLoader;

/**
 * 20180125
 * @author liao.ruizhou.act
 *
 */
public class FileBatch {

	private static final Logger logger = Logger.getLogger(FileBatch.class);
	private static final PropertyLoader propLoader = new PropertyLoader();
	private static final String LOCATION = propLoader.getValue("hk.txt.inputFolder");
	private static final String FILE_NAME_PATTERN = ".+\\.(txt)\\.[\\w-]+";
	private Deque<File> fileList = null;
	private int batchSize = -1;
	private Date cutOffTime = new Date();
	
	public FileBatch(int batchSize, Date cutOffTime) {
		if (batchSize < 1 || null == cutOffTime)
			return;
		this.batchSize = batchSize;
		this.cutOffTime = cutOffTime;
	}
	
	public void scan() {
		logger.info("Scanning folder...");
		String[] filters = new String[]{"txt"};
		this.fileList = getFilesByFilters(LOCATION, FILE_NAME_PATTERN, filters, this.cutOffTime);
	}
	
	public boolean hasNextBatch() {
		return (this.fileList.size() > 0);
	}
	
	public File[] getNextBatch() {
		int len = Math.min(this.batchSize, this.fileList.size());
		ArrayList<File> temp = new ArrayList<File>();
		for (int i = 0; i < len; ++i) 
			temp.add(this.fileList.poll());
		return temp.toArray(new File[temp.size()]);
	}
	
	public static File getXmlFile(File txtFile) {
		if (null == txtFile)
			return null;
		String res = txtFile.getParent() + File.separator;
		String strFileName = txtFile.getName();
		strFileName = strFileName.substring(0, strFileName.lastIndexOf("."));
		strFileName = strFileName.substring(0, strFileName.lastIndexOf("."));
		res = res + strFileName + ".xml";
		return new File(res);
	}
	
	public static void main(String[] args) {
		String path = "Y:\\Richard\\javaWorkSpace_2\\MergeSms\\workbench";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = sdf.parse("20180126");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileBatch fb = new FileBatch(15, date);
		fb.scan();
		int n = 1;
		while(fb.hasNextBatch()) {
			System.out.println("Batch " + n++);
			File[] ary = fb.getNextBatch();
			for (int i = 0; i < ary.length; ++i) 
				System.out.println(ary[i].getName());
		}
		
	}
	
	private static Deque<File> getFilesByFilters(String path, String pattern, String[] filters, 
			Date cutOffTime) {
		Deque<File> fileList = new ArrayDeque<File>();
		FileFilter ff = new FileFilter(pattern);
		ff.setFilters(filters);
		ff.setCutOffTime(cutOffTime);
		Collection<File> files = FileUtils.listFiles(new File(path), ff, null);
		for (Iterator<File> it = files.iterator(); it.hasNext();) {
			File file = it.next();
			fileList.add(file);
		}
		return fileList;
	}

}
