package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;


public class ForgotPass extends AppCompatActivity {

    private Button sendEmailButton;
    private EditText email;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //Variables
        email = (EditText) findViewById(R.id.forgotEmail);
        sendEmailButton= (Button) findViewById(R.id.forgotSend);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference ( "users" );

        //send user an email to reset password
        sendEmailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                mAuth.sendPasswordResetEmail(userEmail);
                Toast.makeText(ForgotPass.this, "Email sent", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ForgotPass.this,MainActivity.class));
                finish();
            };

        });
    }

}