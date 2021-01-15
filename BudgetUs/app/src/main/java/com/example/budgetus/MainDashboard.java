package com.example.budgetus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        RelativeLayout summaryContentView = (RelativeLayout) findViewById(R.id.summary_content);
        summaryContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSummaryContentActivity();
            }
        });

        /*
            basically repeat above with whatever sections we want
        */

    }

    private void launchSummaryContentActivity(){
        Intent intent = new Intent(this, SummaryContentActivity.class);
        startActivity(intent);
    }
}