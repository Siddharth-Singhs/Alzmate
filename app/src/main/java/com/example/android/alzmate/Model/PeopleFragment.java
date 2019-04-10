package com.example.android.alzmate.Model;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.MainActivity;
import com.example.android.alzmate.PersonDetail.Person;
import com.example.android.alzmate.PersonDetail.PersonAdapter;
import com.example.android.alzmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class PeopleFragment extends android.support.v4.app.Fragment {
    Dialog myDialog;
    private ProgressDialog progressDialog;
    private PersonAdapter personAdapter;
    private ListView personDisplayView;
    private ArrayList<Person> informationPerson;
    private  String mCurrentPersonName;
    private String mCurrentRelationship;
    private String mCurrentBio;
    private String mCurrentimageUrl;
    private DatabaseReference mPersonDatabase;
    private DatabaseReference mCareGiverDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private TextView careNameView;
    private TextView carePhoneView;
    private ImageView careImage;
    private String mobileNumber;
    private String careName;
    private String imageURL;
    private ImageView callNumberView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_people, container, false);
        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // Bundle bundle = new Bundle();
        //ArrayList<Person> obj = informationPerson;
        //bundle.putSerializable("informPerson", obj);
        myDialog = new Dialog(this.getContext());
        personDisplayView=(ListView) view.findViewById(R.id.person_result_list);
        informationPerson=new ArrayList<>();
        progressDialog=(ProgressDialog)new ProgressDialog(this.getContext());
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        mPersonDatabase= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("known-people");
        mCareGiverDatabase=FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("care-Taker");
        careNameView=(TextView)view.findViewById(R.id.care_name_get_text);
        carePhoneView=(TextView)view.findViewById(R.id.care_phone_get_text);
        careImage=(ImageView)view.findViewById(R.id.care_image);
        callNumberView=(ImageView)view.findViewById(R.id.care_phone_call_btn);
        callNumberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+mobileNumber));
                startActivity(intent);
            }
        });
        personDisplayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Person mCurrentPerson=(Person) adapterView.getItemAtPosition(position);
                mCurrentPersonName=mCurrentPerson.getName();
                mCurrentRelationship=mCurrentPerson.getRelationship();
                mCurrentBio=mCurrentPerson.getBio();
                mCurrentimageUrl=mCurrentPerson.getImageUrl();
                ShowPopup(view,mCurrentPersonName,mCurrentRelationship,mCurrentBio,mCurrentimageUrl);
            }

        });

    }
    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage(getString(R.string.progress_dialog));
        progressDialog.show();
        mCareGiverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mobileNumber=dataSnapshot.child("careTakerMobileNumber").getValue().toString();
                careName=dataSnapshot.child("careTakerName").getValue().toString();
                imageURL=dataSnapshot.child("careTakerPhotoLocation").getValue().toString();
                careNameView.setText(careName);
                carePhoneView.setText(mobileNumber);
                Glide.with(getContext()).load(imageURL).into(careImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mPersonDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                informationPerson.clear();
                for(DataSnapshot personSnapshot: dataSnapshot.getChildren())
                {

                    String currentUiD=personSnapshot.getKey().toString();
                    String name= personSnapshot.child("name").getValue().toString();
                    String relationship=personSnapshot.child("relation").getValue().toString();
                    String bio=personSnapshot.child("bio").getValue().toString();
                    String ImageUrl=personSnapshot.child("ImgLocation").getValue().toString();
                    Person mTemporaryPerson=new Person(name,relationship,bio,ImageUrl);
                    informationPerson.add(mTemporaryPerson);

                }
                progressDialog.dismiss();
                personAdapter =new PersonAdapter(getActivity(),informationPerson);
                personDisplayView.setAdapter(personAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void ShowPopup(View v,String name,String relationship,String bio,String imageUrl) {

        myDialog.setContentView(R.layout.activity_identifyface);
        ImageView imageView=(ImageView)myDialog.findViewById(R.id.profile_image);
        TextView  nameView=(TextView)myDialog.findViewById(R.id.name_get_text);
        TextView relationshipView=(TextView)myDialog.findViewById(R.id.relationship_get_text);
        TextView bioView=(TextView)myDialog.findViewById(R.id.bio_get_text);

        nameView.setText(name);
        relationshipView.setText(relationship);
        bioView.setText(bio);


        Glide.with(getContext()).load(imageUrl).into(imageView);
        TextView txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
