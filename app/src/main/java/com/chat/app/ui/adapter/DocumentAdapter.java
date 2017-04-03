package com.chat.app.ui.adapter;

/*
 * Created by kopite on 3/4/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.app.R;

import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentHolder> {
    private ArrayList<String> fileName;
    private ArrayList<String> filePath;
    private Context context;

    public DocumentAdapter(Context context, ArrayList<String> filelist, ArrayList<String> fileName) {
        this.fileName = fileName;
        this.filePath = filelist;
        this.context = context;
    }

    @Override
    public DocumentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_message, parent, false);
        return new DocumentHolder(view);
    }

    @Override
    public void onBindViewHolder(DocumentHolder holder, int position) {
        holder.tvPathName.setText(filePath.get(position));
        holder.tvFileName.setText(fileName.get(position));
    }

    @Override
    public int getItemCount() {
        return fileName == null ? 0 : fileName.size();
    }

    class DocumentHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvPathName;

        DocumentHolder(View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.tvFrom);
            tvPathName = (TextView) itemView.findViewById(R.id.tvMsgBody);
        }
    }
}
