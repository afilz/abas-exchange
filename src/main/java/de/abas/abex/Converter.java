/******************************************************************************
  Author             : Andreas.Filz@abas.de
  Date of Creation   : 04.09.2020
  Name               : de.abas.abex.Converter
  Arbeitspaket       : Final Exam
  Function           : Util class to convert abas date & time into the ISO 8601 format
*****************************************************************************/
package de.abas.abex;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import de.abas.erp.common.type.AbasDate;
import de.abas.erp.common.type.AbasTime;

public class Converter {

	/**
	 * Returns a ISO 8601 conform Date String
	 * @param abasDate
	 * @param abasTime
	 * @return A yyyy-MM-dd'T'HH:mm:ss formated String from the given abas date & time
	 */
	public static String abasDateTimeToISO8601(AbasDate abasDate, AbasTime abasTime) {
		SimpleDateFormat sdf;
		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		String[] time;

		if (abasTime != null) {
			time = abasTime.toString().split(":");// A get hours / minutes method within AbasTime would have been
													// lovely.
		} else {
			time = new String[2];
			time[0] = "23";
			time[1] = "59";
		}

		cal.setTime(abasDate.toDate());

		cal.set(Calendar.HOUR_OF_DAY, new Integer(time[0]));
		cal.set(Calendar.MINUTE, new Integer(time[1]));

		sdf.setTimeZone(TimeZone.getTimeZone("CET"));

		return sdf.format(cal.getTime());
	}
}
