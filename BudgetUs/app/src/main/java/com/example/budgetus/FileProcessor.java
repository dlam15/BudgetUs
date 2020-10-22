package com.example.budgetus;

import android.util.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileProcessor {
    private String file = "database.json";
    private FileReader reader;

    public FileProcessor() throws InvalidPathException, SecurityException, FileNotFoundException, IOException {
        reader = new FileReader(file);
    }

    public Map<String, User> getUserMap(){
        Map<String, User> hashmap = new HashMap<String, User>(1000);
        List<User> userList = new ArrayList<User>();

        try {
            JsonReader jFile = new JsonReader(reader);
            userList = findUsers (jFile);
            for (User user:userList){
                String username = user.getUsername();
                hashmap.put(username,user);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hashmap;

    }
    private List<User> findUsers(JsonReader jFile)throws IOException{
        List<User> userList = new ArrayList<>();

        jFile.beginObject();
        while (jFile.hasNext()){
            String users = jFile.nextName();
            if(users.equals("users")){
                jFile.beginArray();
                while(jFile.hasNext()){
                    User temp = createUsers(jFile);
                    userList.add(temp);
                }
                jFile.endArray();
            }else{
                jFile.skipValue();
            }
        }
        jFile.endObject();
        return userList;
    }

    private User createUsers(JsonReader jFile)throws IOException{
        User temp;
        String name = null;
        String email = null;
        String school = null;
        String username = null;
        String password = null;

        jFile.beginObject();
        while(jFile.hasNext()){
            String client = jFile.nextName();
            if(client.equals("name")){
                name = jFile.nextString();
            }else if(client.equals("email")){
                email = jFile.nextString();
            }else if(client.equals("school")){
                school = jFile.nextString();
            }else if(client.equals("username")){
                username = jFile.nextString();
            }else if(client.equals("password")){
                password = jFile.nextString();
            }else{
                jFile.skipValue();
            }
        }
        jFile.endObject();
        temp = new User(name,email,school,username,password);
        return temp;
    }

}