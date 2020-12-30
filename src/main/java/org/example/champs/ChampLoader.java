package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ChampLoader {


    /*
        has the list of champ keys/ids
         */
    HashMap<String, String> champMap = loadChamps();

    public ChampLoader() {
        loadChamps();
    }

    private HashMap<String, String> loadChamps() {
        System.out.println("loading champs!");
        Gson gson = new Gson();

        String all = "";
        //empty string to add to later

        File runesReforged = new File(".\\resources\\champion.json");
        try {
            Scanner reader = new Scanner(runesReforged);
            while (reader.hasNext()) {
                String current = reader.next();
                all = all + current;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonObject big = gson.fromJson(all, JsonObject.class);
        JsonObject champsArray = big.getAsJsonObject("data");

        Set<Map.Entry<String, JsonElement>> champSet = champsArray.entrySet();
        HashMap<String, String> keyChampNamePairs = new HashMap<String, String>();

        for (Map.Entry<String, JsonElement> ele : champSet) {
            //ele = each entry, which is strucutred as a key/value of
            JsonObject vals = (JsonObject) ele.getValue();
            //System.out.println(vals.get("name") + " " + vals.get("key"));
            //getting key/vals of name + key returned by client

            keyChampNamePairs.put(vals.get("key").toString(), vals.get("name").toString());
            //adding...
        }
        
        return keyChampNamePairs;

    }

    public HashMap<String, String> getChampMap() {
        return champMap;
    }


}
