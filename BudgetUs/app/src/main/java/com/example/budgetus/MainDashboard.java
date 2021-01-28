package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainDashboard extends AppCompatActivity {
    private static final String TAG = "MainDashboard";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String email;
    private TextView name;
    private TextView outView;

    private Button summary;
    private Button changeName;
    private Button backbtn;
    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance ().getReference("users" );

        //Here onwards is just for testing purposes and is to be replaced later
        outView = (TextView)findViewById(R.id.informationTextView);//britania
        name = (TextView)findViewById(R.id.displayUsername);

        //Change the person's name to Joe King
        changeName = (Button)findViewById(R.id.changeName);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rename();
            }
        });

        //Go back to the login page and sign out of the app
        backbtn = (Button)findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent (MainDashboard.this,MainActivity.class));
                finish();
            }
        });

        //Go to the summary page
        summary = (Button)findViewById(R.id.summary);
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSummaryContentActivity();
            }
        });


        //Get the email of the user and display it
        if (firebaseAuth.getCurrentUser() != null){
            email = firebaseAuth.getCurrentUser().getEmail();
            Toast.makeText(MainDashboard.this, email, Toast.LENGTH_LONG).show();
        }

        //Get of other information about the user (in real time) and display it
        databaseReference.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userDatasnapshot: dataSnapshot.getChildren ()) {
                    //User curUser;
                    curUser = userDatasnapshot.getValue (User.class);
                    if (curUser != null) {
                        if (curUser.getEmail ( ).equalsIgnoreCase (email)) {
                            String message;
                            message = "Name: " + curUser.getName ( ) + "\n" + "Username: " + /*curUser.getUsername ( ) +*/ "\n" + "Email: "
                                    + curUser.getEmail ( ) + "\n" + "School: " + curUser.getSchool ( );
                            //Toast.makeText (MainDashboard.this, message, Toast.LENGTH_LONG).show ( );
                            outView.setText (message);
                            name.setText(curUser.getName());
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

    //Launch the summary activity
    private void launchSummaryContentActivity() {
        Intent intent = new Intent(this, SummaryContentActivity.class);
        startActivity(intent);
    }

    //Rename the person
    public void rename(){
        curUser.updateName("Joe King");
    }


}