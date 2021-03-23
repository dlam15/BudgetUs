package com.example.budgetus;

import java.util.*;

public class Group {
    private String groupName;
    private String groupID;
    private Map <String, Role> members = new HashMap <String, Role> ();
    private Budget groupBudget;

    public Group(){

    }

    public Group(String groupName, String groupID){
        this.groupName = groupName;
        this.groupID = groupID;
        this.groupBudget = new Budget ();
    }

    public boolean setGroupName(String groupName) {
        this.groupName = groupName;
        return true;
    }

    public Map <String, Role> getMembers() {
        return members;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public Budget getGroupBudget() {
        return groupBudget;
    }

    public boolean registerUser(String newUser, Role status){
        if(members.isEmpty ()){
            members.put(newUser, status);
            return true;
        }
        else{
            if(!(members.containsKey (newUser))){
                members.put(newUser, status);
                return true;
            }
        }
        return false;
    }

    public boolean unregisterUser(String remUser){
        if(members.isEmpty ()){
            return false;
        }
        else{
            if(!(members.containsKey (remUser))){
                members.remove(remUser);
                return true;
            }
        }
        return false;
    }

    public boolean changeStatus(String curUser, Role status){
        if(members.isEmpty ()){
            return false;
        }
        else{
            if(members.containsKey (curUser)){
                for (Map.Entry <String, Role> tmp : members.entrySet()){
                    if(tmp.getKey().equals(curUser)){
                        tmp.setValue(status);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
