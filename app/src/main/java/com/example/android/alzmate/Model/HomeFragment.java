package com.example.android.alzmate.Model;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.EventDetail.EventAdapter;
import com.example.android.alzmate.EventDetail.EventHolder;
import com.example.android.alzmate.PersonDetail.PersonAdapter;
import com.example.android.alzmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends android.support.v4.app.Fragment {
    private ProgressDialog progressDialog;
    private EventAdapter eventAdapter;
    private ListView eventDisplayView;
    private ArrayList<EventHolder> informationEvent;
    private String patientName;
    private String imageURL;
    private String time;
    private DatabaseReference mPatientDatabaseReference;
    private DatabaseReference mEventDatabaseReference;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private TextView patientNameView;
    private TextView patientTimeView;
    private ImageView patientImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_home, container, false);
        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        patientNameView=(TextView)view.findViewById(R.id.patient_name);
        patientTimeView=(TextView)view.findViewById(R.id.today_date);
        patientImageView=(ImageView) view.findViewById(R.id.patient_image);
        eventDisplayView=(ListView) view.findViewById(R.id.event_list_view);
        informationEvent=new ArrayList<>();
        progressDialog=(ProgressDialog)new ProgressDialog(this.getContext());
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        mPatientDatabaseReference= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("patient");
        mEventDatabaseReference=FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("events");


    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage(getString(R.string.progress_dialog));
        progressDialog.show();
        mPatientDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                patientName=dataSnapshot.child("nameAlz").getValue().toString();
                imageURL=dataSnapshot.child("AlzImageURL").getValue().toString();
                patientNameView.setText(patientName);
                Date currentTime = Calendar.getInstance().getTime();
                String currentClockTime=currentTime.toString();
                time=currentClockTime.split(" ")[2]+"-April-2019";
                patientTimeView.setText(time);
                Glide.with(getContext()).load(imageURL).into(patientImageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mEventDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                informationEvent.clear();
                for(DataSnapshot eventSnapshot: dataSnapshot.getChildren())
                {
                    String currentUiD=eventSnapshot.getKey().toString();
                    if(currentUiD.equals(time)) {
                        for(DataSnapshot specficDateSnapshot:eventSnapshot.getChildren()) {
                            String title = specficDateSnapshot.child("title").getValue().toString();
                            String body = specficDateSnapshot.child("body").getValue().toString();
                            String event_time = specficDateSnapshot.child("time").getValue().toString();
                            EventHolder mTemporaryEvent = new EventHolder(title, body, event_time);
                            informationEvent.add(mTemporaryEvent);
                        }
                    }

                }
                progressDialog.dismiss();
                eventAdapter =new EventAdapter(getActivity(),informationEvent);
                eventDisplayView.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
