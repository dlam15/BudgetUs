package com.example.budgetus;
import java.util.*;
import java.security.*;

public class Group {
    private String groupName;
    private final long groupID;
    private Map<Integer, String> members = new HashMap<>();

    public Group(String groupName, long groupID){
        this.groupName = groupName;
        this.groupID = groupID;
    }

    public boolean setGroup_name(String groupName) {
        this.groupName = groupName;
        return true;
    }

    public String getGroup_name() {
        return groupName;
    }

    public boolean registerUser(int newUser, String status){
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

    public boolean unregisterUser(int remUser){
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

    public boolean changeStatus(int curUser, String status){
        if(members.isEmpty ()){
            return false;
        }
        else{
            if(members.containsKey (curUser)){
                for (Map.Entry<Integer, String> tmp : members.entrySet()){
                    if(tmp.getKey() == curUser){
                        tmp.setValue(status);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
