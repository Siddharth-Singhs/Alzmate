package com.example.android.alzmate.Model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Calendar;

import com.bumptech.glide.Glide;
import com.example.android.alzmate.ChatDetail.ChatMessage;
import com.example.android.alzmate.ChatDetail.ChatMessageAdapter;
import com.example.android.alzmate.PersonDetail.PersonAdapter;
import com.example.android.alzmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.getSystemService;

public class ChatFragment extends android.support.v4.app.Fragment {
    public ImageButton sendButton;
    public EditText messageBody;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference mChatReference;
    private DatabaseReference mChatDisplayReference;
    private ProgressDialog progressDialog;
    private ArrayList<ChatMessage> storeMessage;
    private ChatMessageAdapter messgaeAdapter;
    private ListView chatDisplayView;
    private String careTaker;
    private String imageURL;
    private DatabaseReference mCareTakerReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View retView = inflater.inflate(R.layout.fragment_chat, container, false);
        return retView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendButton = (ImageButton)view.findViewById(R.id.msg_send_button);
        progressDialog=(ProgressDialog)new ProgressDialog(this.getContext());
        messageBody = (EditText)view.findViewById(R.id.msg_body);
        mAuth= FirebaseAuth.getInstance();
        storeMessage=new ArrayList<>();
        mCurrentUser=mAuth.getCurrentUser();
        chatDisplayView=(ListView) view.findViewById(R.id.messages_view);
        mChatReference= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(mCurrentUser.getUid());
        mChatDisplayReference= FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(mCurrentUser.getUid()).child("ChatMessage");
        mCareTakerReference=FirebaseDatabase.getInstance().getReference().child("PersonAlz").child(mCurrentUser.getUid()).child("care-Taker");
        

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageBody.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                String messageTime=currentTime.toString();
                String actualTime=messageTime.split(" ")[3]+" "+messageTime.split(" ")[1]+messageTime.split(" ")[2];
                if(!messageText.equals("")){
                    ChatMessage singleMessage=new ChatMessage("Patient",messageText,actualTime);
                    mChatReference.child("ChatMessage").child(actualTime).setValue(singleMessage);
                    messageBody.setText("");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage(getString(R.string.progress_dialog));
        progressDialog.show();
      mCareTakerReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              careTaker=dataSnapshot.child("careTakerName").getValue().toString();
              imageURL=dataSnapshot.child("careTakerPhotoLocation").getValue().toString();
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }

      });
     mChatDisplayReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              storeMessage.clear();
              for (DataSnapshot eachMessage:dataSnapshot.getChildren()) {
                  ChatMessage chatMessage;
                  String name=eachMessage.child("name").getValue().toString();
                  String messageText=eachMessage.child("messageText").getValue().toString();
                  String messageTime=eachMessage.child("messageTime").getValue().toString();
                  if(name.equals("Patient")) {
                      chatMessage = new ChatMessage(name, messageText, messageTime);
                  }
                  else
                  {
                      chatMessage=new ChatMessage(name,messageText,messageTime,careTaker,imageURL);
                  }
                  storeMessage.add(chatMessage);
              }
             // storeMessage.remove(storeMessage.size()-1);
              //storeMessage.remove(storeMessage.size()-1);
              progressDialog.dismiss();
              messgaeAdapter =new ChatMessageAdapter(getActivity(),storeMessage);
              chatDisplayView.setAdapter(messgaeAdapter);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }
}
