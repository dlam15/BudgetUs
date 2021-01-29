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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button loginbutton;
    private Button toRegBtn;//teresa
    private Button forgotPasswordbutton;//teresa
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance ();

        progressDialog = new ProgressDialog (this);

        toRegBtn = findViewById(R.id.mainRegister);
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity();
            }
        });

        //Variables
         editEmail = findViewById(R.id.mainEmail);
         editPassword = findViewById(R.id.mainPassword);
         loginbutton = findViewById(R.id.mainLogin);
         forgotPasswordbutton = findViewById(R.id.mainForgot);//teresa

        //Login Button
         loginbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Login();
             }
         });
    }

    private void Login() {
        final String email =  editEmail.getText().toString();
        final String password = editPassword.getText().toString();

        if(TextUtils.isEmpty (email)){
            editEmail.setError ("Enter your email");
            return;
        }
        else if(TextUtils.isEmpty (password)){
            editPassword.setError ("Enter your password");
            return;
        }
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside (false);

        firebaseAuth.signInWithEmailAndPassword (email, password).addOnCompleteListener (this, new OnCompleteListener <AuthResult> ( ) {
            @Override
            public void onComplete(@NonNull Task <AuthResult> task) {
                if(task.isSuccessful ()){

                    Toast.makeText(MainActivity.this, "Successfully Login!", Toast.LENGTH_LONG).show();
                    Intent intent  = new Intent(MainActivity.this, MainDashboard.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this, "Login Fail!", Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    //Functions
    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish ();
    }
/* teresa
    private void launchForgotUsernameActivity(){
        Intent intent = new Intent(this, forgotLogin.class);
        startActivity(intent);
    }

    private void launchForgotPasswordActivity(){
        Intent intent = new Intent(this, forgotLogin2.class);
        startActivity(intent);
    }
*/

   /* private void validate(String userName,String userPassword) {
         //get username and password
         String matchUser = "admin";
         String matchPass = "123";
         //If match go to next page
        //if (userName.equals(matchUser) && userPassword.equals(matchPass)) { teresa
         if (userRecord.checkUser(userName,userPassword)) {
             Intent dashboardPage = new Intent(this, MainDashboard.class);
             startActivity(dashboardPage);
         }
         else{
             //Can create limited attempts at login
            System.out.println("Failed to validate");
         }
     }*/


}
