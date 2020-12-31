package com.example.budgetus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.google.firebase.auth.UserProfileChangeRequest;

import static android.content.ContentValues.TAG;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

/*
  Java class for using Firebase for authentication, sending email, etc. Adding as separate class for now to
  keep old way and this way apart/not jumbled together, then once it works/we like it, we can change things over.

  I've copied most of these functions for now just to see how they work. We can change return types and
  replace the old code later.
 */
public class FirebaseAuthenticationTest extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /*
     Constructor for calling testing functions
     */
    FirebaseAuthenticationTest(){
        System.out.println("AUTH STUFF");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        test();
    }

    /*
      Test function to:
      -see if user is signed in
      -if not, make or sign in user, update their fields
     */
    public void test() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("null user, lets make one");
            //createAccount("mkyea1@binghamton.edu", "password");//make account
            //updateUserFields();//update with extra info
            signIn("mkyea1@binghamton.edu", "password");//sign in
            //sendEmailVerification();
        } else {
            System.out.println("non null usr");
        }
        printInfo();
    }

    /*
     Function to update additional user fields, like display name and profile picture. Will have to see
     if these can be specified at profile creation as well. Will need parameters.
     */
    private void updateUserFields(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //uses a builder for changes
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("matt kyea")
                .setPhotoUri(Uri.parse("https://www.binghamton.edu/img/hi-res/directory/head.jpg?resolution=200"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }


    /*
     Sign a user in with their email and password.
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
        // [END sign_in_with_email]
    }


    /*
     Creates an account from an email and password. Will also sign the new user in.
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    /*
     Used to verify email. So, we could make an account inactive until they click a link in the email sent here.
     */
    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        if (task.isSuccessful()) {
                            System.out.println("sent");
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    /*
     Basic function to print a user's name, email, pic URL, email verification status, and uid.
     */
    private void printInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            System.out.println(name+email+photoUrl+emailVerified+uid);
            System.out.println("Name: "+name);
            System.out.println("Email: "+email);
            System.out.println("Pic: "+photoUrl);
            System.out.println("Email Verified?: "+emailVerified);
            System.out.println("UID: "+ uid);
        }
    }

}
