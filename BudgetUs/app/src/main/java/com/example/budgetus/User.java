//Class User
import java.util.*; 
import java.io.*;

public class User
{
	String name;
	String email;
	String school;
	String username;
	String password;
	ArrayList<Map.Entry<Integer, String>> groups; 

	public User(String name, String email, String school, String username, String password)
	{
		this.name = name;
		this.email = email;
		this.school = school;
		this.username = username;
		this.password = password;
		groups = new ArrayList<Map.Entry<Integer, String>> ();
	}
	
	public void updateName(Sring name){
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
		for(int i = 0; i < groups.size(); i++){
			Map.Entry<Integer,String> tmp = groups.get(i);

		    if(tmp.getKey() == groupID){
				tmp.setValue(status);
				return true;
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

	public String getStatus(int groupID){
		for(int i = 0; i < groups.size(); i++){
			Map.Entry<Integer,String> tmp = groups.get(i);

		    if(tmp.getKey() == groupID){
				return tmp.getValue(groupID);
			}
		}
		return null;
	}

	public boolean updateGroups(int groupID, String status){
		if(groups.isEmpty()){
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
		return false;
	}

	public void forgotPassword() {

	}
}