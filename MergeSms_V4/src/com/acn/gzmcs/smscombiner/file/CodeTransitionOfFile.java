package com.acn.gzmcs.smscombiner.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CodeTransitionOfFile {
	public static final String LINE_SEPARATOR = System
			.getProperty("line.separator");

	private String inStreamCharsetName = "UTF-8";
	private String outStreamCharsetName = "big5";

	public CodeTransitionOfFile() {
	}

	public CodeTransitionOfFile(String inStreamCharsetName,
			String outStreamCharsetName) {
		this.inStreamCharsetName = inStreamCharsetName;
		this.outStreamCharsetName = outStreamCharsetName;
	}

	public void setInStreamCharsetName(String inSCN) {
		this.inStreamCharsetName = inSCN;
	}

	public String getInStreamCharsetName() {
		return this.inStreamCharsetName;
	}

	public void setOutStreamCharsetName(String outSCN) {
		this.outStreamCharsetName = outSCN;
	}

	public String getOutStreamCharsetName() {
		return this.outStreamCharsetName;
	}

	public static String removeUTF8BOM(String str) {
		if (str.startsWith(BomInputStream.UTF8_BOM)) {
			str = str.substring(1);
		}
		return str;
	}

	public String readSMSFile(File in) throws IOException {
		FileInputStream fis = new FileInputStream(in);
		InputStreamReader isr = new InputStreamReader(fis,
				this.inStreamCharsetName);
		BufferedReader br = new BufferedReader(isr);

		String firstLine = br.readLine();

		br.close();
		isr.close();
		return firstLine.trim();
	}

	public String appendSMSLine(File out, String smsLine) throws IOException {
		if (smsLine == null)
			return null;
		if (!out.exists())
			out.createNewFile();
		String smsLineWithSep = removeUTF8BOM(smsLine) + LINE_SEPARATOR;
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(out, true);
			osw = new OutputStreamWriter(fos, this.outStreamCharsetName);
			bw = new BufferedWriter(osw);
			bw.write(smsLineWithSep);
			bw.close();
			osw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return smsLineWithSep;
	}
}
