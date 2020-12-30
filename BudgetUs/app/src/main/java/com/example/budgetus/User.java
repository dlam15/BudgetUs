package com.example.budgetus;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;
import java.io.*;
import java.security.*;

public class User implements Serializable
{
	private String name;
	private String email;
	private String school;
	private String username;
	private byte[] password;
	private byte[] salt;//sorry, I forgot about this one - used for security
	private byte[] randomID;
	private Map<Integer, String> groups = new HashMap<>();

	public User(String name, String email, String school, String username, byte[] password, byte[] salt)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		this.username = username;
		this.password = password;
		this.salt = salt;
	}
	
	public boolean updateName(String name){
		this.name = name;
		return true;
	}

	public boolean updateEmail(String email){
		this.email = email;
		return true;
	}

	public boolean updatePassword(byte[] password){
		this.password = password;
		return true;
	}

	//NOTE - updating password requires updating salt, so both setters will be called when password changed
	public boolean updateSalt(byte[] salt){
		this.salt = salt;
		return true;
	}

	public boolean updateSchool(String school){
		this.school = school;
		return true;
	}

	public boolean updateStatus(int groupID, String status){
		if(groups.containsKey(groupID)){
			for (Map.Entry<Integer, String> tmp : groups.entrySet()){
				if(tmp.getKey() == groupID){
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

	public String getUsername(){
		return this.username;
	}

	public byte[] getPassword(){
		return this.password;
	}

	public byte[] getSalt() { return this.salt; }

	public byte[] getRandomID() {
		return randomID;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void setRandomID() throws NoSuchAlgorithmException{
		SecureRandom generatedID = SecureRandom.getInstanceStrong();
		generatedID.nextBytes (randomID);
	}

	public String getStatus(int groupID){
		if(groups.containsKey(groupID)){
			for (Map.Entry<Integer, String> tmp : groups.entrySet()){
				if(tmp.getKey() == groupID){
					return tmp.getValue();
				}
			}
		}
		return null;
	}

	public boolean updateGroups(int groupID, String status){
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