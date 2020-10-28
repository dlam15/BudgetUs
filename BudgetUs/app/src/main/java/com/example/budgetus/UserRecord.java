package com.example.budgetus;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;



public class UserRecord {

    private Context mContext;
    private Map<String, User> hashmap = new HashMap<String, User>(1000);

    public UserRecord (Context context){
        this.mContext = context;
        try {
            //FileProcessor fp = new FileProcessor();
            //hashmap = fp.getUserMap();

            //this code opens database.json, now stored at app/src/main/assets
            //info from https://stackoverflow.com/questions/30417810/reading-from-a-text-file-in-android-studio-java
            List<String> mLines = new ArrayList<>();
            //don't understand what this context thing is
            InputStream is = mContext.getAssets().open("database.json");
            //takes it in as input stream and uses a buffered reader but I'm sure there's a way to change that
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            //print just to show it works
            String line;
            while ((line = reader.readLine()) != null)
                mLines.add(line);
            System.out.println("\n\n\n\n\n\nSUCCESS READING\n\n\n\n\n\n");
            for (String string : mLines)
                System.out.println(string);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    //TODO
    //some tests
    //additional functions
    //correct communication between events firing, driver class, user class
    //collision resolution vs username unavailable (see how many collisions from testing)


    /*
     * Add a new user to the hashmap. This is triggered when the user registers.
     *
     * To add a user, we first check that the email is not in use. This requires us to check all users.
     *
     * Then, we must make sure that the username is free as well. For a username to
     * be available, it must not hash to an index that is already in use.
     *
     * Currently, if there is a collision (even if the username is not in use), we tell the user it
     * is unavailable anyway. We could implement collision resolution instead (which is probably done by
     * Java's hashmap already), but I will save that for a later time.
     *
     * If this username is available, we can then populate a new entry of our hashmap with the user object.
     *
     * I also print out a possible message the user should see when the User already exists - maybe pass this back to driver?
     *
     * @param user the user object containing the info for registration
     * @return true if the user was registered correctly, false otherwise
     */
    public boolean addUser(User newUser) {
        String username = newUser.getUsername();//obtain key from username hash function
        if(getUserFromEmail(newUser.getEmail() )!= null){
            System.out.println("Email already in use.");
            return false;//this is not a collision, but rather that the email is in use
        }
        if (hashmap.containsKey(username)) {//true if mapping for this key already exists - could be either a collision or that this username is taken
            User existingUser = hashmap.get(username);
            if(existingUser.getUsername() == username){//username actually in use, otherwise its just a collision
                System.out.println("User already exists");
                return false;
            }else{//could theoretically add user still, but we'd need some collision resolution
                System.out.println("Collision");
                //see what java does here if you put anyway - can it retrieve correct one?
            }
        }else{
            hashmap.put(username, newUser);
            System.out.println("Success");
            return true;
        }
        return false;
    }


    /*
     * Get a user from the hashmap. Considering that all of the user objects are stored in the hashmap,
     * we will need this function to actually obtain the object and work with it. Requires a username to
     * access the object.
     *
     * @param username string username used to index the hashmap and return the object
     * @return user the user object accessed from the hashmap with the username. null if user does not exist
     */
    public User getUser(String username){
        return hashmap.get(username);
    }


    /*
     * Get a user from the hashmap by their email. Will be used when the user forgets their username and during
     * registration to make sure this email is not already in use. Needed as without username we cannot index, and need to O(N) search instead.
     *
     * We could modify this to get a user object by some of the other fields as well if needed.
     *
     * @param email string containing the email address we're searching all users for
     * @return user the user object accessed from the hashmap with the email. null if user does not exist/cannot find email
     */
    public User getUserFromEmail(String email){
        for(User userElement : hashmap.values()){
            User currUser = userElement;
            if(currUser.getEmail() == email) return currUser;
        }
        return null;
    }


    /*
     * Remove a user from the hashmap. We check if they exist, and if they don't print out a message
     * and return false instead. Again, I'd like a better place to put these messages.
     *
     * @param user user object to remove
     * @return true on successful removal, false on unsuccessful removal
     */
    public boolean removeUser(User newUser){
        String username = newUser.getUsername();
        if(hashmap.remove(username) != null) return true;
        System.out.println("User does not exist");
        return false;
    }




    /*
     * Called when the user tries to log in. For a successful login, the username and password must match what was provided.
     * As mentioned in my notes, passwords should not be stored as plaintext. So, we will probably be calling a function in User
     * that uses a salt and hash to check this password with the salted and hashed one we have stored.
     *
     * We also have the ability to know if the username does not exist. We probably won't provide this info to the user in case
     * they are not who they say they are and are looking for usernames to try passwords on.
     *
     * @param username String value of input provided by user for username
     * @param password String value of input provided by user for password
     * @return true on successful login, false on unsuccessful login
     */
    public boolean checkUser(String username, String password){
        if(!hashmap.containsKey(username)) {//username is incorrect - put we might not want to alert the user to this if they're malicious
            System.out.println("Username does not exist");
            return false;
        }else{//username exists
            User accessedUser = hashmap.get(username);
            if(password == accessedUser.getPassword()){
                System.out.println("Successful login");
                return true;
            }
            /* to use the encryption code,
               byte[] salt = accessedUser.getSalt();
               byte[] actualPassword = accessedUser.getPassword();
               return authenticate(password, salt, actualPassword);
             */
            else{
                System.out.println("Incorrect password");
                return false;
            }
        }
    }





    //Everything below this point can be moved to the User class. Some parameters may change to make more sense in that context.


    /*
     * As I said before, we shouldn't store plaintext passwords. One common way to implement a safer system is with a salted hash.
     * We generate some random string, called a salt, and mix this with the password in some way. We then feed this salted password
     * to a hash function, and save the result as the user's "password." We also save the salt. When the user wants to login, repeat the
     * process and check the saltedHashedPassword. By doing so, we are storing the password in a safe way with little overhead.
     */

    //the next 3 functions are a simple implementation of securely storing passwords I found at
    //https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
    //it could probably be improved or use a better hash function or replaced with the ability to sign in with another service (Google)
    //but I will leave it for now as a first attempt
    //I will also explain as I go through these functions

    /* Move to User class?
     *
     * First step in storing a user's password securely. We generate a salt, which is a random string.
     * We use SecureRandom to ensure that this value is actually random and cannot be predicted in any way.
     * This salt will be combined with the user's password. This means that 2 exact passwords will not hash
     * to the same value, as the random salt will make them different. We store this salt in plaintext - it does
     * not need to be secret.
     *
     * @return a string (byte array) that is our 64 bit random salt
     */
    public byte[] generateSalt() throws NoSuchAlgorithmException {
        // VERY important to use SecureRandom instead of just Random
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }



    /* This can stay here or move - maybe its own class
     *
     * This will be called after generating the salt. This will be the only time
     * that part of our code touches the plaintext password. The result of this function
     * is what we will store as the User's password.
     *
     * This function is also called to check a provided password for a match. All we do is
     * salt and hash what's provided, so we can use this to compare in the next function.
     *
     * This password isn't really encrypted, but hashed. The reasoning for this is that
     * we don't need to unencrypt the password, and we actually want to avoid providing
     * a way for that to be done. So, this is a one-way operation.
     *
     * @param password the user's plaintext password to encrypt
     * @param salt the random string used to make it harder to figure out what the password is
     * @return the salted, hashed, password which we can safely store
     */
    public byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
        String algorithm = "PBKDF2WithHmacSHA1";
        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        int derivedKeyLength = 160;
        // Pick an iteration count that works for you. The NIST recommends at
        // least 1,000 iterations:
        // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
        // iOS 4.x reportedly uses 10,000:
        // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }



    /* This can stay here or move
     *
     * This function checks the provided password against the password we have on record for the user.
     * We are not storing the actual password. Instead, we store the result from getEncryptedPassword. So,
     * to check a possible password against the stored one, we need to salt and hash the provided password
     * and compare the 2.
     *
     */
    public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Encrypt the clear-text password using the same salt that was used to
        // encrypt the original password
        byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

        // Authentication succeeds if encrypted password that the user entered
        // is equal to the stored hash
        return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
    }

    /* How can we use these with our User class?
        -when the user creates their account, we call generateSalt, and store the salt with their info
        -we then call getEncryptedPassword with what they provided as a password, and the salt. THIS IS WHAT WE STORE AS THE PASSWORD
        -when the user logs in, we call authenticate() with what they provided as a password, as well as the password and salt stored for the account
        -they are trying to access
     */







    /*
     * Move to User class and uncomment lines when functions exist.
     *
     * 1st function fired when the user forgets their credentials. This emails the user
     * object the forgotID field for the user.
     *
     * I am assuming that the random ID is set in the user class, and that I can access it here.
     *
     * I am also assuming that the user can forget their username OR password. Forgetting a password means that we can still
     * use the normal getUser, as we have the username, to pass as a parameter here. However, on forgetting their username, we
     * can ask for their email and call getUserFromEmail() to get the user object. In either case, we'll call this function next.
     *
     * @param user User object to obtain email and randomID from
     * @return true on successful email sent, false otherwise
     */
    public boolean sendRandomID(User user){
        String email = user.getEmail();//email address of user
        //String randomID = user.getRandomID();
        String randomID = "randomID placeholder";
        String body = "Hello, this is an email from BudgetUs, sent because you forgot your login info. Enter this code to regain access: " + randomID;
        return sendEmail(body, "Forgot Credentials", email);
    }


    /*
    * Move to User class
    *
    * This function sends the message in an email to the reciever. Majority of the code is done in 3 other files
    * (Gmailsender.java, ByteArrayDataSource.java, and JSSEProvider.java), and I found most of this code on the internet
    * so I will cite my sources as well. It also requires a few jar files, stored under app/build/libs. You may have to right-click
    * and click "Add as library" in your AndroidStudio project.
    *
    * Network operations also require a few extra permissions, defined in AndroidManifest, and need to run on a secondary
    * thread.
    * @param message body of the email
    * @param subject subject line of the email
    * @param receiver recipient of the email
    * @return true on success, false on any errors
    */
    public boolean sendEmail(final String message, final String subject, final String receiver){
        final boolean[] ret = {true};//nested functions are weird
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //NOTE - email login info is here, really this code should run on a server or something, where user cannot see the source code
                    GmailSender sender = new GmailSender("budgetusemail@gmail.com", "budgetus123!");//sender of email - credentials
                    sender.sendMail(subject, message, "budgetusemail@gmail.com", receiver);//subject, body, sender, receiver
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                    ret[0] = false;
                }
            }
        }).start();
        return ret[0];
    }

    /* Move to User class and uncomment lines once functions exist.

     * 2nd function fired when the user forgets their username or password. This checks if
     * the forgotID provided by the user matches the forgotID. We will also update the user's
     * random id after this attempted match.
     *
     * @param id the id provided by the user attempting to login
     * @param user User object to obtain real ID from
     * @return true on successful match, false otherwise
     */
    public boolean matchForgotID(byte[] id, User user) throws NoSuchAlgorithmException {
        boolean ret = false;
        //if(Arrays.equals(id, user.getRandomID)) ret = true;
        //user.setRandomID();//in either case, we should generate a new random ID
        return ret;
    }

    //This code requires the following functions to be created in the User class
    //I did not make them so that we did not have any merge conflicts
    //Firstly, we need a char[] randomID field - this will store the random ID
    //using char[] so that I can reuse the secure random salt generator - I figured this should be secure too
    //char[] getRandomID() //returns randomID
    //void setRandomID() //will call generateHash and use this random value to set randomID field


}
