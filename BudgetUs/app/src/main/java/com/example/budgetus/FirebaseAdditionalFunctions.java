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
    These are additional functions we may need for interacting with Firebase User object in the future, like reset password,
    change email, send email verification, etc.

    So, until we add front-end actions to trigger these actions, I leave them here.
 */
public class FirebaseAdditionalFunctions extends AppCompatActivity {

    private FirebaseAuth mAuth;//authentication object used to call functions

    /*
    Used to verify email. So, we could make an account inactive until they click a link in the email sent here.
    We can edit the email sent within Firebase's settings.
    */
    private void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Email Sent");
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            //add retry?
                        }
                    }
                });
    }

    /*
     Function to update additional user fields, like display name and profile picture.

     Might not need, as we add the extra info to our User object.
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
                        }else {
                            Log.e(TAG, "Update profile", task.getException());
                        }
                    }
                });
    }

    /*
    Delete User and their info from the system.

    Replaces:
    removeUser
     */
    private void deleteUser(){
        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User deleted");
                        //log out if it doesn't automatically
                    }else {
                        Log.e(TAG, "Delete user", task.getException());
                    }
                }
            });
    }


    /*
     Users stay signed in after exiting the app by default, so we need an option to sign out.
     */
    private void signOut(){
        mAuth.signOut();
    }


    //These are 2 setters for email and password. I'll move these (and add the rest of the getters and setters) to a User class
    //soon depending on how we'll deal with user objects.

    //Rest of getters are as follows below, or use the builder as seen in updateUserFields above
    //Setters are a bit easier, just .getX(), no async

    //Also, the documentation says that these are security critical actions, and require the user to have signed in recently.
    //I haven't run into an issue with this yet, but will keep an eye out for it.

    private void changeEmail(String newEmail){
        mAuth.getCurrentUser().updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email updated");
                        }else {
                            Log.e(TAG, "Update Email", task.getException());
                        }
                    }
                });
    }

    private void changePassword(String newPassword){
        mAuth.getCurrentUser().updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password updated");
                        }else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                        }
                    }
                });
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
