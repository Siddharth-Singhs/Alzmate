package com.example.android.alzmate.Model;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.alzmate.DairyDetail.DiaryAdapter;
import com.example.android.alzmate.DairyDetail.DiaryHolder;
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


public class DiaryFragment extends android.support.v4.app.Fragment {
    private ProgressDialog progressDialog;
    private DiaryAdapter diaryAdapter;
    private ListView dairyDisplayView;
    private DatabaseReference mDairyDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ArrayList<DiaryHolder> informationDairy;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_diary, container, false);
        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dairyDisplayView=(ListView) view.findViewById(R.id.diary_list_view);
        informationDairy=new ArrayList<>();
        progressDialog=(ProgressDialog)new ProgressDialog(this.getContext());
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        mDairyDatabase= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("diary");


    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage(getString(R.string.progress_dialog));
        progressDialog.show();
        mDairyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                informationDairy.clear();
                for(DataSnapshot dateSnapahot:dataSnapshot.getChildren())
                {
                    String date=dateSnapahot.getKey().toString();
                    for(DataSnapshot detailSnapshot:dateSnapahot.getChildren())
                    {
                        String title=detailSnapshot.child("title").getValue().toString();
                        String body=detailSnapshot.child("body").getValue().toString();
                        String time1=detailSnapshot.getKey().toString();
                        String time=time1.charAt(0)+""+time1.charAt(1)+":"+time1.charAt(2)+""+time1.charAt(3);
                        DiaryHolder mCurrentDairy=new DiaryHolder(title,body,date,time);
                        informationDairy.add(mCurrentDairy);

                    }
                }
                progressDialog.dismiss();
                diaryAdapter =new DiaryAdapter(getActivity(),informationDairy);
                dairyDisplayView.setAdapter(diaryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
