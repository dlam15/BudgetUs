package com.example.budgetus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class RegisterActivity extends AppCompatActivity {
    private UserRecord userRecord;//derrick

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regBtn = (Button) findViewById(R.id.registerBtn);
        userRecord = new UserRecord(RegisterActivity.this); //derrick


        //on click event when the register button is clicked
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
                EditText lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
                EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
                EditText schoolEditText = (EditText) findViewById(R.id.schoolEditText);
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                TextView outTextView = (TextView) findViewById(R.id.outTextView);

                //User user = (User) getIntent().getSerializableExtra("EXTRA_RECORD");

                String name = firstNameEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String school = schoolEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim() + " " + lastNameEditText.getText().toString().trim();
                System.out.println(name);
                outTextView.setText(name);


            }
        });

    }
}