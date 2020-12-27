package org.example;

import static org.junit.Assert.assertTrue;

import com.google.gson.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void getChampNames() {
        Gson gson = new Gson();
        HashMap<String, String> champNames = new HashMap<>();

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

        for(Map.Entry<String, JsonElement> ele : champSet){
            //ele = each entry, which is strucutred as a key/value of
            JsonObject vals = (JsonObject) ele.getValue();
            System.out.println(vals.get("name") + " " + vals.get("key"));
            //getting key/vals of name + key returned by client

            keyChampNamePairs.put(vals.get("key").toString(), vals.get("name").toString());
            //adding...
        }





    }
}
