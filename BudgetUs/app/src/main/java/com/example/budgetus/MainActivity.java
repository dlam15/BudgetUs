package com.example.budgetus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;;

import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginbutton;
    private Button registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText)findViewById(R.id.editPassword);
        loginbutton = (Button) findViewById(R.id.btLogin);
        registerbutton = (Button) findViewById(R.id.btRegister);

        loginbutton.setOnClickListener(new Button.OnClickListener() {
            //@Override
            public void onClick(View view) {
                String inputUser = username.getText().toString().trim();
                String inputPass = password.getText().toString().trim();
                validate(inputUser,inputPass);
            }
        });
        registerbutton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                Intent registerPage = new Intent(MainActivity.this, Register.class);
                startActivity(registerPage);
            }
        });
    }
    private void validate(String userName,String userPassword) {
        //get username and password
        String matchUser = "admin";
        String matchPass = "123";
        //If match go to next page
        if (userName.equals(matchUser) && userPassword.equals(matchPass)) {
            Intent dashboardPage = new Intent(MainActivity.this, MainDashboard.class);
            startActivity(dashboardPage);
        }
        else{
            //Can create limited attempts at login

        }
    }

}