package com.example.android.alzmate.LoginModel;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.alzmate.MainActivity;
import com.example.android.alzmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signUpButton;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth=FirebaseAuth.getInstance();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        progressDialog=new ProgressDialog(this);

        editTextEmail=(EditText)findViewById(R.id.sign_up_email);
        editTextName=(EditText)findViewById(R.id.sign_up_name);
        editTextPassword=(EditText)findViewById(R.id.sign_up_password);
        signUpButton=(Button)findViewById(R.id.sign_up_button);


        //Sign up button clicking
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }
    private void saveUserInformation()
    {
        String nameAlz=editTextName.getText().toString().trim();
        String emailAlz=editTextEmail.getText().toString().trim();

        FirebaseUser user=mAuth.getCurrentUser();
        User currentUser=new User(nameAlz,emailAlz,"");
        mDatabaseReference.getDatabase().getReference().child("PersonAlz").child(user.getUid()).child("Patient").setValue(currentUser);



    }
    private void registerUser() {
        String name=editTextName.getText().toString().trim();
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            //if name is empty
            Toast.makeText(this,R.string.sign_in_enter_the_name,Toast.LENGTH_SHORT).show();
            //stopping the futher execution
            return;
        }
        if(TextUtils.isEmpty(email))
        {
            //if email is empty
            Toast.makeText(this,R.string.sign_in_enter_the_email,Toast.LENGTH_SHORT).show();
            //stopping the futher execution
            return;
        }
        if(TextUtils.isEmpty(password))
        {
            //if password is empty
            Toast.makeText(this,R.string.sign_in_enter_the_password,Toast.LENGTH_SHORT).show();
            //stopping the futher execution
            return;
        }
        //if validation is okay
        //Show a progress bar
        progressDialog.setMessage(getApplicationContext().getResources().getString(R.string.sign_up_message_dialog));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignUpActivity.this,R.string.sign_up_register_successful,Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    saveUserInformation();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(SignUpActivity.this,R.string.sign_up_registration_failed,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
