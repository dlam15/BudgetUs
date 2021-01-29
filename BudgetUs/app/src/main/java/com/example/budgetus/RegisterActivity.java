package com.example.budgetus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText schoolEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private Button regBtn;
    private Button backBtn;
    private TextView outTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private User newUser;
    private Group newGroup;;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance ();
        progressDialog = new ProgressDialog (this);
        databaseReference1 = FirebaseDatabase.getInstance ().getReference ( "users" );
        databaseReference2 = FirebaseDatabase.getInstance ().getReference ( "groups" );
        regBtn = findViewById(R.id.registerRegister);
        backBtn = findViewById(R.id.registerBack);

        //on click event when the register button is clicked
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstNameEditText = findViewById(R.id.registerFirst);
                lastNameEditText = findViewById(R.id.registerLast);
                schoolEditText = findViewById(R.id.registerSchool);
                emailEditText = findViewById (R.id.registerEmail);
                passwordEditText = findViewById (R.id.registerPassword);
                passwordEditText = findViewById (R.id.registerConfirm);

                Register();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Register() {
        final String name = firstNameEditText.getText().toString () + " " + lastNameEditText.getText().toString ();
        final String firstname = firstNameEditText.getText().toString ();
        final String lastname = lastNameEditText.getText().toString ();
        final String school = schoolEditText.getText ().toString ();
        final String email = emailEditText.getText ().toString ();
        final String password = passwordEditText.getText ().toString ();
        final String confirm = confirmEditText.getText ().toString ();

        if(TextUtils.isEmpty (email)){
            emailEditText.setError ("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty (password)){
            passwordEditText.setError ("Enter your password");
            return;
        }
        else if(TextUtils.isEmpty (school)){
            schoolEditText.setError ("Enter a school");
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
        else if(!(password.equals(confirm))){
            confirmEditText.setError ("Password does not match!");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside (false);
        firebaseAuth.createUserWithEmailAndPassword (email, password).addOnCompleteListener (this, new OnCompleteListener <AuthResult> ( ) {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if(task.isSuccessful ()){
                    String id = databaseReference1.push ().getKey ();
                    String id1 = databaseReference2.push().getKey ();
                    newUser = new User(name, email, school, password, id);
                    newGroup = new Group("Admin", id1);
                    newUser.updateGroups (id1, "member");
                    newGroup.registerUser (id, "member");
                    assert id != null;
                    assert id1 != null;

                    databaseReference2.child(id1).setValue(newGroup);
                    databaseReference1.child(id).setValue(newUser);

                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent  = new Intent(RegisterActivity.this, MainActivity.class);
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