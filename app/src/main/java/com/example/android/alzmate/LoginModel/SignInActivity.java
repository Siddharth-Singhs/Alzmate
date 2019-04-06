package com.example.android.alzmate.LoginModel;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.alzmate.MainActivity;
import com.example.android.alzmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //Give the firebase instance
        mAuth=FirebaseAuth.getInstance();
        mProgressDialog=new ProgressDialog(this);
        if(mAuth.getCurrentUser()!=null)
        {
            //start the directly Village Activity
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        //Assign their id
        editTextEmail=(EditText)findViewById(R.id.sign_in_email);
        editTextPassword=(EditText)findViewById(R.id.sign_in_password);
        signIn=(Button)findViewById(R.id.sign_in_button);
        //Checking whether the button is clicked or not
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });


        //To open sign up page
        TextView signUp=(TextView)findViewById(R.id.login_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loginUser() {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            //if email is empty
            Toast.makeText(this, R.string.sign_in_enter_the_email,Toast.LENGTH_SHORT).show();
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

        mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.sign_in_message_dialog));
        mProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            mProgressDialog.dismiss();

                            Toast.makeText(SignInActivity.this,R.string.sign_in_login_successfully,Toast.LENGTH_SHORT).show();
                            //Start the profile activity
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else
                        {
                            mProgressDialog.hide();
                            Toast.makeText(SignInActivity.this,R.string.sign_in_login_failed,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
