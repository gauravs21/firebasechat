package com.chat.app.ui.adapter;

/*
 * Created by kopite on 3/4/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.app.R;
import com.chat.app.model.DocumentModel;
import com.chat.app.utility.Constants;

import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentHolder> {
    private ArrayList<String> fileName;
    private ArrayList<String> filePath;
    private Context context;
    private ArrayList<DocumentModel> modelArrayList;

    public DocumentAdapter(Context context, ArrayList<String> filelist, ArrayList<String> fileName, ArrayList<DocumentModel> modelArrayList) {
        this.fileName = fileName;
        this.filePath = filelist;
        this.context = context;
        this.modelArrayList=modelArrayList;
    }

    @Override
    public DocumentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_document, parent, false);
        return new DocumentHolder(view);
    }

    @Override
    public void onBindViewHolder(DocumentHolder holder, int position) {
        DocumentModel model=modelArrayList.get(holder.getAdapterPosition());
//        Log.e("DBType",model.getType());
        holder.tvPathName.setText(model.getPath());
        holder.tvFileName.setText(model.getName());

        switch (model.getType()){
            case Constants.DocumentExtension.TXT:
                holder.ivDocumentType.setBackgroundResource(R.drawable.txt);
                break;
            case Constants.DocumentExtension.PPT:
                holder.ivDocumentType.setBackgroundResource(R.drawable.ppt);
                break;
            case Constants.DocumentExtension.PPTX:
                holder.ivDocumentType.setBackgroundResource(R.drawable.ppt);
                break;
            case Constants.DocumentExtension.XLS:
                holder.ivDocumentType.setBackgroundResource(R.drawable.xls);
                break;
            case Constants.DocumentExtension.XLSX:
                holder.ivDocumentType.setBackgroundResource(R.drawable.xls);
                break;
            case Constants.DocumentExtension.DOC:
                holder.ivDocumentType.setBackgroundResource(R.drawable.doc);
                break;
            case Constants.DocumentExtension.DOCX:
                holder.ivDocumentType.setBackgroundResource(R.drawable.doc);
                break;
            case Constants.DocumentExtension.PDF:
                holder.ivDocumentType.setBackgroundResource(R.drawable.pdf);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return modelArrayList == null ? 0 : modelArrayList.size();
    }

    class DocumentHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvPathName;
        ImageView ivDocumentType;

        DocumentHolder(View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.row_document_name);
            tvPathName = (TextView) itemView.findViewById(R.id.row_document_path);
            ivDocumentType = (ImageView) itemView.findViewById(R.id.row_document_iv_type);

        }
    }
}
