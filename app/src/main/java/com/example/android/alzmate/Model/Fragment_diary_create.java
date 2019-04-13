package com.example.android.alzmate.Model;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.alzmate.CallbackInterfaceforDiaryCreate;
import com.example.android.alzmate.DairyDetail.MessageHolder;
import com.example.android.alzmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class Fragment_diary_create extends android.support.v4.app.Fragment {

    private ProgressDialog progressDialog;
    private BottomNavigationView mNavView;
    private Button btnCreate;
    private EditText eTitle;
    private EditText eContent ;
    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;
    CallbackInterfaceforDiaryCreate callbackInterfaceforDiaryCreate;


    public void setCallBackInterface(CallbackInterfaceforDiaryCreate callbackInterfaceforDiaryCreate) {
        this.callbackInterfaceforDiaryCreate = callbackInterfaceforDiaryCreate;
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this.getContext());
        btnCreate = getView().findViewById(R.id.create_note);
        eTitle = getView().findViewById(R.id.etitle);
        eContent = getView().findViewById(R.id.eDescription);
        mNavView=getView().findViewById(R.id.main_nav);




        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = eTitle.getText().toString().trim();
                String content = eContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)){
                    createNote(content,title);


                    //setFragement(diaryFragment);


                }
                else {
                    Snackbar.make(view,"Fill Empty Fields", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_fragment_diary_create, container, false);

        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar cal = Calendar.getInstance();
        String dateandtime = cal.getTime().toString();
        final String cDate = dateandtime.split(" ")[2] + "-"+dateandtime.split(" ")[1] + "-" + dateandtime.split(" ")[5];
        final String cTime = dateandtime.split(" ")[3].split(":")[0]+dateandtime.split(" ")[3].split(":")[1];
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null){
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(fAuth.getCurrentUser().getUid()).child("diary").child(cDate).child(cTime);
        }

    }

    private  void createNote(final String Content, final String Title){
        progressDialog.setMessage("Adding To Database");
        progressDialog.show();
        if(fAuth.getCurrentUser()!=null){
            MessageHolder message = new MessageHolder(Title,Content);
            fNotesDatabase.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();

                    if(callbackInterfaceforDiaryCreate!=null){
                        callbackInterfaceforDiaryCreate.callbackmethodforDiaryCreate();
                    }

                }
            });
        }else {
            Toast.makeText(getActivity(),"Users is not Signed In",Toast.LENGTH_SHORT).show();
        }
    }

    private void setFragement(Fragment fragement) {
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame,fragement);
        fragmentTransaction.commit();

    }



}
