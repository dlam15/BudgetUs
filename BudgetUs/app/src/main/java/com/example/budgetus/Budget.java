package com.example.budgetus;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Budget {

    private ArrayList<Transaction> listOfTransactions = new ArrayList<>();//might use a different data structure
    private final double startingFunds;
    private double remainingFunds;
    Context c;

    /*
     *  Constructor requires startingFunds as well as a context (needed for a bitmap function in Transaction, so it gets passed there)
     */
    public Budget(double startingFunds, Context c){
        this.startingFunds = this.remainingFunds = startingFunds;
        this.c = c;
    }

    //can get, but shouldn't be allowed to set - we want to maintain valid record via transactions
    public double getStartingFunds() { return startingFunds; }
    public double getRemainingFunds() { return remainingFunds; }


     /*
     * Add a new transaction to the list of transactions. Last 4 parameters are optional. We also update the remaining funds after this transaction.
     *
     * @param amount the amount the transaction was for, can be negative (but I haven't decided when that should be used)
     * @param name the name for the transaction we can use to identify it
     * @param receipt OPTIONAL - path to a photo on the user's device, we use it to make a bitmap image attached to the transaction
     * @param description OPTIONAL - description for transaction, can be used when name doesn't explain enough
     * @param date OPTIONAL - date the transaction occurred on. Right now this is a Calendar object, I might accept String format and convert if that's easier for caller
     * @param category OPTIONAL - category from category enum - will probably change how the enum works, basic pre-defined choice right now
     */
    public void addTransaction(double amount, String name, Uri receipt, String description, Calendar date, Transaction.Category category){
        Transaction t = new Transaction(c, startingFunds, amount, name, receipt, description, date, category);
        listOfTransactions.add(t);
        remainingFunds = this.remainingFunds - amount;//necessary to update funds correctly
    }


    /*
     *  We need to provide a way to search the list of transactions - user could have huge list, needs to search for something.
     *  Haven't worked this out yet (how to tell what field they are searching by to avoid a slow search?),  but searchable fields
     *  are amount, name, description, date, category for now.
     *
     *  I'll just return the Transaction object and assume user can easily click on a transaction to work with it,  add/modify vars, sort, run stats, etc.
     *  I.e. front end has a way to display Transaction object.
     *
     * @param some searchable value
     * @return the Transaction object matching this criteria (more than 1?)
     */
    public Transaction findTransaction(double amount){
        return null;
    }


    /*
     *  A way to update a transaction (probably to add an optional field).
     *  Comes in with a transaction parameter - user can click on or we'll search for it.
     *  I haven't figured this out yet either - I'm imagining 1 screen/menu to update a transaction, not individual pop ups for edit name, edit amount, etc
     *  and then we just update anything that's not null or not changed?
     *  Also, will these fields be null, or will they be the previous values? probably the previous, but then how do we remove a value - provide null as picture would
     *  have to mean remove rather than don't update
     */
    public void modifyTransaction(Transaction transaction, double amount, String name,  Image receipt,  String description,  Calendar date, Transaction.Category category){
        //if(amount >0) transaction.amount = amount; //this needs to be more thought out - could have negatives?
        //if(name != null)
    }


    /*
     * Methods for sorting arraylist by different fields. Each works pretty much the same:
     *  -declare a nested class that implements a Comparator for Transactions
     *  -override compare function to compare what field we want
     *  -call Collections.sort with arraylist and comparator we made
     */


    /*
     * Sort by amount of transaction, low to high.
     */
    public void sortTransactionsLowToHigh(){
        class SortByAmountLowToHigh implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                return Integer.compare(((int) a.getAmount()), (int) b.getAmount());
            }
        }
        SortByAmountLowToHigh amountComparator = new SortByAmountLowToHigh();
        Collections.sort(listOfTransactions, amountComparator);
    }

    /*
     * Sort by amount of transaction, high to low.
     */
    public void sortTransactionsHighToLow(){
        class SortByAmountHighToLow implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                return Integer.compare(((int) b.getAmount()), (int) a.getAmount());
            }
        }
        SortByAmountHighToLow amountComparator = new SortByAmountHighToLow();
        Collections.sort(listOfTransactions, amountComparator);
    }


    /*
     * Sort by name of transaction, in alphabetical order.
     */
    public void sortTransactionsNameAlphabetical(){
        class SortByNameAlphabetical implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                return a.getName().compareTo(b.getName());
            }
        }
        SortByNameAlphabetical amountComparator = new SortByNameAlphabetical();
        Collections.sort(listOfTransactions, amountComparator);
    }

    /*
     * Sort by name of transaction, in reverse alphabetical order.
     */
    public void sortTransactionsNameReverseAlphabetical(){
        class SortByNameReverseAlphabetical implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                return b.getName().compareTo(a.getName());
            }
        }
        SortByNameReverseAlphabetical amountComparator = new SortByNameReverseAlphabetical();
        Collections.sort(listOfTransactions, amountComparator);
    }

    /* Now some optional ones - requires checking null
     * I will treat null as undefined/empty, so it will always come last (after the sorted/valid stuff)
     * For example in alphabetical order: A, B, null, null
     * In reverse alphabetical order: B, A, null
     * Although in both of those example the user will probably see a blank space/empty string rather than the word null.
     * I tested most of these but want to double check null always comes after.
     *
     * I'm also thinking about comparing by a required field when we have 2 nulls, but I haven't done that yet.
     */

    /*
     * Sort by description in alphabetical order.
     */
    public void sortTransactionsDescriptionAlphabetical(){
        for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
        class SortByDescriptionAlphabetical implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                //what worked for me so far is to compare by the empty string multiply by -1 to get all nulls after all sorted values
                if(a.getDescription() == null) return -1 * "".compareTo(b.getDescription());
                if(b.getDescription() == null) return -1 * a.getDescription().compareTo("");
                return a.getDescription().compareTo(b.getDescription());
            }
        }
        SortByDescriptionAlphabetical amountComparator = new SortByDescriptionAlphabetical();
        Collections.sort(listOfTransactions, amountComparator);
        for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
    }


    /*
     * Sort by description in reverse alphabetical order.
     */
    public void sortTransactionsDescriptionReverseAlphabetical(){
        for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
        class SortByDescriptionReverseAlphabetical implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                //same here, comparing with empty string will put nulls after everything else
                if(a.getDescription() == null) return b.getDescription().compareTo("");
                if(b.getDescription() == null) return "".compareTo(a.getDescription());
                return b.getDescription().compareTo(a.getDescription());
            }
        }
        SortByDescriptionReverseAlphabetical amountComparator = new SortByDescriptionReverseAlphabetical();
        Collections.sort(listOfTransactions, amountComparator);
        for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
    }


    /*
     * Sort by date in alphabetical order of old/past to new/future.
     */
    public void sortTransactionsDateOldToNew(){
        class SortByDateOldToNew implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                //was having an issue with null, so I'm making a calendar with 0 as the year and working with that, puts null after everything else
                Calendar zero = new GregorianCalendar(0, 0, 0);
                if(a.getDateOfTransaction() == null) return (-1) * zero.compareTo(b.getDateOfTransaction());
                if(b.getDateOfTransaction() == null) return (-1) * a.getDateOfTransaction().compareTo(zero);
                return a.getDateOfTransaction().compareTo(b.getDateOfTransaction());
            }
        }
        SortByDateOldToNew amountComparator = new SortByDateOldToNew();
        Collections.sort(listOfTransactions, amountComparator);
    }

    /*
     * Sort by date in alphabetical order of new/future to old/past.
     */
    public void sortTransactionsDateNewToOld(){
        class SortByDateNewToOld implements Comparator<Transaction>{
            @Override
            public int compare(Transaction a, Transaction b){
                //same as above, year 0 gives null after everything else (assuming we have no transactions from 0)
                Calendar zero = new GregorianCalendar(0, 0, 0);
                if(a.getDateOfTransaction() == null) return b.getDateOfTransaction().compareTo(zero);
                if(b.getDateOfTransaction() == null) return zero.compareTo(a.getDateOfTransaction());
                return b.getDateOfTransaction().compareTo(a.getDateOfTransaction());
            }
        }
        SortByDateNewToOld amountComparator = new SortByDateNewToOld();
        Collections.sort(listOfTransactions, amountComparator);
    }


    /*
     * Trying to do a quick breakdown of transactions by category. Didn't test and don't know how we'd display.
     *
     * @return a map/key val pairs of category, percentage we could use to make a pie chart or something
     */
    public Map<String, Double> breakdownInfo(){
        Map<String, Double> ret = new HashMap<>();
        int currCategoryEntries=0;
        for(Transaction.Category c: Transaction.Category.values()){
            currCategoryEntries = 0;
            for(Transaction t: listOfTransactions){
                if(t.getCategory() != null && t.getCategory() == c) currCategoryEntries++;
            }
            ret.put(c.toString(), (double) (currCategoryEntries/listOfTransactions.size()));
        }
        return null;
    }

}
