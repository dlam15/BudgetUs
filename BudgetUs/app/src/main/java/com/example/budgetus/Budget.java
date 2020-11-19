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
     * How do we take in some input and tell what it is? (double vs string vs date etc)?
     *
     *  I'll keep basic for now - each of the searchable types are of different variable types (except name and description)
     *  So I'll make a different function for each and assume we'll come up with some code to tell what field the user is searching by.
     *
     *  I'll also provide an intersection and a union function in case we need it.
     *
     *  I'll just return the Transaction object and assume user can easily click on a transaction to work with it,  add/modify vars, sort, run stats, etc.
     *  I.e. front end has a way to display Transaction object.
     *
     * @param some searchable value
     * @return an arraylist of the Transaction object(s) matching this criteria
     */
    public ArrayList<Transaction> findTransaction(double amount) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getAmount() == amount) ret.add(t);//i don't remember how these work - is this like a copy of a copy, doesn't work outside this?
        }
        System.out.println(ret.get(0).getAmount());//want to test
        return ret;
    }

    /*
     * As mentioned above, going to make a search function for each field. This is for Strings,
     * so name or description, we'll check both for "contains" the identifier string.
     *
     * @param string we're looking for in name or description
     * @return an arraylist of the Transaction object(s) with substring identifier in name or description field
     */
    public ArrayList<Transaction> findTransaction(String identifier) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getName().contains(identifier)) ret.add(t);//i don't remember how these work - is this like a copy of a copy, doesn't work outside this?
            else if(t.getDescription()!=null && t.getDescription().contains(identifier)) ret.add(t);
        }
        return ret;
    }

    /*
     * Search function for date. I'll do a bit more with ranges and whatnot as well, but probably not here.
     * However, I am providing 3 different find functions that take a Calendar - this one is for an exact match,
     * and the 2 below are for year match and month match.
     *
     * FIX - need to test and I want to provide year or month or day or all/mix and return any matches
     *
     * @param date of transaction we're looking for
     * @return an arraylist of the Transaction object(s) that occurred on this date
     */
    public ArrayList<Transaction> findTransaction(Calendar date) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && t.getDateOfTransaction().equals(date)) ret.add(t);
        }
        return ret;
    }

    /*
     * Search function for date by year. So, we'll return all transactions that occurred on the same year as
     * the date parameter.
     *
     * @param date of transaction who's year we'll compare with
     * @return an arraylist of the Transaction object(s) that occurred during the same year as date parameter
     */
    public ArrayList<Transaction> findTransactionByYear(Calendar date) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && t.getDateOfTransaction().get(Calendar.YEAR) == date.get(Calendar.YEAR)) ret.add(t);
        }
        return ret;
    }

    /*
     * Search function for date by month. So, we'll return all transactions that occurred on the same month as
     * the date parameter. This only checks month, and does not include year. For example, if March is the month
     * the date parameter has,  transactions from March 2000 and March 2020 will be returned (and any others occurring in March).
     *
     * @param date of transaction who's month we'll compare with
     * @return an arraylist of the Transaction object(s) that occurred during the same month as date parameter
     */
    public ArrayList<Transaction> findTransactionByMonth(Calendar date) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && t.getDateOfTransaction().get(Calendar.MONTH) == date.get(Calendar.MONTH)) ret.add(t);
        }
        return ret;
    }


    /*
     * Search function for category.
     *
     *
     * @param date of transaction we're looking for
     * @return an arraylist of the Transaction object(s) that occurred on this date
     */
    public ArrayList<Transaction> findTransaction(Transaction.Category category) {
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getCategory()!=null && t.getCategory() == category) ret.add(t);
        }
        return ret;
    }

    /*
     * Providing intersection and union of 2 arraylists of transactions in case we need it (name X and amt y)
     *
     * Basic intersection function. Take in 2 arraylists, if an element is in both add it to a return list.
     *
     * @param list1 the first arraylist of transactions
     * @param list2 the second arraylist of transactions
     * @return intersection of list1 and list2 (all elements in both lists)
     */
    public ArrayList<Transaction> transactionsIntersection(ArrayList<Transaction> list1, ArrayList<Transaction> list2){
        ArrayList<Transaction> retList = new ArrayList<Transaction>();
        for(Transaction t: list1){
            if(list2.contains(t)) retList.add(t);
        }
        return retList;
    }
    /*
     *
     * Basic union function. Take in 2 arraylists, return list of elements in either list with no duplicates.
     *
     * @param list1 the first arraylist of transactions
     * @param list2 the second arraylist of transactions
     * @return union of list1 and list2 (elements in list1 or list2)
     */
    public ArrayList<Transaction> transactionsUnion(ArrayList<Transaction> list1, ArrayList<Transaction> list2){
        ArrayList<Transaction> retList = new ArrayList<Transaction>();
        retList.addAll(list1);
        for(Transaction t: list2){
            if(!retList.contains(t)) retList.add(t);//avoid duplicates
        }
        return retList;
    }


    /*
     * First attempt to provide some sort of filtering rather than searching - all transactions from a time period/range
     * I'd like to reuse the findTransaction functions, but ranges are somewhat different.
     *
     * Here, we take in a startDate and endDate, and return all transactions that took place on and between these dates.
     * This uses the before and after methods provided by the calendar class.
     *
     * @param startDate date on which to start search
     * @param endDate date on which to end search
     * @return arraylist of all transactions that occurred on or between these dates
     */
    public ArrayList<Transaction> eventsBetween(Calendar startDate, Calendar endDate){
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && ((t.getDateOfTransaction().equals(endDate))||(t.getDateOfTransaction().equals(startDate)) || (t.getDateOfTransaction().after(startDate) && t.getDateOfTransaction().before(endDate)))) {
                ret.add(t);
                //System.out.println(t.getDateOfTransaction());
            }
        }
        return ret;
    }

    /*
     * Use fundsSpentOverTime to get amount spent, could add extra stuff like X% in this category, etc. - probably will add next
     * Can also do stuff with amountbefore and amountafter in each transaction, etc - will think about what's useful first
     *
     * Also need to figure out if making parameters Calendar class works for front end easily of if converting a string to a
     * Calendar object would be easier
     *
     * Also works with just year provided - we set undefined month as December and undefined day to 31st, allowing inclusive search
     *
     * @param startDate date on which to start search
     * @param endDate date on which to end search
     * @return amount spent between these dates
     */
    public double fundsSpentOverTime(Calendar startDate, Calendar endDate){
        //if date not defined, assume user wants month inclusive, set as 31st
        if(endDate.get(Calendar.DATE) == 0) endDate.set(Calendar.DATE, 31);
        if(startDate.get(Calendar.DATE) == 0) startDate.set(Calendar.DATE, 31);
        //if month not defined, assume user wants year inclusive, set as December
        if(endDate.get(Calendar.MONTH) == 0) endDate.set(Calendar.MONTH, 12);
        if(startDate.get(Calendar.MONTH) == 0) startDate.set(Calendar.MONTH, 12);
        ArrayList<Transaction> allTransactions = new ArrayList<>();
        allTransactions = eventsBetween(startDate, endDate);
        double runningTotal = 0;
        for(Transaction t: allTransactions) runningTotal+=t.getAmount();
        return runningTotal;
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
     * Sort by date in order of old/past to new/future.
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
     * Sort by date in order of new/future to old/past.
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
