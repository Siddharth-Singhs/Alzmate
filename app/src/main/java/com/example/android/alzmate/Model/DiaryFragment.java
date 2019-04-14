package com.example.android.alzmate.Model;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.alzmate.CallBackInterface;
import com.example.android.alzmate.DairyDetail.DiaryAdapter;
import com.example.android.alzmate.DairyDetail.DiaryHolder;
import com.example.android.alzmate.DairyDetail.MessageHolder;
import com.example.android.alzmate.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class DiaryFragment extends android.support.v4.app.Fragment {
    Dialog myDialog;
    Dialog diaryCreateDialog;
    private ProgressDialog progressDialog;
    private DiaryAdapter diaryAdapter;
    private ListView dairyDisplayView;
    private DatabaseReference mDairyDatabase;
    private String mCurrentBody;
    private String mCurrentTitle;
    private FirebaseUser mCurrentUser;
    private CardView dairy_card_view;
    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;
    private FirebaseAuth mAuth;
    private Fragment_diary_create fragment_diary_create;
    private ArrayList<DiaryHolder> informationDairy;
    private Button btnCreate;
    private EditText eTitle;
    private EditText eContent ;
    private TextView txtclose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


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
        myDialog = new Dialog(this.getContext());
        diaryCreateDialog = new Dialog(this.getContext());
        fragment_diary_create =new Fragment_diary_create();
        progressDialog=(ProgressDialog)new ProgressDialog(this.getContext());
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        mDairyDatabase= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(user.getUid()).child("diary");

        final FloatingActionsMenu bfab = getView().findViewById(R.id.bfab);
        final FloatingActionButton badd_note  = getView().findViewById(R.id.Add_note);
        final FloatingActionButton badd_reminder  = getView().findViewById(R.id.Add_Reminder);

        dairyDisplayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DiaryHolder mdiaryhold = (DiaryHolder) adapterView.getItemAtPosition(i);
                mCurrentBody=mdiaryhold.getBody();
                mCurrentTitle=mdiaryhold.getTitle();
                ShowPopup(view,mCurrentTitle,mCurrentBody);
            }
        });

        Calendar cal = Calendar.getInstance();
        String dateandtime = cal.getTime().toString();
        final String cDate = dateandtime.split(" ")[2] + "-"+dateandtime.split(" ")[1] + "-" + dateandtime.split(" ")[5];
        final String cTime = dateandtime.split(" ")[3].split(":")[0]+dateandtime.split(" ")[3].split(":")[1];
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null){
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(fAuth.getCurrentUser().getUid()).child("diary").child(cDate).child(cTime);
        }



        bfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bfab.toggle();
            }
        });

        badd_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bfab.collapse();
                ShowDiaryEntryPopup();
            }
        });

        badd_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bfab.collapse();
            }
        });


    }

    private void setFragement(Fragment fragement) {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragement);
        fragmentTransaction.commit();

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

    public void ShowPopup(View v,String Title,String Body) {

        myDialog.setContentView(R.layout.diarydialog);
        TextView titleView=(TextView)myDialog.findViewById(R.id.title_get_text);
        TextView bodyView=(TextView)myDialog.findViewById(R.id.body_get_text);

        titleView.setText(Title);
        bodyView.setText(Body);


        TextView txtclose =(TextView) myDialog.findViewById(R.id.txt_close);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    private void ShowDiaryEntryPopup() {
        diaryCreateDialog.setContentView(R.layout.dairyentrydialog);
        final EditText eTitle = diaryCreateDialog.findViewById(R.id.etitle);
        final EditText eContent = diaryCreateDialog.findViewById(R.id.eDescription);
//        TextView txtclose =(TextView) diaryCreateDialog.findViewById(R.id.txt_close);

        Button btn_create = diaryCreateDialog.findViewById(R.id.create_note);


        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = eTitle.getText().toString().trim();
                String content = eContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)){
                    createNote(content,title);
                    diaryCreateDialog.dismiss();
                    //setFragement(diaryFragment);
                }
                else {
                    Snackbar.make(view,"Fill Empty Fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

//        txtclose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                diaryCreateDialog.dismiss();
//            }
//        });

        diaryCreateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        diaryCreateDialog.show();
    }

    public void createNote(final String Content, final String Title){
        progressDialog.setMessage("Adding To Database");
        progressDialog.show();
        if(fAuth.getCurrentUser()!=null){
            MessageHolder message = new MessageHolder(Title,Content);
            fNotesDatabase.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),"Note Added to DataBase",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(getActivity(),"Users is not Signed In",Toast.LENGTH_SHORT).show();
        }
    }



}


