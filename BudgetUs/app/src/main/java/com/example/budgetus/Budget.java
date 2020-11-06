package com.example.budgetus;

import java.util.ArrayList;

public class Budget {

    private ArrayList<Transaction> listOfTransactions = new ArrayList<>();


    /* I have a few options here- transactions have quite a few optional fields. Do we
     *  -have multiple versions of this function?
     *  -have multiple constructors in Transaction?
     *  -check for null and use this to not set values?
     *
     * In any case, we'll add a transaction to the arraylist. We'll also make sure to update any running totals.
     */
    public void addTransaction(){
        Transaction t1  = new Transaction(1, "to matt", null, null, null, null);

        //note - category tag does work, but adding it is a bit hard to find -
        //so, when we call .withCategory, we'll always pass a Category enum, but addTag is optional
        //but it isn't obvious that this is an option

        System.out.println(t1.getAmount());
        System.out.println(t1.getName());
        System.out.println(t1.getDateOfTransaction());
        System.out.println(t1.getDescription());
        System.out.println(t1.getCategory());
        System.out.println(t1.getCategory().getTag());

        t1.setDateOfTransaction(2020, 05, 1, -1, -1);
    }

}
