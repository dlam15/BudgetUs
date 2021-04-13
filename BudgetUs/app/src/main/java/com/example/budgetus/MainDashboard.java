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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

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
        final String id2 = FirebaseAuth.getInstance ().getCurrentUser ().getUid ();
        databaseReference.child(id2).get().addOnCompleteListener(new OnCompleteListener <DataSnapshot> () {
            @Override
            public void onComplete(@NonNull Task <DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    curUser = task.getResult ().getValue (User.class);
                    if (curUser != null) {
                        outView.setText (curUser.getName ( ));
                    }
                    loadGroups(curUser);//Matt
                    Log.d("firebase", id2);
                }
            }
        });
        /*
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
         */

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



    //Matt's functions for loading groups

    /*
     * This takes in the User we logged in, and loads all of their groups. Need a
     * Groups field in User to store these.
     */
    public void loadGroups(User curUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("groups");
        Map<String, Role> groupsAndRoles = curUser.getGroups();
        //used to tell when we're done loading all groups and can exit (by calling the function that needs the data we're loading here)
        final int size = groupsAndRoles.entrySet().size();
        final int[] currentEntry = {0};

        final ArrayList<Group> groups = new ArrayList<>();//temporary, should store in User object

        for (final Map.Entry<String, Role> entry : groupsAndRoles.entrySet()) {
            database.child(entry.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Group group = task.getResult().getValue(Group.class);//load the group and all its info (including Budget)
                        //so now we need an actual Groups field in User, or somewhere to store this
                        //using this arraylist for now
                        groups.add(group);
                        group.getGroupBudget().setContext(MainDashboard.this);//used for toasts
                        group.getGroupBudget().setRole(entry.getValue()); //set the role, maybe come up with something better
                        //loading a group automatically loads the budget info, so I don't really need the constructor anymore, which is why I'm using this setter
                        //little test:
                        System.out.println(group.getGroupBudget().getListOfTransactions().get(0).toString());//this is allowed, I'm a member
                        group.getGroupBudget().addTransaction(1000, "invalid test", null, "testing role permissions", Calendar.getInstance(), null);//this is not allowed, toast pops up and nothing happens
                        if(currentEntry[0] == size-1){//once we're done loading
                            setGroups(groups);//we can call the function to display group info
                            //by calling here, we ensure we don't get a null pointer exception by trying to work with group data before its been loaded
                        }
                    }
                    currentEntry[0]++;
                }

            });

        }
    }

    /*
     * We will only call this function after we've loaded all of the user's group data.
     * This ensures we won't get a null pointer exception by making sure we have all data first.
     * If my MainDashboard is out of date, just move lines that deal with group info here
     */
    private void setGroups(ArrayList<Group> groups){
        //once we call this function, all of the groups and their data have been loaded
        //so we can start displaying
        //I won't add because I think my XML file is out of date
    }
}