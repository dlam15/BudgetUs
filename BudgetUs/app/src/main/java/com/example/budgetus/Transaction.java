package com.example.budgetus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
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
     * but I can't save bitmap as string and convert back for json
     * but I can do so with byte array, which is what we're doing here/why we're saving that
     *
     * flow:
     * - From front end: we obtain uri of image from user's phone -> convert to bitmap and save -> convert to byte array and save
     * - from back end: we obtain byte[] from database and save -> create bitmap and save
     */
    private byte[] receiptByteArray;//the bitmap converted to a byte array for JSON storage
    private Bitmap receipt = null;//the actual image we can display on front end
    private String description = null;
    private Category category = null;
    private Calendar dateOfTransaction = null;//Date is depreciated
    //should allow different levels?
    //month and year
    //month and week and year
    //month and day and year
    //m/d/y and hour
    //m/d/y and hour and minute
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

    public Calendar getDateOfTransaction() {
        return dateOfTransaction;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmountBefore() { return amountBefore; }

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

    public void setCategory(Category category) { this.category = category; }

    //image updating setters- when we update 1, update all (bitmap and byte[])

    /*
    * We accept Uri from front end, but don't need to save it
    * so we just create bitmap and call that setter
    * @param uri - the path to the photo on the user's device, we use to create bitmap then don't need
     */
    public void setReceipt(Uri uri){
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

    public void setDateOfTransaction(Calendar date) { this.dateOfTransaction = date; }


    /* work in progress
     * little different - make it easy to provide numerical date rather than create calendar object
     * might not actually be any easier, I'll see
    */
    public void setDateOfTransaction(int year, int month, int day, int hour, int minute) {
        Calendar date = Calendar.getInstance();
        date.clear();
        /*
        if(minute == -1) date.set(year, month, day, hour, 0);
        if(hour == null) date.set(year, month, day);
        if(day == null) date.set(year, month);
        else
        */
        date.set(year, month, day, hour, minute);
        this.dateOfTransaction = dateOfTransaction;
    }


    /*
     * I decided that we'll use the same constructor no matter how many optional fields are defined.
     * We can provide null for any of the last 4 parameters, but the first 4 must be provided.
     * I probably don't need to check for null here because I'll just set as null, but its a good reminder that these can be null.
     *
     * I also require the startingAmount variable so that each transaction is a mini-record of the budget's funds overtime.
     *
     * @param c a context variable needed when calling some global functions (specifically a bitmap function here)
     * @param startingAmount the funds in the budget when this transaction is made
     * @param cost the amount the transaction was for, can be negative (but I haven't decided when that should be used)
     * @param name the name for the transaction we can use to identify it
     * @param receipt OPTIONAL - path to a photo on the user's device, we use it to make a bitmap image attached to the transaction
     * @param description OPTIONAL - description for transaction, can be used when name doesn't explain enough
     * @param date OPTIONAL - date the transaction occurred on. Right now this is a Calendar object, I might accept String format and convert if that's easier for caller
     * @param category OPTIONAL - category from category enum - will probably change how the enum works, basic pre-defined choice right now
     */
    public Transaction(Context c, double startingAmount, double cost, String name, Uri receipt,  String description,  Calendar date,  Category category){
        this.c = c;//need for bitmap stuff for some reason
        this.amountBefore = startingAmount;
        this.amountAfter = this.amountBefore - cost;//handles positive and negative
        this.amount = cost;
        this.name = name;
        if(receipt!=null) setReceipt(receipt); //sets this.receipt and this.receiptByteArray, need to check for null
        //don't need to check the rest because they're null already, but its a good reminder
        //or I might set some flag for each one and use that to check if its undefined rather than checking for null?
        if(description != null) this.description = description;
        if(date != null) this.dateOfTransaction = date;
        if(category!=null) this.category = category;
    }

    /* A possible option for optional variables. If we don't store null,
     * and instead use some default value, we'd need checks like this to
     * tell that the field has not been provided by the user.
     *
     * But I'm not doing that so far and probably won't need to
     */
    public boolean checkValidityDate(){
        //if(this.dateOfTransaction == somedefaultval) return false;
        return true;
    }


}
