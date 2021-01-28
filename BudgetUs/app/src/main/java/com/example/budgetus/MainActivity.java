package com.example.budgetus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button loginbutton;
    private Button toRegBtn;
    //private Button forgotUsernamebutton;
    private Button forgotPasswordbutton;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        editEmail = findViewById(R.id.mainEmail);
        editPassword = findViewById(R.id.mainPassword);

        //Login Button
        loginbutton = findViewById(R.id.mainLogin);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //Register Button
        toRegBtn = findViewById(R.id.mainRegister);
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity();
            }
        });

        //Forgot Username Button
        /*forgotUsernamebutton = findViewById(R.id.btForgotUsername);//teresa
        forgotUsernamebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgotUsernameActivity();
            }
        });*/

        //Forgot Password Button
        forgotPasswordbutton = findViewById(R.id.mainForgot);//teresa
        forgotPasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgotPasswordActivity();
            }
        });

    }

    //Functions
    /* Login function
    Uses the Firebase Authorization to login the user with email and password.
     */
    private void login() {
        final String email =  editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        //Errors if the input boxes are empty
        if(TextUtils.isEmpty(email)){
            editEmail.setError("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty(password)){
            editPassword.setError("Enter your password");
            return;
        }

        //Displays a loading circle with message
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener <AuthResult> ( ) {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if(task.isSuccessful()){
                    //If successful go to the main dashboard and display a message at the bottom of the screen
                    Toast.makeText(MainActivity.this, "Successfully Login!", Toast.LENGTH_LONG).show();
                    Intent intent  = new Intent(MainActivity.this, MainDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //If unsuccessful display an error message
                    Toast.makeText(MainActivity.this, "Login Fail!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish ();
    }

    /*private void launchForgotUsernameActivity(){
        Intent intent = new Intent(this, ForgotUser.class);
        startActivity(intent);
    }*/

    private void launchForgotPasswordActivity(){
        Intent intent = new Intent(this, ForgotPass.class);
        startActivity(intent);
    }

}
