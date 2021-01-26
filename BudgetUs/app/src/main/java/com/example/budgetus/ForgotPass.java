package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.JsonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

/*
    Used to reset password. Emails a user based on their username. We can change the email that is sent.

    Might need to sign the user out once they submit this request? Unsure

    Replaces UserRecord functions:
        sendRandomID
        checkRandomID (link in email brings user to a page to reset password)
        sendEmail
 */

public class ForgotPass extends AppCompatActivity {

    private Button sendEmailButton;
    private EditText username;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //Variables
        username = (EditText) findViewById(R.id.enterUsernametxt);
        sendEmailButton= (Button) findViewById(R.id.sendEmailPBtn);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference ( "users" );
        progressDialog = new ProgressDialog(this);


        //send user an email to reset password
        //usually done by providing email (which is easier) but username helpful too and possible with the database
        sendEmailButton.setOnClickListener(new View.OnClickListener() {

            //triggered when user presses button
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside (false);

                    //the usual way to search db uses a query - I couldn't get this to work so we'll search all objects ourselves (for now)
                    databaseReference.get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>(){

                                //triggered once task completes
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    String inputUsername = username.getText().toString().trim();
                                    String emailAddress = "";
                                    boolean userFound = false;
                                    //look at all child results (each is a User object)
                                    for(DataSnapshot userDatasnapshot: task.getResult().getChildren()) {
                                        User curUser;
                                        curUser = userDatasnapshot.getValue(User.class);//cast results to user obj
                                        if(curUser.getUsername() != null && curUser.getUsername().equals(inputUsername)){//found username provided
                                            //get email, set a flag so we email next, break
                                            emailAddress = curUser.getEmail();
                                            userFound = true;
                                            break;
                                        }
                                    }
                                    if(userFound == true) {//user found, will send email
                                        mAuth.sendPasswordResetEmail(emailAddress)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    //triggered once task completes
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ForgotPass.this, "Email Sent.", Toast.LENGTH_LONG).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(ForgotPass.this, "Email not sent. Please try again.", Toast.LENGTH_LONG).show();
                                                            //Log.e(TAG, "sendEmailVerification", task.getException());
                                                            //add retry?
                                                        }
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                    }else{
                                        progressDialog.dismiss();
                                        Toast.makeText(ForgotPass.this, "User not found.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

            };
        });



    }




}