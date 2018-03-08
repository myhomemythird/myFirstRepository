package com.acn.gzmcs.smscombiner.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
	
	private static final String strDateFormat = "yyyy-MM-dd-HH.mm.ss";
	
	public static String getNowStrTime() {
		return getStrTime(strDateFormat);
	}
	
	public static String getStrTime(String format) {
		Date now = new Date();
		DateFormat timeformat = new SimpleDateFormat(format);
		String strnow = timeformat.format(now);
		return strnow;
	}
	
	public static String getStrTime(Date date) {
		DateFormat timeformat = new SimpleDateFormat(strDateFormat);
		return timeformat.format(date);
	}

	public static String getStrPreviousDate(int n, String format) {
		if (n <= 0)
			n = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(5, -n);
		Date date = calendar.getTime();
		DateFormat timeformat = new SimpleDateFormat(format);
		String strnow = timeformat.format(date);
		return strnow;
	}

	public static String getStrNextDate(int n, String format) {
		if (n <= 0)
			n = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.add(5, n);
		Date date = calendar.getTime();
		DateFormat timeformat = new SimpleDateFormat(format);
		String strnow = timeformat.format(date);
		return strnow;
	}

	public static String toNextDate(String dateStr) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date = new Date(date.getTime() + 86400000L);
		String strnow = sdf.format(date);
		return strnow;
	}

	public static Date toDate(String dateStr) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date toAddDate(String dateStr) {
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date(date.getTime() + 86400000L);
	}
}
