package com.chat.app.utility;

/*
 * Created by kopite on 4/4/17.
 */

public class Constants {
    public static final long MAX_SIZE = 10485760;

    public static class DocumentExtension {
        public static final String TXT = ".txt";
        public static final String PPT = ".ppt";
        public static final String PPTX = ".pptx";
        public static final String XLS = ".xls";
        public static final String XLSX = ".xlsx";
        public static final String DOC = ".doc";
        public static final String DOCX = ".docx";
        public static final String PDF = ".pdf";
    }

    public class REQUEST_CODE {
        public static final int GET_DOC = 101;
        public static final int SELECT_FILE = 102;
        public static final int REQUEST_CAMERA = 103;
        public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 121;
    }

    public class MSG_TYPE {
        public static final String NORMAL = "normal";
        public static final String TEXT = "text";
        public static final String DOC = "word";
        public static final String PPT = "ppt";
        public static final String EXCEL_SHEET = "excel";
        public static final String PDF = "pdf";
        public static final String IMAGE = "image";
    }
}
