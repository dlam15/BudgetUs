package com.example.budgetus;
import java.util.*; 
import java.io.*;

public class User
{
	private String name;
	private String email;
	private String school;
	private String username;
	private String password;
	private Map<Integer, String> groups=  new HashMap<>();;


	public User(String name, String email, String school, String username, String password)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		this.username = username;
		this.password = password;

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

	public String getPassword(){
		return this.password;
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

	public void forgotPassword() {

	}
}
