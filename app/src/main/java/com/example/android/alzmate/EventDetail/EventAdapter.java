package com.example.android.alzmate.EventDetail;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<EventHolder> {
    private Activity context;
    private ArrayList<EventHolder> allEvent;
    public EventAdapter(@NonNull Activity context, ArrayList<EventHolder> allEvent) {
        super(context, R.layout.event_list_layout,allEvent);
        this.context=context;
        this.allEvent=allEvent;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        LayoutInflater inflater=context.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.event_list_layout,null,true);

        //
        TextView title=(TextView)listItemView.findViewById(R.id.event_title_txt);
        TextView body=(TextView)listItemView.findViewById(R.id.event_body_text);
        TextView time=(TextView)listItemView.findViewById(R.id.event_tine);

        EventHolder currentEvent=allEvent.get(position);
        title.setText(currentEvent.getTitle());
        body.setText(currentEvent.getBody());
        time.setText(currentEvent.getTime());

        return listItemView;
    }
}
