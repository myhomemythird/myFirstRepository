package com.acn.gzmcs.smscombiner.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeOfMergeAndSendSMS {
	private static final String TIME_MERGE_SEND_SMS = "Time.Merge.Send.SMS";
	private static SimpleDateFormat TodayFormat = new SimpleDateFormat(
			"yyyyMMdd");
	private static PropertyLoader propLoader = new PropertyLoader();
	private static String[] scheduleTimesFormProp = propLoader.getValue(
			"Time.Merge.Send.SMS").split(",");
	private Date dateToday;
	private String strToday;
	private String strCurMonth;
	private String strNextMonth;
	private String scheduleHour;
	private String[] aryNonworkDays;
	private String[] aryNonworkDays_NextMonth;

	public TimeOfMergeAndSendSMS() {
		this.dateToday = new Date();
		init();
	}

	public TimeOfMergeAndSendSMS(Date date) {
		this.dateToday = date;
		init();
	}

	private void init() {
		trimForAry(scheduleTimesFormProp);
		this.strToday = TodayFormat.format(this.dateToday);
		this.strCurMonth = this.strToday.substring(0, 6);
		this.scheduleHour = new SimpleDateFormat("HH").format(this.dateToday);
		this.strNextMonth = nextMonthOf(this.strCurMonth);
		String strNonworkDays = propLoader.getValue(this.strCurMonth);
		String strNonworkDays_NextMonth = propLoader
				.getValue(this.strNextMonth);
		if (strNonworkDays == null)
			strNonworkDays = "";
		this.aryNonworkDays = strNonworkDays.split(",");
		if (strNonworkDays_NextMonth == null)
			strNonworkDays_NextMonth = "";
		this.aryNonworkDays_NextMonth = strNonworkDays_NextMonth.split(",");
		trimForAry(this.aryNonworkDays);
		trimForAry(this.aryNonworkDays_NextMonth);
		trimForAry(scheduleTimesFormProp);
	}

	public String getStrToday() {
		return this.strToday;
	}

	public String[] getScheduleTimesFromProp() {
		return scheduleTimesFormProp;
	}

	public String getScheduleHour() {
		return this.scheduleHour + ":00";
	}

	private static void trimForAry(String[] ary) {
		for (int i = 0; i < ary.length; i++)
			ary[i] = ary[i].trim();
	}

	public boolean isNonwork(String Date) {
		String[] aryDays = this.aryNonworkDays;
		String strMonth = this.strCurMonth;
		String monthOfDate = Date.substring(0, 6);
		if (monthOfDate.equalsIgnoreCase(this.strNextMonth)) {
			aryDays = this.aryNonworkDays_NextMonth;
			strMonth = this.strNextMonth;
		}
		for (String day : aryDays) {
			if (Date.equalsIgnoreCase(strMonth + day))
				return true;
		}
		return false;
	}

	public String nextDateOf(String Date) {
		Calendar calInstance = Calendar.getInstance();
		Date dateTemp = null;
		try {
			dateTemp = TodayFormat.parse(Date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calInstance.setTime(dateTemp);
		int day = calInstance.get(5);

		calInstance.set(5, day + 1);

		String nextDate = TodayFormat.format(calInstance.getTime());
		return nextDate;
	}

	public String nextMonthOf(String month) {
		String strNextMonth = month;
		String strNextDate = month + "27";
		do {
			strNextDate = nextDateOf(strNextDate);
			strNextMonth = strNextDate.substring(0, 6);
		} while (month.equalsIgnoreCase(strNextMonth));
		return strNextMonth;
	}

	public String nextWorkingDateOf(String Date) {
		String nextWorkingDate = Date;
		do {
			nextWorkingDate = nextDateOf(nextWorkingDate);
		} while (isNonwork(nextWorkingDate));
		return nextWorkingDate;
	}

	public boolean isNextDayNonwork() {
		return isNonwork(nextDateOf(this.strToday));
	}

	public boolean isTodayNonwork() {
		return isNonwork(this.strToday);
	}
}
