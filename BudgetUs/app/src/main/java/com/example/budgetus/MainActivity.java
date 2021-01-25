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

    //private EditText username;
    //private EditText password;
    private EditText editEmail;
    private EditText editPassword;
    private Button loginbutton;
    private Button toRegBtn;//teresa
    private Button forgotUsernamebutton;
    private Button forgotPasswordbutton;
    //private UserRecord userRecord;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    //static final String EXTRA_RECORD = "com.example.EXTRA_RECORD";

   // private static final int RC_SIGN_IN = 123;//for authentication
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance ();

        progressDialog = new ProgressDialog (this);
        //Button toRegBtn = (Button) findViewById(R.id.goToRegisterButton);
        toRegBtn = findViewById(R.id.goToRegisterButton);
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity();
            }
        });
        //userRecord = new UserRecord();
        /*userRecord = new UserRecord(MainActivity.this); //matt



        //Variables
        //final EditText username = (EditText) findViewById(R.id.editUsername);
        //final EditText password = (EditText)findViewById(R.id.editPassword);
        Button loginbutton = (Button) findViewById(R.id.btLogin);
        Button toRegBtn = (Button) findViewById(R.id.goToRegisterButton);
        Button forgotUsernamebutton = (Button) findViewById(R.id.btForgotUsername);
        Button forgotPasswordbutton = (Button) findViewById(R.id.btForgotPassword);

        //Register Button
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity(); }
        });*/

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        loginbutton = findViewById(R.id.btLogin);
        forgotUsernamebutton = findViewById(R.id.btForgotUsername);//teresa
        forgotPasswordbutton = findViewById(R.id.btForgotPassword);//teresa

        //Login Button
        loginbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                /*EditText username = (EditText) findViewById(R.id.editUsername); //derrick
                EditText password = (EditText)findViewById(R.id.editPassword); //derrick
                String inputUser = username.getText().toString().trim();
                String inputPass = password.getText().toString().trim();
                validate(inputUser,inputPass);*/
                login();
             }
         });


        // teresa
        //Forgot Username Button
        forgotUsernamebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgotUsernameActivity();
            }
        });

        //Forgot Password Button
        forgotPasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchForgotPasswordActivity();
            }
        });

        //derrick
        /*List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);*/

    }

    private void login() {
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
    // teresa
    private void launchForgotUsernameActivity(){
        Intent intent = new Intent(this, ForgotUser.class);
        startActivity(intent);
    }

    private void launchForgotPasswordActivity(){
        Intent intent = new Intent(this, ForgotPass.class);
        startActivity(intent);
    }


    /*private void validate(String userName,String userPassword) {
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

     //derrick
    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }*/


}
