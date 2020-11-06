package com.example.budgetus;

import android.media.Image;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/*
 * The most basic/smallest unit of our budgeting feature.
 * A transaction is some exchange between parties for some amount for some object.
 *
 * A key issue I'm having is optional variables. There are a number of fields the user should
 * be allowed to leave blank. However, I want to make creating a transaction easy, and use the same
 * constructor. Doing so means we'll be dealing with null or checking for some default value?
 */
public class Transaction {

    //variables

    //required. All transactions will have these fields defined.
    private double amount;
    private String name;



    //optional. A transaction can define 0 to all of these fields and anywhere in between.
    private Image receipt = null;//have not looked at - Image class may not be best
    private String description = null;
    private Calendar dateOfTransaction = null;//date is depreciated
    //should allow different levels:
    //month and year
    //month and week and year
    //month and day and year
    //m/d/y and hour
    //m/d/y and hour and minute
    //might be able to use Calendar View to take input

    //basic for now, might expand later
    //want to offer a number of categories that makes sense
    //but also want to allow user to provide a tag to specify where needed
    //or does this make no sense, and user should make their own categories
    public enum Category {
        FOOD, FUNDRAISING, OTHER;
        private String tag;

        public Category addTag(String t){
            tag = t;
            return this;
        }

        public String getTag(){return tag;}
    }
    private Category category;


    //basic getters
    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public Image getReceipt() {
        return receipt;
    }

    public String getDescription() {
        return description;
    }

    public Calendar getDateOfTransaction() {
        return dateOfTransaction;
    }

    public Category getCategory() {
        return category;
    }


    //basic setters
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReceipt(Image receipt) {
        this.receipt = receipt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //little different - make it easy to provide numberical date rather than create calendar object
    //might not actually be any easier, I'll see
    public void setDateOfTransaction(int year, int month, int day, int hour, int minute) {
        Calendar date = Calendar.getInstance();
        date.clear();
        /*
        if(minute == -1) date.set(year, month, day, hour, 0);
        if(hour == null) date.set(year, mnullonth, day);
        if(day == null) date.set(year, month);
        else
        */
        date.set(year, month, day, hour, minute);
        this.dateOfTransaction = dateOfTransaction;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void test(){
        dateOfTransaction.getInstance();//have to init like this first
        dateOfTransaction.clear();
        dateOfTransaction.set(2020,05,05);
        category = Category.FOOD;
        category.addTag("pizza");
        System.out.println(category.getTag());
    }

    /*
     * Haven't decided what to do with this yet exactly, but I'd like to use the same constructor for making every transaction.
     * Otherwise, we'd need one for every possible combo
     * Or we could just set the required ones, and just add what we have after? But then making an object is somewhat annoying
     */
    public Transaction(double amount, String name,  Image receipt,  String description, Calendar date,  Category category){
        this.amount = amount;
        this.name = name;
        if(receipt != null) this.receipt = receipt;
        if(description != null) this.description = description;
        if(date != null) this.dateOfTransaction = date;
        if(category!=null) this.category = category;
    }


    /* A possible option for optional variables. If we don't store null,
     * and instead use some default value, we'd need checks like this to
     * tell that the field has not been provided by the user.
     */
    public boolean checkValidityDate(){
        //if(this.dateOfTransaction == somedefaultval) return false;
        return true;
    }


}
