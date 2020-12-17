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
    private Button toRegBtn;//teresa
    private Button forgotUsernamebutton;//teresa
    private Button forgotPasswordbutton;//teresa
    private UserRecord userRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button toRegBtn = (Button) findViewById(R.id.goToRegisterButton);
        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRegisterActivity();
            }
        });
        userRecord = new UserRecord();//UserRecord userRecord = new UserRecord(MainActivity.this); matt


        //Variables
         username = (EditText) findViewById(R.id.editUsername);
         password = (EditText)findViewById(R.id.editPassword);
         loginbutton = (Button) findViewById(R.id.btLogin);
         toRegBtn = (Button) findViewById(R.id.goToRegisterButton);//teresa
         forgotUsernamebutton = (Button) findViewById(R.id.btForgotUsername);//teresa
         forgotPasswordbutton = (Button) findViewById(R.id.btForgotPassword);//teresa

        //Register Button //teresa
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

        // registerbutton.setOnClickListener(new Button.OnClickListener(){
        //     public void onClick(View view) {
        //         Intent registerPage = new Intent(MainActivity.this, Register.class);
        //         startActivity(registerPage);
        //     }
        // });
        /* teresa
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
        */
    }

    //Functions
    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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

    private void validate(String userName,String userPassword) {
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
     }


}
