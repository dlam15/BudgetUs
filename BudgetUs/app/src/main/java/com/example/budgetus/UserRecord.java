package com.example.budgetus;

import android.util.JsonReader;
import android.util.Log;

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

    private Map<String, User> hashmap = new HashMap<String, User>(1000);

    public UserRecord (){
        try {
            FileProcessor fp = new FileProcessor();
            hashmap = fp.getUserMap();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //TODO
    //some tests
    //new functions not on the sheet
    //correct communication between events firing, driver class, user class
    //---anything modifying user variables will be done in user
    //collision resolution vs username unavailable


    /*
     * Add a new user to the hashmap. This is triggered when the user registers.
     *
     * To add a user, we first check that the email is not in use. This requires us to check all users.
     *
     * Then, we must make sure that the username is free as well. For a username to
     * be available, it must not hash to an index that is already in use.
     *
     * Currently, if there is a collision (even if the username is not in use), we tell the user it
     * is unavailable anyway. We could implement collision resolution instead, but I will save
     * this for a later time.
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


    /*  NEW
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


    /*  NEW
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
            /*
             * As I said before, we shouldn't store plaintext passwords. This doesn't really matter right now
             * because this would be on a server and this login stuff isn't functional for now, but later it
             * might look something like this
             */
            /*
             User accessedUser = hashmap.get(username);
             if(accessedUser.checkPassword(password)) //successful login

             checkPassword(String password){
                int salt = this.getSalt();//salts are randomly generated at user creation
                String saltedPassword = salt ^ password; //or something
                String saltedHash = hash(saltedPassword);
                return (saltedHash == password);
             }
             */
            else{
                System.out.println("Incorrect password");
                return false;
            }
        }
    }




    /*
     * Can do in User class?
     *
     * 1st function fired when the user forgets their password. This emails the user
     * object the forgotID field for the user.
     *
     * I am assuming that the random ID is set in the user class, and that I can access it here.
     *
     * I am also assuming that the user can forget their username OR password. Forgetting a password means that we can still
     * use the normal getUser, as we have the username, to pass as a parameter here. However,  on forgetting their username, we
     * can ask for their email and do an O(n) search of the hashmap to figure out what their username is, then send that in the email.
     * We'll do this in searchUsers(), and another function sendUsername().
     *
     * Note: this takes in a user object as the parameter. To do so, whoever calls this function will need to first call the
     * getUser() function in this class. We can change this to just the username to remove this step if needed.
     *
     * @param user User object to obtain email and randomID from
     * @return true on successful email sent, false otherwise
     */
    public boolean sendRandomID(final User user){

        final String randomID = "1234567";//user.getRandomID();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String body = "Hi Derrick This is an email from BudgetUs, sent because you forgot your password. Enter this ID:";
                    body+=randomID;
                    GmailSender sender = new GmailSender("budgetusemail@gmail.com", "budgetus123!");
                    sender.sendMail("Forgot Password", body , "budgetusemail@gmail.com", "dlam15@binghamton.edu");
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();



        return false;
    }



    /*
     * Can do in User class?
     *
     * 1st function fired when the user forgets their username. This emails the user
     * object the username field for the user.
     *
     *
     * Note: this takes in a user object as the parameter. To do so, whoever calls this function will need to first call the
     * getUser() function in this class. We can change this to just the username to remove this step if needed.
     *
     * @param user User object to obtain email and randomID from
     * @return true on successful email sent, false otherwise
     */
    public boolean sendUsername(User user){
        String email = user.getEmail();//email address of user
        String randomID = user.getRandomID();//random id to email
        return false;
    }

    /* Can do in User Class?

     * 2nd function fired when the user forgets their username or password. This checks if
     * the forgotID provided by the user matches the forgotID.
     *
     * @param user User object to obtain email from
     * @return true on successful email sent, false otherwise
     */
    //public boolean matchForgotID(9 digit id,user object){

    //}

    /* REMOVED
     * I don't think we need this: I can just call user.forgotPassword from matchForgotID
     */
    //public boolean forgotPassword(new password){}
}
