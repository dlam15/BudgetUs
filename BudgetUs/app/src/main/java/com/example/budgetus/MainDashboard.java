package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainDashboard extends AppCompatActivity {
    private static final String TAG = "MainDashboard";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button summaryBtn;
    private Button backBtn;
    private Button nameBtn;
    private User curUser;
    private String email;
    private TextView outView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        outView = (TextView)findViewById(R.id.displayUsername);
        nameBtn = findViewById (R.id.changeName);
        summaryBtn = findViewById(R.id.summary);
        backBtn = findViewById(R.id.back);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance ().getReference ( "users" );

        if (firebaseAuth.getCurrentUser() != null){
            email = firebaseAuth.getCurrentUser().getEmail();

            databaseReference.addValueEventListener (new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userDatasnapshot: dataSnapshot.getChildren ()) {
                        curUser = userDatasnapshot.getValue (User.class);
                        if (curUser != null) {
                            if (curUser.getEmail ( ).equalsIgnoreCase (email)) {
                                String message;
                                outView.setText (curUser.getName ());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }

        summaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSummary();
            }
        });

        backBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        nameBtn.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                launchChangeName();
            }
        });


    }

    private void launchChangeName() {
    }

    private void logout() {
        firebaseAuth.signOut ();
        Toast.makeText(MainDashboard.this, "Successfully logout!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish ();
    }

    private void launchSummary() {
        Intent intent = new Intent(this, SummaryContentActivity.class);
        startActivity(intent);
        finish ();
    }
}