package com.example.budgetus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button regBtn = (Button) findViewById(R.id.registerBtn);

        //on click event when the register button is clicked
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
                EditText lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
                TextView outTextView = (TextView) findViewById(R.id.outTextView);

                String res = firstNameEditText.getText() + " " + lastNameEditText.getText();
                System.out.println(res);
                outTextView.setText(res);
            }
        });

    }
}