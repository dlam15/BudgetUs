package com.example.budgetus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button regBtn;
    private TextView outTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private User newUser;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance ();
        progressDialog = new ProgressDialog (this);
        databaseReference = FirebaseDatabase.getInstance ().getReference ( "users" );
        regBtn = findViewById(R.id.registerBtn);

        //on click event when the register button is clicked
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstNameEditText = findViewById(R.id.firstNameEditText);
                lastNameEditText = findViewById(R.id.lastNameEditText);
                usernameEditText = findViewById(R.id.usernameEditText);
                emailEditText = findViewById (R.id.emailEditText);
                passwordEditText = findViewById (R.id.passwordEditText);
                outTextView = findViewById(R.id.outTextView);

                Register();
            }
        });
    }

    private void Register() {
        final String name = firstNameEditText.getText().toString () + " " + lastNameEditText.getText().toString ();
        final String firstname = firstNameEditText.getText().toString ();
        final String lastname = lastNameEditText.getText().toString ();
        final String username = usernameEditText.getText ().toString ();
        final String email = emailEditText.getText ().toString ();
        final String password = passwordEditText.getText ().toString ();

        if(TextUtils.isEmpty (email)){
            emailEditText.setError ("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty (password)){
            passwordEditText.setError ("Enter your password");
            return;
        }
        else if(TextUtils.isEmpty (username)){
            usernameEditText.setError ("Enter a username");
            return;
        }
        else if(TextUtils.isEmpty (firstname)){
            firstNameEditText.setError ("Enter your first name");
            return;
        }
        else if(TextUtils.isEmpty (lastname)){
            lastNameEditText.setError ("Enter your last name");
            return;
        }
        else if(!isValidEmail(email)){
            emailEditText.setError ("Invalid Email");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside (false);
        firebaseAuth.createUserWithEmailAndPassword (email, password).addOnCompleteListener (this, new OnCompleteListener <AuthResult> ( ) {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if(task.isSuccessful ()){
                    String id = databaseReference.push ().getKey ();
                    newUser = new User(name, email, "Binghamton", username, password, id);
                    assert id != null;
                    databaseReference.child(id).setValue(newUser);

                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent  = new Intent(RegisterActivity.this, MainDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Sign Up Fail!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return (!TextUtils.isEmpty (email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}