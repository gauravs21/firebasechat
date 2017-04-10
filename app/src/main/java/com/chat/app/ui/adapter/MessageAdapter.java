package com.chat.app.ui.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.utility.Constants;
import com.chat.app.utility.PrefsUtil;
import com.chat.app.utility.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/*
 * Created by kopite on 1/4/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private Context context;
    private ArrayList<ChatMessage> chatMessages;
    private int viewType = 2;
    private final int BUBBLE_SEND = 0;
    private final int BUBBLE_RECEIVED = 1;

    public MessageAdapter(Context context, ArrayList<ChatMessage> messageArrayList) {
        this.context = context;
        this.chatMessages = messageArrayList;

    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == BUBBLE_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.bubble_chat_received, parent, false);
            return new MessageHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.bubble_chat_send, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        final float SCALE = context.getResources().getDisplayMetrics().density;

        final ChatMessage chatMessage = chatMessages.get(position);
//        Log.e("DB", chatMessage.getFrom() + " " + chatMessage.getMessageBody() + " " + PrefsUtil.getEmail(context));

        LinearLayout.LayoutParams relativeParams =
                (LinearLayout.LayoutParams) holder.relativeLayoutParent.getLayoutParams();

        //if message from you
        if (chatMessage.getFrom().equals(PrefsUtil.getEmail(context))) {
            relativeParams.setMargins((int) (4 * SCALE + 0.5f), 0, (int) (16 * SCALE + 0.5f), 0);  // left, top, right, bottom

            holder.tvFrom.setTextColor(ContextCompat.getColor(context, R.color.chat_box_received));
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.chat_box_received));
            holder.tvMessageBody.setTextColor(ContextCompat.getColor(context, R.color.chat_box_received));
            holder.tvChatTime.setTextColor(ContextCompat.getColor(context, R.color.chat_box_received));
            holder.tvFileName.setTextColor(ContextCompat.getColor(context, R.color.chat_box_send));
            holder.tvFileSize.setTextColor(ContextCompat.getColor(context, R.color.chat_box_send));

        } else {
            relativeParams.setMargins((int) (16 * SCALE + 0.5f), 0, (int) (4 * SCALE + 0.5f), 0);  // left, top, right, bottom
            holder.tvFrom.setTextColor(ContextCompat.getColor(context, R.color.chat_box_send));
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.chat_box_send));
            holder.tvMessageBody.setTextColor(ContextCompat.getColor(context, R.color.chat_box_send));
            holder.tvChatTime.setTextColor(ContextCompat.getColor(context, R.color.chat_box_send));
            holder.tvFileName.setTextColor(ContextCompat.getColor(context, R.color.chat_box_received));
            holder.tvFileSize.setTextColor(ContextCompat.getColor(context, R.color.chat_box_received));
        }

        final String messageType = chatMessage.getMessageType();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        holder.tvFrom.setText(chatMessage.getFrom());
        holder.tvChatTime.setText(sdf.format(chatMessage.getTimestamp()));

        if (messageType.equals(Constants.MSG_TYPE.NORMAL)) {
            holder.tvMessageBody.setText(chatMessage.getMessageBody());
            holder.relativeLayout.setVisibility(View.GONE);
        } else {

            Log.e("DB","in else");
            switch (chatMessage.getMessageType()) {

                case Constants.MSG_TYPE.TEXT:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.txt);
                    holder.ivCoverImage.setBackground(ContextCompat.getDrawable(context,R.drawable.txt));

                    break;
                case Constants.MSG_TYPE.PPT:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.ppt);
                    holder.ivCoverImage.setBackground(ContextCompat.getDrawable(context,R.drawable.ppt));

                    Log.e("DB","in ppt");
                    break;
//                case Constants.DocumentExtension.PPTX:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.ppt);
//                    break;
                case Constants.MSG_TYPE.EXCEL_SHEET:
//                    holder.ivCoverImage.setBackground(R.drawable.xls);
                    holder.ivCoverImage.setBackground(ContextCompat.getDrawable(context,R.drawable.xls));
                    break;
//                case Constants.DocumentExtension.XLSX:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.xls);
//                    break;
                case Constants.MSG_TYPE.DOC:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.doc);
                    holder.ivCoverImage.setBackground(ContextCompat.getDrawable(context,R.drawable.doc));

                    break;
//                case Constants.DocumentExtension.DOCX:
//                    holder.ivCoverImage.setBackgroundResource(R.drawable.doc);
//                    break;
                case Constants.MSG_TYPE.PDF:
                    holder.ivCoverImage.setBackground(ContextCompat.getDrawable(context,R.drawable.pdf));

                    break;
                default:Log.e("DB","in switch");
            }


            holder.tvMessageBody.setText(chatMessage.getMessageType());
            holder.tvFileName.setText(chatMessage.getMessageBody());
            String fileSize = UserUtils.getFileSize(chatMessage.getFileLength());
            holder.tvFileSize.setText(fileSize);
            holder.ivDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl(chatMessage.getDownloadLink());
                    File rootPath = new File(Environment.getExternalStorageDirectory(), "Quorg");
                    if (!rootPath.exists()) {
                        rootPath.mkdirs();
                    }
                    try {
                        final File tempFile = File.createTempFile("documents", messageType);
                        final File localFile = new File(rootPath, chatMessage.getMessageBody());
                        storageRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                try {
                                    FileOutputStream fos = new FileOutputStream(tempFile);
                                    Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages == null ? 0 : chatMessages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvMessageBody, tvChatTime, tvFileName, tvFileSize;
        RelativeLayout relativeLayout, relativeLayoutParent;
        ImageView ivDownload, ivCoverImage;

        MessageHolder(View itemView) {
            super(itemView);
            tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
            tvMessageBody = (TextView) itemView.findViewById(R.id.tvMsgBody);
            relativeLayoutParent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            int width = context.getResources().getDisplayMetrics().widthPixels;
            tvMessageBody.setMaxWidth((int) (width * 0.8));
            tvChatTime = (TextView) itemView.findViewById(R.id.tvChatTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_document);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_doc_title);
            tvFileSize = (TextView) itemView.findViewById(R.id.tv_doc_size);
            ivDownload = (ImageView) itemView.findViewById(R.id.iv_download);
            ivCoverImage = (ImageView) itemView.findViewById(R.id.iv_cover_type);
            tvFileName.setMaxWidth((int) (width * 0.8));

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getFrom().equals(PrefsUtil.getEmail(context)))
            return BUBBLE_SEND;
        else
            return BUBBLE_RECEIVED;
    }

}
