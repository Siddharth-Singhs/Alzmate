package com.example.android.alzmate.PersonDetail;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.R;


import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class PersonAdapter extends ArrayAdapter<Person> {

    private Activity context;
    private ArrayList<Person> allPerson;
    public PersonAdapter(@NonNull Activity context, ArrayList<Person> allPerson) {
        super(context, R.layout.person_list_layout,allPerson);
        this.context=context;
        this.allPerson=allPerson;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        LayoutInflater inflater=context.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.person_list_layout,null,true);

        //
        TextView name=(TextView)listItemView.findViewById(R.id.name_text);
        TextView relationship=(TextView)listItemView.findViewById(R.id.relationship_text);
        TextView bio=(TextView)listItemView.findViewById(R.id.bio_text);
        ImageView imageView=(ImageView)listItemView.findViewById(R.id.user_image);

        Person currentPerson=allPerson.get(position);
        name.setText(currentPerson.getName());
        relationship.setText(currentPerson.getRelationship());
        bio.setText(currentPerson.getBio());
        String imageUrl=currentPerson.getImageUrl();
        Glide.with(getContext()).load(imageUrl).into(imageView);

        return listItemView;
    }
}
