package com.example.budgetus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

//import com.firebase.ui.auth.AuthUI;
//import com.firebase.ui.auth.IdpResponse;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //private EditText username;
    //private EditText password;
    //private Button loginbutton;
    //private Button toRegBtn;
    //private Button forgotUsernamebutton;
    //private Button forgotPasswordbutton;
    private UserRecord userRecord;
    //static final String EXTRA_RECORD = "com.example.EXTRA_RECORD";

   // private static final int RC_SIGN_IN = 123;//for authentication
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*Button toRegBtn = (Button) findViewById(R.id.goToRegisterButton);
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity();
            }
        });*/
        //userRecord = new UserRecord();
        userRecord = new UserRecord(MainActivity.this); //matt



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
        });

        //Login Button
         loginbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 EditText username = (EditText) findViewById(R.id.editUsername); //derrick
                 EditText password = (EditText)findViewById(R.id.editPassword); //derrick
                 String inputUser = username.getText().toString().trim();
                 String inputPass = password.getText().toString().trim();
                 validate(inputUser,inputPass);
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

    //Functions
    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void launchForgotUsernameActivity(){
        Intent intent = new Intent(this, ForgotUser.class);
        startActivity(intent);
    }

    private void launchForgotPasswordActivity(){
        Intent intent = new Intent(this, ForgotPass.class);
        startActivity(intent);
    }


    private void validate(String userName,String userPassword) {
         //get username and password
         String matchUser = "admin";
         String matchPass = "123";
         
         if (userRecord.checkUser(userName,userPassword)) {
             Intent dashboardPage = new Intent(this, MainDashboard.class);
             startActivity(dashboardPage);
         }
         else{
             //Can create limited attempts at login
            System.out.println("Failed to validate");
         }
     }

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
