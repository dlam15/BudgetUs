package com.example.budgetus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

;



public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginbutton;
    private Button toRegBtn;
    private Button forgotUsernamebutton;
    private Button forgotPasswordbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables
         username = (EditText) findViewById(R.id.editUsername);
         password = (EditText)findViewById(R.id.editPassword);
         loginbutton = (Button) findViewById(R.id.btLogin);
         toRegBtn = (Button) findViewById(R.id.goToRegisterButton);
         forgotUsernamebutton = (Button) findViewById(R.id.btForgotUsername);
         forgotPasswordbutton = (Button) findViewById(R.id.btForgotPassword);


        //Register Button
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { launchRegisterActivity(); }
        });

         //Login Button
         loginbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String inputUser = username.getText().toString().trim();
                 String inputPass = password.getText().toString().trim();
                 validate(inputUser,inputPass);
             }
         });

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

    }

    //Functions
    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void launchForgotUsernameActivity(){
        Intent intent = new Intent(this, forgotLogin.class);
        startActivity(intent);
    }

    private void launchForgotPasswordActivity(){
        Intent intent = new Intent(this, forgotLogin2.class);
        startActivity(intent);
    }



     private void validate(String userName,String userPassword) {
         //get username and password
         String matchUser = "admin";
         String matchPass = "123";
         //If match go to next page
         if (userName.equals(matchUser) && userPassword.equals(matchPass)) {
             Intent dashboardPage = new Intent(this, MainDashboard.class);
             startActivity(dashboardPage);
         }
         else{
             //Can create limited attempts at login

         }
     }


}
