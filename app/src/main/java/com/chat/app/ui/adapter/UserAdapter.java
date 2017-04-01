package com.chat.app.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.app.R;
import com.chat.app.model.User;
import com.chat.app.ui.activity.ChatScreen;

import java.util.ArrayList;

/*
 * Created by kopite on 29/3/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    private Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.users = userArrayList;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user_data, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        holder.textView.setText(users.get(position).getEmail());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatScreen.class);
                intent.putExtra(ChatScreen.EMAIL,users.get(holder.getAdapterPosition()).getEmail());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView textView;

        UserHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.row_tv_email);
            textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
