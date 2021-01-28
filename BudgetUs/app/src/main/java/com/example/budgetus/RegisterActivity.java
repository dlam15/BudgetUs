package com.example.budgetus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    //private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private Button regBtn;
    private Button backBtn;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private User newUser;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        firstNameEditText = findViewById(R.id.registerFirst);
        lastNameEditText = findViewById(R.id.registerLast);
        emailEditText = findViewById (R.id.registerEmail);
        passwordEditText = findViewById (R.id.registerPassword);
        confirmEditText = findViewById(R.id.registerConfirm);

        //Register Button
        regBtn = findViewById(R.id.registerRegister);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pass = passwordEditText.getText().toString();
                final String pass2 = confirmEditText.getText().toString();

                if(!pass.equals(pass2)){
                    confirmEditText.setError("Passwords do not match");
                }else{
                    register();
                }
            }
        });

        //Back to login screen button
        backBtn = findViewById(R.id.registerBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }
        });

    }

    /* Register function
    Uses the Firebase Authorization to register a user with email and password.
     */
    private void register() {
        final String firstname = firstNameEditText.getText().toString();
        final String lastname = lastNameEditText.getText().toString();
        final String name = firstname + " " + lastname;
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        //Errors if the input boxes are empty
        if(TextUtils.isEmpty (firstname)){
            firstNameEditText.setError("Enter your first name");
            return;
        }
        else if(TextUtils.isEmpty (lastname)){
            lastNameEditText.setError("Enter your last name");
            return;
        }
        else if(TextUtils.isEmpty (email)){
            emailEditText.setError("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty (password)){
            passwordEditText.setError("Enter your password");
            return;
        }
        /*else if(TextUtils.isEmpty (username)){
            usernameEditText.setError ("Enter a username");
            return;
        }*/
        else if(!isValidEmail(email)){
            emailEditText.setError("Invalid Email");
            return;
        }

        //Displays a loading circle with message
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        //Creates a user with the email and password given (does not allow repeated email)
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener <AuthResult> () {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if(task.isSuccessful()) {
                    //Create a User object and add it to the database
                    String id = databaseReference.push().getKey();
                    newUser = new User(name, email, "Binghamton", password, id);
                    assert id != null;
                    databaseReference.child(id).setValue(newUser);

                    //Display a successful message and continue to their account
                    Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //Display an unsuccessful message
                    //Should we try to see what the issue is? ex. email already in use
                    Toast.makeText(RegisterActivity.this, "Sign Up Fail!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    /* Valid email function
    Makes sure the email is of the correct format.
     */
    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}