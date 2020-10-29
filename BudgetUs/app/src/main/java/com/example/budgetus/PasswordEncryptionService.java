package com.example.budgetus;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/*
 * We shouldn't store plaintext passwords. One common way to implement a safer system is with a salted hash.
 * We generate some random string, called a salt, and mix this with the password in some way. We then feed this salted password
 * to a hash function, and save the result as the user's "password." We also save the salt. When the user wants to login, repeat the
 * process and check the saltedHashedPassword. By doing so, we are storing the password in a safe way with little overhead.
 */

//I made it final and all of its functions static because I thought that made sense
public final class PasswordEncryptionService {

    //prevents instantiation - making the class essentially static
    private PasswordEncryptionService(){ }

    //the next 3 functions are a simple implementation of securely storing passwords I found at
    //https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
    //it could probably be improved or use a better hash function or replaced with the ability to sign in with another service (Google)

    /*
     * First step in storing a user's password securely. We generate a salt, which is a random string.
     * We use SecureRandom to ensure that this value is unpredictably random and cannot be predicted in any way.
     * This salt will be combined with the user's password. This means that 2 exact passwords will not hash
     * to the same value, as the random salt will make them different. We store this salt in plaintext - it does
     * not need to be secret.
     *
     * @return a string (byte array) that is our 64 bit random salt
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        //important to use SecureRandom instead of just Random
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return salt;
    }


    /*
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
    public static byte[] getEncryptedPassword(String password, byte[] salt)  throws NoSuchAlgorithmException, InvalidKeySpecException {
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


    /*
     * This function checks the provided password against the password we have on record for the user.
     * We are not storing the actual password. Instead, we store the result from getEncryptedPassword. So,
     * to check a possible password against the stored one, we need to salt and hash the provided password
     * and compare the 2.
     *
     * @param attemptedPassword what the user trying to log in provides as the password
     * @param encryptedPassword the account's actual password
     * @param salt the account's salt, used to generate the encryptedPassword, so its needed to regenerate and check
     */
    public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
        -when the user logs in, we call authenticate() with what they provided as a password, as well as the password and salt stored for the account they are trying to access
     */

}
