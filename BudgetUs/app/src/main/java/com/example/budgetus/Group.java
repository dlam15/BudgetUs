package com.example.budgetus;
import com.sun.mail.iap.ByteArray;

import java.util.*;
import java.security.*;

public class Group {
    private String groupName;
    private final long groupID;
    private Map<byte[], String> members = new HashMap<>();

    public Group(String groupName, long groupID){
        this.groupName = groupName;
        this.groupID = groupID;
    }

    public boolean setGroupName(String groupName) {
        this.groupName = groupName;
        return true;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean registerUser(byte[] newUser, String status){
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

    public boolean unregisterUser(byte[] remUser){
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

    public boolean changeStatus(byte[] curUser, String status){
        if(members.isEmpty ()){
            return false;
        }
        else{
            if(members.containsKey (curUser)){
                for (Map.Entry<byte[], String> tmp : members.entrySet()){
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
