package com.example.android.alzmate.ChatDetail;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.R;

import java.util.ArrayList;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private Activity context;
    private ArrayList<ChatMessage> allChat;
    public ChatMessageAdapter(@NonNull Activity context, ArrayList<ChatMessage> allChat) {
        super(context, R.layout.my_message,allChat);
        this.context=context;
        this.allChat=allChat;

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        if (allChat.get(position).getName().equals("Patient"))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View listItemView=convertView;
        int type=getItemViewType(position);
        if(type==0)
        {
            listItemView = inflater.inflate(R.layout.my_message,parent,false);
        }
        else
        {
            listItemView = inflater.inflate(R.layout.their_message,parent,false);
        }
        TextView careGiverView;
        ImageView careGiverImageView;
        TextView messageBody=(TextView)listItemView.findViewById(R.id.message_body);

        ChatMessage singleMessage=allChat.get(position);
        messageBody.setText(singleMessage.getMessageText());
        if(type==1)
        {
            careGiverView=(TextView)listItemView.findViewById(R.id.care_name);
            careGiverImageView=(ImageView) listItemView.findViewById(R.id.avatar);
            careGiverView.setText(singleMessage.getCareTaker());
            Glide.with(getContext()).load(singleMessage.getImageURL()).into(careGiverImageView);
        }



        return listItemView;
    }
}
