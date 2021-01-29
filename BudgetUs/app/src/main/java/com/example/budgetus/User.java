package com.example.budgetus;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.io.*;
import java.security.*;

public class User
{
	private String name;
	private String email;
	private String school;
	private String password;
	private String randomID;
	private Map<String, String> groups = new HashMap<>();

	public User(){

	}

	public User(String name, String email, String school, String password, String randomID)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		this.password = password;
		this.randomID = randomID;
	}
	
	public boolean updateName(String name){
		this.name = name;
		return true;
	}

	public boolean updateEmail(String email){
		this.email = email;
		return true;
	}

	public boolean updatePassword(String password){
		this.password = password;
		return true;
	}

	public boolean updateSchool(String school){
		this.school = school;
		return true;
	}

	public boolean updateStatus(String groupID, String status){
		if(groups.containsKey(groupID)){
			for (Map.Entry<String, String> tmp : groups.entrySet()){
				if(tmp.getKey().equals( groupID)){
					tmp.setValue(status);
					return true;
				}
			}
		}
		return false;
	}

	public String getName(){
		return this.name;
	}

	public String getEmail(){
		return this.email;
	}

	public String getSchool(){
		return this.school;
	}

	public String getPassword(){
		return this.password;
	}

	public 	String getRandomID() {
		return this.randomID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setGroups(Map <String, String> groups) {
		this.groups = groups;
	}

	public Map <String, String> getGroups() {
		return groups;
	}

	public void setRandomID(String id){
		this.randomID = id;
	}

	public String getStatus(String groupID){
		if(groups.containsKey(groupID)){
			for (Map.Entry<String, String> tmp : groups.entrySet()){
				if(tmp.getKey().equals( groupID)){
					return tmp.getValue();
				}
			}
		}
		return null;
	}

	public boolean updateGroups(String groupID, String status){
		if(groups.isEmpty()){
			groups.put(groupID, status);
			return true;
		}
		else{
			if(!(groups.containsKey(groupID))) {
				groups.put(groupID, status);
				return true;
			}
		}
		return false;
	}
}