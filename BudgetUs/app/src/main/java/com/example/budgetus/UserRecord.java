package com.example.budgetus;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class UserRecord {

    //private Context mContext; matt
    private Map<String, User> hashmap = new HashMap<String, User>(1000);

    //public UserRecord (Context context){ matt
    public UserRecord (){
        //this.mContext = context; matt
        try {
            FileProcessor fp = new FileProcessor();
            hashmap = fp.getUserMap();
            //System.out.println(getUser("dlam15").getSchool());//prints Binghamton
            //System.out.println(getUser("admin").getName());//prints John Doe
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
     * A function for some simple tests of hashmap, adding users, reading info, etc
     * What I've learned:
     *  -hashmap handles pretty much everything (hash function, collisions, expanding, etc)
     *  -reading from a null object will crash the entire app, so we might want some checks or catches?
     *  -security stuff works
     *  -Derrick's database stuff works
     */
  /* public void test()  {
        try{
            byte[] salt = PasswordEncryptionService.generateSalt();
            System.out.println("salt: " + Arrays.toString(salt));
            byte[] pw = PasswordEncryptionService.getEncryptedPassword("2e8u13189rh12d1f3dcx]1[dq3", salt);
            System.out.println("password: " + Arrays.toString(pw));
            System.out.println(PasswordEncryptionService.authenticate("2e8u13189rh12d1f3dcx]1[dq3", pw, salt));
            System.out.println(PasswordEncryptionService.authenticate("notpassword", pw, salt));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }*/


    /* We use this function to check if a username a new user is trying to use is valid. For
     * it to be valid, it must not be in use by someone else. So, we just check the hashmap for the name.
     *
     * @param username the username the registering user wants to use and we need to check for
     * @return true if the username is not in use, false if it is
     */
    public boolean checkUsername(String username){
        return !hashmap.containsKey(username);
    }

     /* We use this function to check if a email a new user is trying to use is valid. For
     * it to be valid, it must not be in use by someone else. So, we just check the hashmap for the email.
     *
     * @param email the email the registering user wants to use and we need to check for
     * @return true if the email is not in use, false if it is
     */
    public boolean checkEmail(String email){
        for(User userElement : hashmap.values()){
            User currUser = userElement;
            if(currUser.getEmail().equals(email)) return false;
        }
        return true;
    }

    /* MODIFIED - assumes that the User class has checked that the provided username and email are valid
     * with checkUsername() and checkEmail. If both of these functions return true, the user can use what they provided.
     * Then, the User class can create a User object and provide it here. We should do this instead of just providing the
     * username because I need to store a User object here, which contains fields I don't have access to here.
     *
     * So, make sure those 2 functions are called first, because I will not check here, and if the username was in use,
     * the old user will be overwritten without us noticing.
     *
     * Java's hashmap handles collisions for us, so even if 2 usernames hash to the same value, both usernames can exist.
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
        hashmap.put(username, newUser);
        System.out.println("Success");
        return true;
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
            //if(userElement.getEmail().equalsIgnoreCase (email)) return userElement; britania
        }
        return null;
    }


    /*
     * Remove a user from the hashmap. We check if they exist, and if they don't print out a message
     * and return false instead. Again, I'd like a better place to put these messages.
     *
     * @param user username of object to remove
     * @return true on successful removal, false on unsuccessful removal
     */
    public boolean removeUser(String username){
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
            //assert accessedUser != null; britania
            //if(accessedUser.getPassword().equals(password)){ matt
            if(password == accessedUser.getPassword()){
                System.out.println("Successful login");
                return true;
            }

               /*
               //to use the encryption code, wrap this in try catch
               byte[] salt = accessedUser.getSalt();
               byte[] actualPassword = accessedUser.getPassword();
               return PasswordEncryptionService.authenticate(password, salt, actualPassword);
                */

             else{
                System.out.println("Incorrect password");
                return false;
            }
        }
    }







    /*
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
        String randomID = user.getRandomID();
        //String randomID = "randomID placeholder"; matt
        String body = "Hello, this is an email from BudgetUs, sent because you forgot your login info. Enter this code to regain access: " + randomID;
        return sendEmail(body, "Forgot Credentials", email);
    }


    /*
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
/* britania
    public boolean sendUsername(User user){
        String email = user.getEmail();//email address of user
        String randomID = "123gruwguwecfcrugrb2y4i32t47vtc37";//user.getRandomID();//random id to email
        return false;
    }*/

    /*
     * 2nd function fired when the user forgets their username or password. This checks if
     * the forgotID provided by the user matches the forgotID. We will also update the user's
     * random id after this attempted match.
     *
     * @param id the id provided by the user attempting to login
     * @param user User object to obtain real ID from
     * @return true on successful match, false otherwise
     */
    public boolean matchForgotID(byte[] id, User user) {
        boolean ret = false;
        //if(Arrays.equals(id, user.getRandomID)) ret = true;
        //user.setRandomID();//in either case, we should generate a new random ID
        return ret;
    }

    /*
     * This will be the function called when trying to register a user. We check the username
     * and email fields they provide to see if they are already in use. If not, we can create the user.
     * Otherwise, the user will have to provide different info.
     *
     * After this function returns true, we make a new user object with what the new user provided, and
     * then call addUser. We will also print out which of the fields are in use (if any). We could change this to the
     * return value to pass somewhere to tell the user.
     *
     * @param username the new user's desired username
     * @param email the new user's desired email
     * @return true if both are available, false otherwise
     */
    public boolean attemptRegister(String username, String email){
        if(!checkUsername(username)){
            System.out.println("Username in use");
            return false;
        }
        if(!checkEmail(email)){
            System.out.println("Email in use");
            return false;
        }
        return true;
    }

    //This code requires the following functions to be created in the User class
    //I did not make them so that we did not have any merge conflicts
    //Firstly, we need a char[] randomID field - this will store the random ID
    //using char[] so that I can reuse the secure random salt generator - I figured this should be securely random too
    //password field needs to changed to char[] as well
    //char[] getRandomID() //returns randomID
    //void setRandomID() //will call generateHash and use this random value to set randomID field


}
