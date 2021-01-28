package com.example.budgetus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ForgotUser extends AppCompatActivity {


    private Button sendEmailButton;
    private EditText email;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_user);

        //Variables
        email = (EditText) findViewById(R.id.enterTxt);
        sendEmailButton= (Button) findViewById(R.id.sendEmailBtn);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputEmail = email.getText().toString().trim();
                //string user = findUserfromEmail();
                //sendEmail();
                //the usual way to search db uses a query - I couldn't get this to work so we'll search all objects ourselves (for now)
                //we need to check database to see if this is a valid email
                databaseReference.get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

                            //triggered once task completes
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                String emailAddress = email.getText().toString().trim();
                                String username = "";
                                boolean userFound = false;
                                //look at all child results (each is a User object)
                                for (DataSnapshot userDatasnapshot : task.getResult().getChildren()) {
                                    User curUser;
                                    curUser = userDatasnapshot.getValue(User.class);//cast results to user obj
                                    if (curUser.getEmail() != null && curUser.getEmail().equals(emailAddress)) {//found username provided
                                        //get email, set a flag so we email next, break
                                        username = curUser.getName();
                                        userFound = true;
                                        break;
                                    }
                                }
                                if (userFound == true) {//user found, will send email
                                    //problem - Firebase only lets us send an email for password reset, email change, or email verification
                                    //so we either need to use my old password code or just tell user the username or find a way to add to Firebase's templates
                                    //for now, I've added my old password code, but one of the alternatives is probably better
                                    sendEmail("Hello, you forgot your username, it is " + username, "Forgot Username", emailAddress);
                                } else {
                                    System.out.println("User not found");
                                }

                            }
                        });
            }
        });
    }

    /*
     * This function sends the message in an email to the reciever. Majority of the code is done in 3 other files
     * (Gmailsender.java, ByteArrayDataSource.java, and JSSEProvider.java), and I found most of this code on the internet
     * so I will cite my sources as well. It also requires a few jar files, stored under app/build/libs. You may have to right-click
     * and click "Add as library" in your AndroidStudio project.
     *
     * Network operations also require a few extra permissions, defined in AndroidManifest, and need to run on a secondary
     * thread.
     *
     * NOTE - Google will turn off this feature if its not being used, so go to Google settings for the account to see if its on if this
     * function isn't working.
     *
     * @param message body of the email
     * @param subject subject line of the email
     * @param receiver recipient of the email
     * @return true on success, false on any errors
     */
    public boolean sendEmail(final String message, final String subject, final String receiver){
        System.out.println(message +", "+ subject +", "+ receiver);
        final boolean[] ret = {true};//nested functions are weird
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //NOTE - email login info is here, really this code should run on a server or something, where user cannot see the source code
                    GmailSender sender = new GmailSender("budgetusemail@gmail.com", "");//sender of email - credentials
                    //I have no idea how to hide the credentials, just don't push with password for now
                    sender.sendMail(subject, message, "budgetusemail@gmail.com", receiver);//subject, body, sender, receiver
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    ret[0] = false;
                }
            }
        }).start();
        return ret[0];
    }
}