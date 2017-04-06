package com.chat.app.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chat.app.R;
import com.chat.app.model.DocumentModel;
import com.chat.app.ui.adapter.DocumentAdapter;
import com.chat.app.utility.Constants;
import com.chat.app.utility.UserUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DocumentList extends AppCompatActivity {

//    private File sdcardObj = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

    private ArrayList<String> filelist = new ArrayList<>();
    private ArrayList<String> fileName = new ArrayList<>();
    RecyclerView recyclerView;
    ArrayList<DocumentModel> modelArrayList = new ArrayList<>();
    String type;
    int fileCount;
    ProgressBar progressBar;
    TextView tvReadMessage;

    private File sdcardObj = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    String secStore = System.getenv("SECONDARY_STORAGE");
    File f_secs = new File(secStore);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);
        progressBar = (ProgressBar) findViewById(R.id.read_document);
        tvReadMessage = (TextView) findViewById(R.id.tvReadMessage);
        modelArrayList.clear();
//        listFiles(sdcardObj, filelist);
//        listFiles(f_secs, filelist);

        new ReadDocuments().execute();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private class ReadDocuments extends AsyncTask<File, String, ArrayList<DocumentModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            tvReadMessage.setText(R.string.read_documents);
            Log.e("DBAsync", "on pre");
        }

        @Override
        protected ArrayList<DocumentModel> doInBackground(File... params) {
            listFiles(sdcardObj, filelist);
            listFiles(f_secs, filelist);
            Log.e("DBAsync", "in background");
            return modelArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<DocumentModel> documentModels) {
            super.onPostExecute(documentModels);
            Log.e("DBAsync", "post execute");
            tvReadMessage.setText("");
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setAdapter(new DocumentAdapter(DocumentList.this, filelist, fileName, documentModels));
        }
    }

    private void listFiles(File sdcard, ArrayList<String> fileList) {
        if (sdcard.isDirectory()) {
            File[] files = sdcard.listFiles();
            fileCount += files.length;
            try {
                for (File f : files) {
                    if (!f.isDirectory()) {
                        if (f.getName().endsWith(Constants.DocumentExtension.TXT)
                                || f.getName().endsWith(Constants.DocumentExtension.PPT)
                                || f.getName().endsWith(Constants.DocumentExtension.PPTX)
                                || f.getName().endsWith(Constants.DocumentExtension.XLS)
                                || f.getName().endsWith(Constants.DocumentExtension.XLSX)
                                || f.getName().endsWith(Constants.DocumentExtension.DOC)
                                || f.getName().endsWith(Constants.DocumentExtension.DOCX)
                                || f.getName().endsWith(Constants.DocumentExtension.PDF)) {
                            String extension = f.getName().substring(f.getName().lastIndexOf("."),
                                    f.getName().length());


                            long fileLength = f.length();

//
//                            if (fileSize > 1024) {
//                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
//                                double k= fileSize/1024;
//                                double m = (fileSize / 1024.0)/1024.0;
//                                double g = ((fileSize / 1024)/1024.0)/1024.0;
//                                if (g > 1) {
//                                    fileSize = g;
//                                    Log.e("DB", f.getName() + " " + String.valueOf( decimalFormat.format(fileSize)) + " GB");
//                                } else if (m > 1) {
//                                    fileSize = m;
//                                    Log.e("DB", f.getName() + " " + String.valueOf( decimalFormat.format(fileSize)) + " MB");
//                                } else {
//                                    fileSize = k;
//                                    Log.e("DB", f.getName() + " " + String.valueOf( decimalFormat.format(fileSize)) + " KB");
//                                }
//
//                            } else {
//                                Log.e("DB", f.getName() + " " + String.valueOf(fileSize) + " Bytes");
//                            }

                            modelArrayList.add(new DocumentModel(f.getAbsolutePath(), f.getName(),
                                    extension, fileLength));
                        }
                    } else {
                        listFiles(f, fileList);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }
}
