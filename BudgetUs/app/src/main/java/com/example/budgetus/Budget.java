package com.example.budgetus;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Budget {

    private ArrayList<Transaction> listOfTransactions = new ArrayList<>();//might use a different data structure
    private ArrayList<Transaction> requestedTransactions = new ArrayList<>();//members can make requests, they go in here until approved or denied
    private ArrayList<String> categoriesInUse = new ArrayList<>();//need to find a spot to initialize this
    private final double startingFunds;
    private double remainingFunds;
    Context c;


    //I'm still testing these
    //I don't know who could call them
    public void saveToDB(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("matt budget test/budget1");
        for(Transaction t: getListOfTransactions()){
            t.setDateOfTransaction(null);
            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(t);
        }
    }

    public void loadFromDB(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("matt budget test/budget1/");
        final Transaction[] t = new Transaction[1];
        myRef.child("t1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("MADE IT");
                t[0] = dataSnapshot.getValue(Transaction.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
        listOfTransactions.add(t[0]);
    }



    //The following functions can be called by the Admin/Owner (same as e-board)
    //The following functions can be called by the E-Board/Members with permissions
    //mostly adding/modifing transactions, making a new budget, etc.


    /*
     *  Constructor requires startingFunds as well as a context (needed for a bitmap function in Transaction, so it gets passed there)
     */
    public Budget(double startingFunds, Context c){
        this.startingFunds = this.remainingFunds = startingFunds;
        this.c = c;
    }

    /*
     * Add a new transaction to the list of transactions. Last 5 parameters are optional. We also update the remaining funds after this transaction.
     *
     * @param amount the amount the transaction was for, can be negative (but I haven't decided when that should be used)
     * @param name the name for the transaction we can use to identify it
     * @param c OPTIONAL a context variable needed when creating image
     * @param receipt OPTIONAL - path to a photo on the user's device, we use it to make a bitmap image attached to the transaction
     * @param description OPTIONAL - description for transaction, can be used when name doesn't explain enough
     * @param date OPTIONAL - date the transaction occurred on. Right now this is a Calendar object, I might accept String format and convert if that's easier for caller
     * @param category OPTIONAL - category from category enum - will probably change how the enum works, basic pre-defined choice right now
     */
    public void addTransaction(double amount, String name, Uri receipt, String description, Calendar date, String category){
        Transaction t = new Transaction(startingFunds, amount, name, c, receipt, description, date, category);
        listOfTransactions.add(t);
        remainingFunds = this.remainingFunds - amount;//necessary to update funds correctly
    }

    /*
     * Same as above, but use a Transaction object. Used for adding pending/requested transactions.
     *
     * @param t Transaction object to add to listOfTransactions
     */
    public void addTransaction(Transaction t){
        listOfTransactions.add(t);
        remainingFunds = this.remainingFunds - t.getAmount();//necessary to update funds correctly
    }

    /*
     *  A way to update a transaction (probably to add an optional field).
     *
     * I picture it working like this from front-end:
     * -user selects a transaction (so we have the object)
     * -user selects menu option to update (so we know to call this function)
     * -update opens the same page as add (i.e. boxes for each possible field)
     * -fields will be displayed as they are currently for the transaction
     * -user adds, removes, edits, etc
     * -front-end sends us the transaction object to edit, and all of the fields
     * -we'll just update all with what's provided
     * -its more likely that only a field or 2 is changed, but why bother checking for a change if we're just updating with itself?
     *
     * This makes me realize that optional items need a "null representation" on the front end. For example, if no date is specified, display "No date."
     * I'll keep this in mind for now, but they should probably be defined in Transaction.
     *
     * So, this function pretty much makes a new transaction and replaces an old one with it.
     *
     */
    public void modifyTransaction(Transaction transaction, double amount, String name,  Uri receipt,  String description,  Calendar date, String category){
        Transaction updatedTransaction = new Transaction(transaction.getAmountBefore(), amount, name, c, receipt, description, date, category);
        listOfTransactions.set(listOfTransactions.indexOf(transaction), updatedTransaction);//replace at index of old
    }

    /*
     * Members who cannot add or modify transactions directly must make requests. They go into
     * this ArrayList. I've decided that only e-board members/people who can approve or deny these requests
     * should be able to deal with this list.
     */
    public ArrayList<Transaction> getRequestedTransactions() {
        return requestedTransactions;
    }


    //these 2 functions are a work in progress right now
    //the idea is simple, but I haven't decided how they'll work yet
    //i.e. how is an e-board member going to see a transaction in the pending list to begin with?
    //we'll have a section for it, and I'll need to modify the functions below to take a list of transactions as a parameter
    //then we can use for the requested list or the real/approved list

    public void approveRequestedTransaction(Transaction t){
        addTransaction(t);
        requestedTransactions.remove(t);
        //somehow send notification to user that requested
    }

    public void denyRequestedTransaction(Transaction t){
        requestedTransactions.remove(t);
        //somehow send notification to user that requested
    }



    //The following functions can be called by any member of the group
    //they are mostly searches and info about the budget

    /*
     * Members without the ability to directly add a Transaction should be able to request a new Transaction.
     * I haven't figured out how this will work, but it would have to go something like this:
     * -member makes request with Transaction
     * -This is put in some pending queue that E-board members can see
     * -E-board members have ability to look at transactions in the queue, approve or deny
     * -Approving just calls addTransaction
     * -Deny does not
     * -In either case, maybe the member that requested should get a notification (this is a later feature)
     */
    public void requestTransaction(double amount, String name, Uri receipt, String description, Calendar date, String category){
        Transaction t = new Transaction(startingFunds, amount, name, c, receipt, description, date, category);
        requestedTransactions.add(t);
    }

    //can get, but shouldn't be allowed to set - we want to maintain valid record via transactions
    public double getStartingFunds() { return startingFunds; }
    public double getRemainingFunds() { return remainingFunds; }

    public ArrayList<Transaction> getListOfTransactions(){ return listOfTransactions; }

    /*
     * Users define the category of a Transaction. This function returns all
     * categories in use in the budget.
     *
     * For now I'll always update/search for changes before getting
     * but I'll probably move the updating elsewhere eventually.
     *
     * @return an arraylist of strings containing all categories used in the budget currently
     */
    public ArrayList<String> getCategoriesInUse() {
        if(categoriesInUse!=null) categoriesInUse.clear();
        for(Transaction t: getListOfTransactions()){
            if(t.getCategory()!= null && !categoriesInUse.contains(t.getCategory())) categoriesInUse.add(t.getCategory());
        }
        return categoriesInUse;
    }




    /*
     *  We need to provide a way to search the list of transactions - user could have huge list, needs to search for something.
     *  Not sure exactly how it will work, but maybe a form - they are given a way to input info by field, a search will be similar.
     *  I.e. we'll know what field to search by on front end.
     *
     *  If not, we can just search every field for a match, which will be slower with bigger lists, but ok for smaller ones.
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
            if(t.getAmount() == amount) ret.add(t);
        }
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
     * @param date of transaction we're looking for
     * @return an arraylist of the Transaction object(s) that occurred on this date
     */
    public ArrayList<Transaction> findTransactionByCategory(String category) {
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
        ArrayList<Transaction> retList = new ArrayList<>();
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
        ArrayList<Transaction> retList = new ArrayList<>();
        retList.addAll(list1);
        for(Transaction t: list2){
            if(!retList.contains(t)) retList.add(t);//avoid duplicates
        }
        return retList;
    }


    /*
     Not sure how easy it'll be to provide Calendar object from front end/what checks can be done there, so I added this function here,
     It checks that the month and day are defined, and if they're not, sets them as December and the 31st respectively.
     This allows us to use Calendars that are just set to a year while providing the expected behavior (e.g. 2019 - 2020 is Jan 1st 2019
     to Dec 31st 2020 - this is not what Calendar does by default).

     @param date the date to check for month and day
     @return date, with Dec 31st as the month and day if it was not previously set
     */
    Calendar defineCalendar(Calendar date){
        //if day not defined, assume user wants month inclusive, set as 31st
        if(date.get(Calendar.DATE) == 0) date.set(Calendar.DATE, 31);
        //if month not defined, assume user wants year inclusive, set as December
        if(date.get(Calendar.MONTH) == 0) date.set(Calendar.MONTH, 12);
        return date;
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
        startDate = defineCalendar(startDate);
        endDate = defineCalendar(endDate);
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
     * Like eventsBetween, but just all events after startDate.
     *
     * @param startDate date on which to start search
     * @return arraylist of all transactions that occurred on or after startDate
     */
    public ArrayList<Transaction> eventsAfter(Calendar startDate){
        startDate = defineCalendar(startDate);
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && (t.getDateOfTransaction().equals(startDate) || t.getDateOfTransaction().after(startDate))) {
                ret.add(t);
                //System.out.println(t.getDateOfTransaction());
            }
        }
        return ret;
    }

    /*
     * Like eventsBetween, but just all events before endDate.
     *
     * @param endDate date on which to end search
     * @return arraylist of all transactions that occurred on or before endDate
     */
    public ArrayList<Transaction> eventsBefore(Calendar endDate){
        endDate = defineCalendar(endDate);
        ArrayList<Transaction> ret = new ArrayList<>();
        for(Transaction t: listOfTransactions){
            if(t.getDateOfTransaction()!=null && (t.getDateOfTransaction().equals(endDate) || t.getDateOfTransaction().before(endDate))) {
                ret.add(t);
                //System.out.println(t.getDateOfTransaction());
            }
        }
        return ret;
    }

    /*
     * Another feature we can support is planned/future transactions. I was going to make a whole new system
     * for this, but we can support by just adding normally, but with a date in the future. Then, getting
     * future transactions is just calling eventsAfter(Today).
     *
     * @return all transactions in listOfTransactions that have a date after the current date.
     */
    public ArrayList<Transaction> getPlannedTransactions(){
        return eventsAfter(Calendar.getInstance());
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
        ArrayList<Transaction> allTransactions;
        allTransactions = eventsBetween(startDate, endDate);
        double runningTotal = 0;
        for(Transaction t: allTransactions) runningTotal+=t.getAmount();
        return runningTotal;
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
        //for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
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
        //for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
    }

    /*
     * Sort by description in reverse alphabetical order.
     */
    public void sortTransactionsDescriptionReverseAlphabetical(){
        //for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
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
        //for(Transaction t: listOfTransactions) System.out.println(t.getDescription());
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
     * Find all transactions between 2 dates, where either or both of them may be null or missing info.
     * Used in the breakdown functions below.
     *
     * @param startDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @param endDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @return an ArrayList of transactions between the startDate and endDate if neither are null, after the
     *      startDate if endDate is null, before the endDate if the startDate is null, or just all transactions
     *      if both are null.
     */
    public ArrayList<Transaction> getTransactionsList(Calendar startDate, Calendar endDate){
        if(startDate != null && endDate != null){//we want to search transactions in a time frame
            return eventsBetween(startDate, endDate);
        }else if(startDate != null){//search all after startDate
            return eventsAfter(startDate);
        }else if(endDate != null){//search all before endDate
            return eventsBefore(endDate);
        }else{//dates not set, search all
            return this.listOfTransactions;
        }
    }

    /*
     * A function we can use to display a pie chart . Returns a PieChart of string, double PieEntries,
     * where the string is the category, and the double is the percentage of transactions in that category. We can also
     * provide dates as parameters. Might add another function for different searches.
     *
     * @param startDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @param endDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @return a PieChart with category name, percentage of transactions in that category
     */
    public PieData pieChartByCategory(Calendar startDate, Calendar endDate){
        List<PieEntry> entries = new ArrayList<>();

        ArrayList<Transaction> currentTransactionsList = getTransactionsList(startDate, endDate);

        double currCategoryEntries=0;
        //System.out.println(currentTransactionsList.size());
        for(String c: getCategoriesInUse()){
            currCategoryEntries = 0;
            for(Transaction t: currentTransactionsList){
                if(t.getCategory() != null && t.getCategory() == c) currCategoryEntries++;
            }
            //System.out.println(currCategoryEntries/currentTransactionsList.size());
            entries.add(new PieEntry((float) (currCategoryEntries/currentTransactionsList.size()), c));
        }
        PieDataSet set = new PieDataSet(entries, "");//don't think this label does anything

        //but I can do colors here - will have to see how many entries we have first
        set.setColors(Color.rgb(0,0,255), Color.rgb(0,255,0), Color.rgb(255,0,0), Color.rgb(255,255,0), Color.rgb(255,0,255));

        set.setValueTextSize(16f);

        PieData data = new PieData(set);
        data.setValueTextSize(16f);

        //unfortunately there's a few things that need to be done on front end:
        /*
        com.github.mikephil.charting.charts.PieChart pieChart = findViewById(R.id.chart);//get from XML
        pieChart.setData(data);//populate XML with this data
        pieChart.setUsePercentValues(true);//use percentages
        pieChart.setCenterText("Percent of Y-Values in Predefined Ranges");//set text - this does nothing when in the DataSet
        pieChart.getDescription().setEnabled(false);//remove description from corner of screen
         */

        return data;
    }

    /*
     * Another function for a pie chart, but done by cost. Will return PieChart with percentage of transactions less
     * than or equal to each value in the provided array (and greater than the last chunk we looked at). If no array is
     * provided, we'll do $10, $50, $100, $500, $1000 and >$1000 (i.e, 0-10, 10.01-50, ....).
     *
     * @param startDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @param endDate optional parameter. If not null, we'll only look at transactions from or after this date
     * @param values optional parameter. An array of length n. Each element is a double, for a numerical value.
     *      Function uses the array as the values to split the pie chart into. For elements 0 - n, we find all
     *      transactions less than or equal to the value. Our return map will have n+1 entries, as we'll also return
     *      the percentage of values greater than the last value.
     * @return a PieChart with percentage of transactions within each range provided in values parameter
     */
    public PieData pieChartByCost(Calendar startDate, Calendar endDate, ArrayList<Double> values){
        Map<String, Double> ret = new HashMap<>();

        List<PieEntry> entries = new ArrayList<>();

        ArrayList<Transaction> currentTransactionsList = getTransactionsList(startDate, endDate);
        if(values == null){
            values = new ArrayList<>();
            values.add(10.00);
            values.add(50.00);
            values.add(100.00);
            values.add(500.00);
            values.add(1000.00);
        }
        Collections.sort(values);
        double currCategoryEntries=0;
        double prevValue = 0;
        //System.out.println(currentTransactionsList.size());
        for(Double currValue : values){
            currCategoryEntries = 0;
            for(Transaction t: currentTransactionsList) {
                //System.out.println("prev: " + prevValue);
                //System.out.println("curr: " + currValue);
                if (t.getAmount() <= currValue && t.getAmount() > prevValue) currCategoryEntries++;
            }
            prevValue = currValue;
            //System.out.println(currCategoryEntries/currentTransactionsList.size());
            //ret.put("<= " + currValue.toString(), (double) (currCategoryEntries/currentTransactionsList.size()) );
            entries.add(new PieEntry((float) (currCategoryEntries/currentTransactionsList.size()), "<= " + currValue.toString()));
        }
        //then the last value
        currCategoryEntries = 0;
        for(Transaction t: currentTransactionsList) {
            //System.out.println(prevValue);
            if (t.getAmount() > prevValue) currCategoryEntries++;
        }
        //System.out.println(currCategoryEntries/currentTransactionsList.size());
        //ret.put("> "+prevValue, (double) (currCategoryEntries/currentTransactionsList.size()) );
        entries.add(new PieEntry((float) (currCategoryEntries/currentTransactionsList.size()), "> "+prevValue));

        PieDataSet set = new PieDataSet(entries, "");//don't think this label does anything

        //but I can do colors here - will have to see how many entries we have first
        set.setColors(Color.rgb(0,0,255), Color.rgb(0,255,0), Color.rgb(255,0,0), Color.rgb(255,255,0), Color.rgb(255,0,255));

        set.setValueTextSize(16f);

        PieData data = new PieData(set);
        data.setValueTextSize(16f);

        //unfortunately there's a few things that need to be done on front end:
        /*
        com.github.mikephil.charting.charts.PieChart pieChart = findViewById(R.id.chart);//get from XML
        pieChart.setData(data);//populate XML with this data
        pieChart.setUsePercentValues(true);//use percentages
        pieChart.setCenterText("Percent of Y-Values in Predefined Ranges");//set text - this does nothing when in the DataSet
        pieChart.getDescription().setEnabled(false);//remove description from corner of screen
         */


        return data;
    }

    //Pending members cannot call any Budget functions
    //Users not in the group cannot call any Budget functions


}
