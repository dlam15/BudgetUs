package com.example.budgetus;

//Class User
import java.util.*; 
import java.io.*;

public class User
{
	private String name;
	private String email;
	private String school;
	private String username;
	private String password;
	private Map<Integer, String> groups;

	public User(String name, String email, String school, String username, String password)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		this.username = username;
		this.password = password;
		groups = new HashMap<Integer, String>();
	}
	
	public void updateName(String name){
		this.name = name;
	}

	public void updateEmail(String email){
		this.email = email;
	}

	public void updatePassword(String password){
		this.password = password;
	}

	public void updateSchool(String school){
		this.school = school;
	}

	public boolean updateStatus(int groupID, String status){
		groups.put(groupID,status);

		if(groups.get(groupID)!= status){
			return false;
		}
		return true;

		/*for(int i = 0; i < groups.size(); i++){
			Map.Entry<Integer,String> tmp = groups.get(i);

		    if(tmp.getKey() == groupID){
				tmp.setValue(status);
				return true;
			}
		}
		return false;*/
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
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

	public String getRandomID() {
		return null;
	}


	public String getStatus(int groupID){
		return groups.get(groupID);

		/*for(int i = 0; i < groups.size(); i++){
			Map.Entry<Integer,String> tmp = groups.get(i);

		    if(tmp.getKey() == groupID){
				return tmp.getValue(groupID);
			}
		}
		return null;*/
	}

	public boolean updateGroups(int groupID, String status){
		/*if(groups.isEmpty()){
			Map.Entry<Integer, String> tmp = new Map.Entry<Integer, String>(groupID, status);
			return groups.add(tmp);
		}
		else{
			for(int i = 0; i < groups.size(); i++){
			Map.Entry<Integer,String> tmp = groups.get(i);

		    if(tmp.getKey() == groupID){
				if(tmp.getValue(groupID).equals(status){
					return true;
				}
				else{
					return false;
				}
			}
		}
		return false;*/
		return true;
	}

	public void forgotPassword() {

	}
}