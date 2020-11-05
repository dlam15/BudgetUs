package com.example.budgetus;

import java.util.ArrayList;

public class Budget {

    private ArrayList<Transaction> listOfTransactions = new ArrayList<>();


    /* I have a few options here- transactions have quite a few optional fields. Do we
     *  -have multiple versions of this function?
     *  -have multiple constructors in Transaction?
     *  -check for nullptr and use this to not set values?
     * Right now, I like the idea of builders. We have 2 values that are required, and everything else is optional.
     * We add whatever we want while building. However, it can be a bit confusing at first when making a new object,
     * so I'll have to document how to use it.
     *
     * In any case, we'll add a transaction to the arraylist. We'll also make sure to update any running totals.
     */
    public void addTransaction(){
        Transaction t1 = Transaction.newBuilder(20, "pizza")
                .withDescription("gim pizza from nirchis")
                .withCategory(Transaction.Category.FOOD.addTag("nirchi's pizza"))
                .build();

        //note - category tag does work, but adding it is a bit hard to find -
        //so, when we call .withCategory, we'll always pass a Category enum, but addTag is optional
        //but it isn't obvious that this is an option
        //probably will change, but try to avoid nested builders

        System.out.println(t1.getAmount());
        System.out.println(t1.getName());
        System.out.println(t1.getDateOfTransaction());
        System.out.println(t1.getDescription());
        System.out.println(t1.getCategory());
        System.out.println(t1.getCategory().getTag());
    }

}
