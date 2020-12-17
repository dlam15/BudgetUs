package com.example.budgetus;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.security.*;


public class GroupRecord {
    private static Map<Long, Group> groups = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean addGroup(String groupName) throws NoSuchAlgorithmException{
        SecureRandom randID = SecureRandom.getInstanceStrong();
        long tmpID = randID.nextLong ();

        if(groups.isEmpty ()){
            Group newGroup = new Group(groupName, tmpID);
            groups.put(tmpID, newGroup);
            return true;
        }
        else{
            boolean created = false;
            while(!created){
                if(!groups.containsKey(tmpID)){
                    Group newGroup = new Group(groupName, tmpID);
                    groups.put(tmpID, newGroup);
                    created = true;
                }
                else{
                    tmpID = randID.nextLong ();
                }
            }
        }
        return true;
    }

    public boolean removeGroup(long groupID){
        if(groups.isEmpty ()){
            return false;
        }
        else{
            if(groups.containsKey(groupID)){
                groups.remove(groupID);
                return true;
            }
        }
        return false;
    }
}
