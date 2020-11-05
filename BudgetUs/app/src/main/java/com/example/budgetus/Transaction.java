package com.example.budgetus;

import android.media.Image;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/*
 * The most basic/smallest unit of our budgeting feature.
 * A transaction is some exchange between parties for some amount for some object.
 */
public class Transaction {

    //variables

    //required
    private double amount;
    private String name;

    //optional
    private Image receipt;//have not looked at - Image class may not be best
    private String description;
    private Calendar dateOfTransaction;//date is depreciated
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

    public void test(){
        dateOfTransaction.getInstance();//have to init like this first
        dateOfTransaction.clear();
        dateOfTransaction.set(2020,05,05);
        category = Category.FOOD;
        category.addTag("pizza");
        System.out.println(category.getTag());
    }

    private Transaction (Builder builder){
        Objects.requireNonNull(builder.name);
        Objects.requireNonNull(builder.amount);
        name = builder.name;
        amount = builder.amount;
        receipt = builder.receipt;
        description = builder.description;
        dateOfTransaction = builder.dateOfTransaction;
        category = builder.category;
    }

    public static Builder newBuilder(double amount, String name){
        return new Builder(amount, name);
    }

    static final class Builder{
        private double amount;
        private String name;
        private Image receipt;
        private String description;
        private Calendar dateOfTransaction;
        private Category category;

        private Builder(double amt, String nm) {
            this.name = nm;
            this.amount = amt;
        }

        public Transaction.Builder withAmount(double amt){
            amount = amt;
            return this;
        }

        public Transaction.Builder withName(String nm){
            name = nm;
            return this;
        }

        public Transaction.Builder withReceipt(Image recpt){
            receipt = recpt;
            return this;
        }

        public Transaction.Builder withDescription(String des){
            description = des;
            return this;
        }

        public Transaction.Builder withDate(Calendar date){
            dateOfTransaction = date;
            return this;
        }

        public Transaction.Builder withCategory(Category c){
            category = c;
            return this;
        }

        public Transaction build(){
            return new Transaction(this);
        }


    }


}
