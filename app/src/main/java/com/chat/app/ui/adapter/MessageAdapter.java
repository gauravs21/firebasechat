package com.chat.app.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.app.R;
import com.chat.app.model.ChatMessage;
import com.chat.app.utility.Constants;
import com.chat.app.utility.PrefsUtil;

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
        ChatMessage chatMessage = chatMessages.get(position);
        String messageType = chatMessage.getMessageType();
        if (messageType.equals(Constants.MSG_TYPE.NORMAL)) {
            holder.tvFrom.setText(chatMessage.getFrom());
            holder.tvMessageBody.setText(chatMessage.getMessageBody());
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
            holder.tvChatTime.setText(sdf.format(chatMessage.getTimestamp()));
            holder.relativeLayout.setVisibility(View.GONE);
        } else {
            holder.tvMessageBody.setVisibility(View.GONE);
//            holder.tvFileName.setText();

        }

    }

    @Override
    public int getItemCount() {
        return chatMessages == null ? 0 : chatMessages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvMessageBody, tvChatTime, tvFileName, tvFileSize;
        RelativeLayout relativeLayout;
        ImageView ivDownload, ivCoverImage;

        MessageHolder(View itemView) {
            super(itemView);
            tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
            tvMessageBody = (TextView) itemView.findViewById(R.id.tvMsgBody);
            int width = context.getResources().getDisplayMetrics().widthPixels;
            tvMessageBody.setMaxWidth((int) (width * 0.8));
            tvChatTime = (TextView) itemView.findViewById(R.id.tvChatTime);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_document);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_doc_title);
            tvFileSize = (TextView) itemView.findViewById(R.id.tv_doc_size);
            ivDownload = (ImageView) itemView.findViewById(R.id.iv_download);
            ivCoverImage = (ImageView) itemView.findViewById(R.id.iv_cover_type);
            tvFileName.setMaxWidth((int) (width*0.8));
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
