package com.chat.app.ui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.chat.app.R;
import com.chat.app.ui.adapter.DocumentAdapter;

import java.io.File;
import java.util.ArrayList;

public class DocumentList extends AppCompatActivity {

    private File sdcardObj = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    private ArrayList<String> filelist = new ArrayList<String>();
    private ArrayList<String> fileName = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_list);
        listFiles(sdcardObj, filelist);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_documents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DocumentAdapter(this, filelist, fileName));
    }

    private void listFiles(File sdcard, ArrayList<String> fileList) {
        if (sdcard.isDirectory()) {
            File[] files = sdcard.listFiles();

            try {
                for (File f : files) {
                    if (!f.isDirectory()) {
                        if (f.getName().endsWith(".doc") ||
                                f.getName().endsWith(".xls") ||
                                f.getName().endsWith(".docx") ||
                                f.getName().endsWith(".pdf") ||
                                f.getName().endsWith(".xsls")) {
                            // Log.d(" FILES",f.getName());
                            filelist.add(f.getAbsolutePath());
                            fileName.add(f.getName());
                            Log.e("DBList", f.getAbsolutePath());
                            Log.e("DBList", String.valueOf(filelist.size()));
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
