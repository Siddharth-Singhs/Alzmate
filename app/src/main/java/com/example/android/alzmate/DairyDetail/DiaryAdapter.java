package com.example.android.alzmate.DairyDetail;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.alzmate.R;

import java.util.ArrayList;

public class DiaryAdapter extends ArrayAdapter<DiaryHolder>{
    private Activity context;
    private ArrayList<DiaryHolder> allDairy;
    public DiaryAdapter(@NonNull Activity context, ArrayList<DiaryHolder> allDairy) {
        super(context, R.layout.dairy_list_layout,allDairy);
        this.context=context;
        this.allDairy=allDairy;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        LayoutInflater inflater=context.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.dairy_list_layout,null,true);

        TextView title=(TextView)listItemView.findViewById(R.id.dairy_title_txt);
        TextView body=(TextView)listItemView.findViewById(R.id.dairy_body_text);
        TextView date=(TextView)listItemView.findViewById(R.id.dairy_date);
        TextView time=(TextView)listItemView.findViewById(R.id.dairy_time);
        //ImageView imageView=(ImageView)listItemView.findViewById(R.id.user_image);

        DiaryHolder currentDairy=allDairy.get(position);
        title.setText(currentDairy.getTitle());
        body.setText(currentDairy.getBody());
        date.setText(currentDairy.getDate());
        time.setText(currentDairy.getTime());


        return listItemView;
    }
}
