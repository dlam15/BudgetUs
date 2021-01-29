package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private String email;
    private TextView outView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);

        /*outView = (TextView)findViewById(R.id.informationTextView);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance ().getReference ( "users" );

        if (firebaseAuth.getCurrentUser() != null){
            email = firebaseAuth.getCurrentUser().getEmail();
            Toast.makeText(MainDashboard.this, email, Toast.LENGTH_LONG).show();
            if (!email.equalsIgnoreCase ("bcamoyrose@gmail.com")){
                startActivity(new Intent (MainDashboard.this,MainActivity.class));
                finish();
            }
        }

        databaseReference.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userDatasnapshot: dataSnapshot.getChildren ()) {
                    User curUser;
                    curUser = userDatasnapshot.getValue (User.class);
                    if (curUser != null) {
                        if (curUser.getEmail ( ).equalsIgnoreCase (email)) {
                            String message;
                            message = "Name: " + curUser.getName ( ) + "\n" + "Username: " + curUser.getUsername ( ) + "\n" + "Email: "
                                    + curUser.getEmail ( ) + "\n" + "School: " + curUser.getSchool ( );
                            //Toast.makeText (MainDashboard.this, message, Toast.LENGTH_LONG).show ( );
                            outView.setText (message);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });*/

    }
}