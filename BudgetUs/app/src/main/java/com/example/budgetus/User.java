package com.example.budgetus;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.io.*;

public class User
{
	private String name;
	private String email;
	private String school;
	//private String username;
	private String password;
	private String randomID;
	//private Map<Integer, String> groups=  new HashMap<>();
	private Map<String, String> groups=  new HashMap<>(); //The databaase only accepts maps with Strings as the key
	private FirebaseAuth firebaseAuth;
	private DatabaseReference databaseReference;

	//Needed for some part of the firebase
	public User(){

	}

	public User(String name, String email, String school, String password, String randomID)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		//this.username = username;
		this.password = password;
		this.randomID = randomID;
		firebaseAuth = FirebaseAuth.getInstance();

  }
	//Setters which also update the database
	public boolean updateName(String newName){
		boolean result = updateDatabase("name", newName);
		this.name = newName;
		return result;
	}

	public boolean updateEmail(String newEmail){
		boolean result = updateDatabase("email", newEmail);
		firebaseAuth.getCurrentUser().updateEmail(newEmail); //Used to update the user's account
		this.email = newEmail;
		return result;
	}

	public boolean updatePassword(String newPassword){
		boolean result = updateDatabase("password", newPassword);
		firebaseAuth.getCurrentUser().updatePassword(newPassword); //Used to update the user's account
		this.password = newPassword;
		return result;
	}

	public boolean updateSchool(String newSchool){
		boolean result = updateDatabase("school", newSchool);
		this.school = newSchool;
		return result;
	}

	public boolean updateStatus(int groupID, String status){
		if(groups.containsKey(groupID)){
			for (Map.Entry<String, String> tmp : groups.entrySet()){
				if(tmp.getKey().equals(groupID)){
					tmp.setValue(status);
					return true;
				}
			}
		}
		return false;
	}

	public void setRandomID(String id){
		this.randomID = id;
	}

	//Function to update the database
	public boolean updateDatabase(String type, Object change){
		databaseReference = FirebaseDatabase.getInstance().getReference("users/" + randomID + "/" + type);
		databaseReference.setValue(change);
		return true;
	}



	//Getters
	public String getName(){
		return this.name;
	}

	public String getEmail(){
		return this.email;
	}

	public String getSchool(){
		return this.school;
	}

	//public String getUsername(){
	//	return this.username;
	//}

	public String getPassword(){
		return this.password;
	}

	public String getRandomID() {
		return this.randomID;
	}

	/*public void setUsername(String username) {
		this.username = username;
	}*/

	//May need to change based on how we want to store the groups
	/*public void setGroups(Map <String, String> groups) {
		this.groups = groups;
	}

	public Map <String, String> getGroups() {
		return groups;
	}*/



	public String getStatus(int groupID){
		if(groups.containsKey(groupID)){
			for (Map.Entry<String, String> tmp : groups.entrySet()){
				if(tmp.getKey().equals(groupID)){
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
