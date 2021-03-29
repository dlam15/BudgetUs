package com.example.budgetus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * The most basic/smallest unit of our budgeting features
 * A transaction is some exchange between parties for some amount
 * I might make this a nested class of Budget - Budget is probably the only class that will use Transaction
 *
 * We have some optional fields here -things the user can provide if they want, but don't have to
 * Right now, those include a picture of a receipt, a description, a category, and a date of transaction.
 * I think I'll just have to return null when any of those haven't been defined, but I may use a @Nullable annotation
 * to remind everyone else that when trying to access an optional field, a return value of null is valid and means
 * that the object is not yet defined.
 */
public class Transaction {



    /*
     * basic enum of categories for now, might expand later
     * want to offer a number of categories that makes sense
     * but also want to allow user to provide a tag to specify where needed
     * or does this make no sense, and user should make their own categories? - still thinking about
     */
    public enum Category {
        FOOD, FUNDRAISING, OTHER;
        private String tag;

        public Category addTag(String t){
            tag = t;
            return this;
        }

        public String getTag(){return tag;}
    }

    private String category;


    /*
     * Variables. We have required fields and optional fields
     */

    //required - all transactions will have these fields defined
    private double amount;
    private String name;
    //required, but we handle these
    private double amountBefore;
    private double amountAfter;
    private Context c;

    //optional - A transaction can define 0 to all of these fields

    /*
     * so for images, I found how to save bitmaps from local URI on user's device
     * but I can't save bitmap as string and convert back from database
     * but I can do so with byte array, which is what we're doing here/why we're saving that
     *
     * execution flow:
     * - From front end: we obtain uri of image from user's phone -> convert to bitmap and save -> convert to byte array and save to database
     * - from back end: we obtain byte[] from database and save -> create bitmap and save
     */
    private byte[] receiptByteArray;//the bitmap converted to a byte array for database storage
    private Bitmap receipt = null;//the actual image we can display on front end
    private String description = null;
    //private Category category = null;
    @Exclude private Calendar dateOfTransaction = null;//Date is depreciated
    //might be able to use Calendar View to take input

    /*
     * Getters
     */
    public double getAmount() {
        return amount;
    }

    public String getName() { return name; }

    //the actual bitmap image we can display on the front end
    public Bitmap getReceipt() {
        return receipt;
    }

    //the byte conversion of the bitmap we can store in the database
    public byte[] getReceiptByteArray() { return receiptByteArray; }

    public String getDescription() {
        return description;
    }


    @Exclude public Calendar getDateOfTransaction() {
        return dateOfTransaction;
    }

    private String DateAsString;

    public String getDateAsString(){
        return DateAsString;
    }



    public String getCategory() {
        return category;
    }

    //amount in budget before this transaction
    public double getAmountBefore() { return amountBefore; }

    //amount in budget after this transaction
    public double getAmountAfter() { return amountAfter; }

