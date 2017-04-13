package com.chat.app.utility;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * Created by kopite on 5/4/17.
 */

public class UserUtils {
    public static String getFileSize(double fileSize) {

        if (fileSize > 1024) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            double k = fileSize / 1024;
            double m = (fileSize / 1024.0) / 1024.0;
            double g = ((fileSize / 1024) / 1024.0) / 1024.0;
            if (g > 1) {
                fileSize = g;
                return String.valueOf(decimalFormat.format(fileSize)) + " GB";
            } else if (m > 1) {
                fileSize = m;
                return String.valueOf(decimalFormat.format(fileSize)) + " MB";
            } else {
                fileSize = k;
                return String.valueOf(decimalFormat.format(fileSize)) + " KB";
            }

        } else {
            return String.valueOf((long) fileSize) + " Bytes";
        }
    }

    public static boolean isNull(String text) {
        return text == null || text.trim().equalsIgnoreCase("null");
    }

    public static String getPrintableDate(Date currDate, Date prevDate) {
        String time = "";
        boolean printDate = false;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        String currentDate = sdf.format(currDate);
        String previous = sdf.format(prevDate);

        if (!currentDate.equals(previous))
            printDate = true;

        if (printDate) {
            long currentTime = System.currentTimeMillis();
            Date date = new Date(currentTime);
            String today = sdf.format(date);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            String yesterday = sdf.format(cal.getTimeInMillis());
            Log.e("DB", yesterday);
            if (currentDate.equalsIgnoreCase(today))
                time = "Today";
            else if (currentDate.equals(yesterday))
                time = "Yesterday";
            else
                time = currentDate;
        }

        return time;
    }
}
