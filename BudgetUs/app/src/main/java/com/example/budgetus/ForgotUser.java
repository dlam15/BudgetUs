package com.example.budgetus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotUser extends AppCompatActivity {


    private Button sendEmailButton;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_user);

        //Variables
        email = (EditText) findViewById(R.id.enterEmailtxt);
        sendEmailButton= (Button) findViewById(R.id.sendEmailBtn);


        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String inputEmail = email.getText().toString().trim();
                    //string user = findUserfromEmail();
                    //sendEmail();

            }
        });

    }
}