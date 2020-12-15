package com.example.budgetus;
import java.util.*;
import java.security.*;


public class GroupRecord {
    private static Map<Long, Group> groups = new HashMap<>();

    public boolean addGroup(String groupName){
        if(groups.isEmpty ()){
            SecureRandom randID = new SecureRandom();
            long tmpID = randID.nextLong ();
            Group newGroup = new Group(groupName, tmpID);
            groups.put(tmpID, newGroup);
            return true;
        }
        else{
            SecureRandom randID = new SecureRandom();
            long tmpID = randID.nextLong();
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