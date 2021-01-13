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

    private FirebaseAuth mAuth;//authentication object used to call functions

    /*
     Constructor for calling testing functions
     */
    FirebaseAuthenticationTest(){
        System.out.println("Preforming Authentication");
        mAuth = FirebaseAuth.getInstance();// Initialize Firebase Auth
        test();
    }

    /*
      Test function to call the other functions
     */
    public void test() {
        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            //createAccount("mkyea1@binghamton.edu", "password");//make account
            //updateUserFields();//update with extra info
            signIn("mkyea1@binghamton.edu", "password2");//sign in
            //resetPassword();
            //sendEmailVerification();
        } else {
            System.out.println("User already signed in");
        }
        printInfo();
    }


    /*
   API functions are async, so we need to make sure that an operation completes before allowing anything else.
   So, when we call a function, we disallow anything else from being selected until that call finishes.
   hideActions() and showActions() can be used for this purpose. Right now I think just hiding and showing buttons on the
   screen will work, but we can add checks in code instead if easier/cleaner/etc.

   For example, user hits sign in->all possible actions hidden as web service called to sign user in -> web service returns -> display possible actions again

   I'll start adding to these once I connect this code to the front-end/display abilities
   */
    public void hideActions(){}
    public void showActions(){ }


    /*
     The rest of the functions below are mostly copy-pasted from the documentation. However, keep in mind that they are mostly async (use Java tasks).
     This makes sense - we can't just halt execution and wait on the web service to respond, and this lets us know when the call is finished. So,
     we'll need to be careful and make sure an action is finished before allowing another one (i.e. make sure user is successfully logged in before
     allowing them to display info or change password, etc) which is why we'll use something like hideActions() and showActions() described above.
     */



    /*
     Creates an account from an email and password. Will also sign the new user in.
     There are a few other fields that a User contains, like name and picture. The function updateUserFields,
     seen below, can be called here to register and set extra info all at once.

     Replaces:
     checkUsername
     checkEmail
     attemptRegister
     addUser
     getUser
     getUserFromEmail
     */
    private void createAccount(String email, String password) {
        hideActions();//display "loading"
        Log.d(TAG, "createAccount:" + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            showActions();//display "success"
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            showActions();//display "failure" and allow to try again
                        }
                    }
                });
    }


    /*
    Used to verify email. So, we could make an account inactive until they click a link in the email sent here.
    We can edit the email sent within Firebase's settings.

    Probably doesn't need to hide other options, email failing to send is an odd issue that'd we'd probably just try to send again
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
     Used to reset password. I think there is a way to change User's email as well. Again, we can edit the email sent within Firebase.

     Might need to sign the user out once they submit this request? Unsure

     Probably don't need to hide other options, email failing to send is an odd issue that'd we'd probably just try to send again

     Replaces:
     sendRandomID
     checkRandomID (link in email brings user to a page to reset password)
     sendEmail
     */
    private void resetPassword(){
        FirebaseUser user = mAuth.getCurrentUser();
        String emailAddress = user.getEmail();
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            //add retry?
                        }
                    }
                });
    }





    /*
     Sign a user in with their email and password.

     Replaces:
     checkUser
     PasswordEncryptionService class
     */
    private void signIn(String email, String password) {
        hideActions();//show "signing in"
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            showActions();//success, show dashboard/options available to a signed in user
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            showActions();//show failure, ask to try again (probably add a limit if Firebase doesn't automatically)
                        }
                    }
                });
    }


    /*
     Function to update additional user fields, like display name and profile picture. Will have to see
     if these can be specified at profile creation as well. Will need parameters.
     */
    private void updateUserFields(){
        hideActions();//show "updating"
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
                            showActions();//show "fields updated"
                        }else {
                            Log.e(TAG, "Update profile", task.getException());
                            showActions();//show "failure" and why
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
        hideActions();//show "deleting" (probably add confirm button)
        mAuth.getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User deleted");
                        //log out if it doesn't automatically
                        showActions();//show "deleted," go back to register screen
                    }else {
                        Log.e(TAG, "Delete user", task.getException());
                        showActions();//show error
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
        hideActions();//show "updating"
        mAuth.getCurrentUser().updateEmail(newEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email updated");
                            showActions();//show "success"
                        }else {
                            Log.e(TAG, "Update Email", task.getException());
                            showActions();//show "failure"
                        }
                    }
                });
    }

    private void changePassword(String newPassword){
        hideActions();
        mAuth.getCurrentUser().updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password updated");
                            showActions();//show "success"
                        }else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            showActions();//show "failure"
                        }
                    }
                });
    }





    /*
     Basic function to print a user's name, email, pic URL, email verification status, and uid. Not a copy-pasted function from the documentation/API.
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