    /*
     * Setters
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) { this.description = description; }

    public void setCategory(String category) { this.category = category; }

    public void setDateAsString(String dateAsString) { DateAsString = dateAsString; }

    //image updating setters- when we update 1, update all (bitmap and byte[])

    /*
    * We accept Uri from front end, but don't need to save it
    * so we just create bitmap and call that setter
    * @param uri - the path to the photo on the user's device, we use to create bitmap then don't need
     */
    public void setReceipt(Uri uri, Context c){
        try{
            setReceipt(ImageDecoder.decodeBitmap(ImageDecoder.createSource(c.getContentResolver(), uri)));
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /*
     * when we update bitmap, need to update byte array
     *
     * @param receipt the bitmap (actual image) to store
     */
    public void setReceipt(Bitmap receipt) {
        this.receipt = receipt;
        ByteArrayOutputStream output = new ByteArrayOutputStream(this.receipt.getByteCount());
        this.receipt.compress(Bitmap.CompressFormat.JPEG, 100, output);
        this.receiptByteArray = output.toByteArray();
    }


    /*
     * when we update byte array, need to update bitmap
     *
     * @param receiptByteArray the image represented in bytes, we can decode to get image
     */
    public void setReceiptByteArray(byte[] receiptByteArray) {
        this.receiptByteArray = receiptByteArray;
        this.receipt = BitmapFactory.decodeByteArray(this.receiptByteArray, 0, this.receiptByteArray.length);
    }

    //takes a Calendar object, could change to whatever's easiest
    public void setDateOfTransaction(Calendar date) { this.dateOfTransaction = date; }

    //will try to clean up, can try to use for front-end display too
    @Override
    public String toString(){
        String ret = "Transaction\n";
        ret+="\tName: "+this.name+"\n";
        ret+="\tAmount: "+this.amount+"\n";
        ret+="\tAmount Before: "+this.amountBefore+"\n";
        ret+="\tAmount After: "+this.amountAfter+"\n";
        ret+="\tContains Image: ";
        ret+= (this.receipt != null) +"\n";
        ret+="\tDescription: ";
        if(this.description == null) ret+="Undefined\n";
        else ret+=this.description+"\n";
        ret+="\tDate: ";
        if(this.dateOfTransaction == null) ret+="Undefined\n";
        else ret+=this.dateOfTransaction.getTime().toString()+"\n";
        ret+="\tCategory: ";
        if(this.category == null) ret+="Undefined\n";
        else ret+=this.category+"\n";
        return ret;
    }

    /*
     * Probably not the final version of this function. I'm trying to pass back some basic/quick info.
     * What I picture is a list of recent transactions, and a bit of info for each, like amount,
     * name, and how much was in the budget before and after. So, I'll send those values back here.
     *
     * Might not use, and if we do I'll probably need a better way to pass it that's not a string.
     *
     * @return a concatenated string of name, amount, amountBefore, and amountAfter for this
     */
    public String quickInfo(){
        return "Name: " + this.name +" Amount: "+ this.amount +" Amount Before Transaction: "+ this.amountBefore + " Amount After Transaction: " + this.amountAfter;
    }

    /*
     * I decided that we'll use the same constructor no matter how many optional fields are defined.
     * We can provide null for any of the last 5 parameters (except context if receipt is valid), but the first 3 must be provided.
     * I probably don't need to check for null here because I'll just set as null, but its a good reminder that these can be null.
     *
     * I also require the startingAmount variable so that each transaction is a mini-record of the budget's funds overtime.
     *
     * @param startingAmount the funds in the budget when this transaction is made
     * @param cost the amount the transaction was for, can be negative (but I haven't decided when that should be used)
     * @param name the name for the transaction we can use to identify it
     * @param c OPTIONAL a context variable needed when creating image
     * @param receipt OPTIONAL - path to a photo on the user's device, we use it to make a bitmap image attached to the transaction
     * @param description OPTIONAL - description for transaction, can be used when name doesn't explain enough
     * @param date OPTIONAL - date the transaction occurred on. Right now this is a Calendar object, I might accept String format and convert if that's easier for caller
     * @param category OPTIONAL - category from category enum - will probably change how the enum works, basic pre-defined choice right now
     */
    public Transaction(double startingAmount, double cost, String name, Context c, Uri receipt,  String description,  Calendar date,  String category){
        this.amountBefore = startingAmount;
        this.amountAfter = this.amountBefore - cost;//handles positive and negative
        this.amount = cost;
        this.name = name;

        //optionals
        this.c = c;//needed for image
        if(receipt!=null && c!=null) setReceipt(receipt, c); //sets this.receipt and this.receiptByteArray, which assume object isn't null, need to check
        //may add another parameter to make image from byte array (from database entry)
        //don't need to check the rest because they're null already
        this.description = description;
        this.dateOfTransaction = date;
        if(this.dateOfTransaction != null){
            this.DateAsString = getDateOfTransaction().get(Calendar.MONTH) +"/" + getDateOfTransaction().get(Calendar.DATE) +"/" + getDateOfTransaction().get(Calendar.YEAR);
        }
        this.category = category;
    }
    public Transaction(){
    }
}
