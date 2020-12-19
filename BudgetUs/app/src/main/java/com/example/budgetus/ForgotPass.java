package com.example.budgetus;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPass extends AppCompatActivity {

    private Button sendEmailButton;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        //Variables
        username = (EditText) findViewById(R.id.enterUsernametxt);
        sendEmailButton= (Button) findViewById(R.id.sendEmailPBtn);


        sendEmailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    String inputUsername = username.getText().toString().trim();
                    //string user = findUserfromEmail();
                    //sendEmail();

            }
        });
    }

}