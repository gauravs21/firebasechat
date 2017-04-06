package com.chat.app.utility;

import java.text.DecimalFormat;

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
            return String.valueOf((long)fileSize) + " Bytes";
        }
    }
}
