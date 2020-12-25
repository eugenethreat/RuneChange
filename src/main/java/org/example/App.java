package org.example;

import com.google.gson.Gson;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;

import java.io.*;
import java.util.*;

import generated.LolPerksPerkPageResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class App {

    static ClientApi api = new ClientApi();
    //LCU client object - works like magic

    static HashMap<String, String> runeNamesAndIds = getPerkNames();
    //map for matching ids to strings

    static ArrayList<String> currentRunes = new ArrayList<String>();

    public static void main(String[] args) {

        System.out.println("running...");

        /*
        Adds a listener waiting for an instance of the League Client
        Binds and does a thing
         */
        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                System.out.println("connected");
                whenConnected();
                api.stop();

            }

            @Override
            public void onClientDisconnected() {
                System.out.println("disconnected");
            }
        });


    }

    public static ClientApi getApi() {
        return api;
    }

    private static void whenConnected() {
        setNewPage();
    }

    public static void setNewPage() {
        System.out.println("----------------");
        try {
            LolPerksPerkPageResource[] pages = api.executeGet("/lol-perks/v1/pages", LolPerksPerkPageResource[].class);

            /*
            check if pages already exist
            >if pages exist, edit them
            >if they don't make new ones
             */

            if (!pages[0].isDeletable) {
                System.out.println("no user-created pages!");

                LolPerksPerkPageResource newPage = new LolPerksPerkPageResource();
                newPage.name = "NEW TEST PAGE";

                List<Integer> listOfNewPerks = new ArrayList<>();


                listOfNewPerks.add(8112);
                listOfNewPerks.add(8126);
                listOfNewPerks.add(8136);
                listOfNewPerks.add(8106);
                /*
                electrocute
                cheap shot
                zombie ward
                ultimate hunter
                 */

                listOfNewPerks.add(8009);
                listOfNewPerks.add(8014);
                //presence of mind
                //coupedegrace

                listOfNewPerks.add(5005);
                listOfNewPerks.add(5008);
                listOfNewPerks.add(5002);

                newPage.selectedPerkIds = listOfNewPerks;
                newPage.primaryStyleId = 8100;
                newPage.subStyleId = 8000;
                //need to ste primary/substyle ids as well!

                /*
                precision: 8000
                domination: 8100
                sorcery: 8200
                inspiration: 8300
                resolve: 8400
                 */

                System.out.println("valid: " + newPage.isValid);

                getApi().executePost("/lol-perks/v1/pages/", newPage);


            } else {

                LolPerksPerkPageResource page1 = pages[0];

                System.out.println(page1.isEditable);
                System.out.println(page1.id);
                System.out.println(page1.name);

                System.out.println(page1.selectedPerkIds);

                System.out.println("setting new page");

                List<Integer> listOfNewPerks = new ArrayList<>();

                listOfNewPerks.add(8112);
                listOfNewPerks.add(8126);
                listOfNewPerks.add(8136);
                listOfNewPerks.add(8106);

                listOfNewPerks.add(8009);
                listOfNewPerks.add(8014);

                listOfNewPerks.add(5005);
                listOfNewPerks.add(5008);
                listOfNewPerks.add(5002);

                System.out.println(listOfNewPerks);

                LolPerksPerkPageResource newPage = new LolPerksPerkPageResource();
                newPage.id = page1.id;
                newPage.selectedPerkIds = listOfNewPerks;
                newPage.name = "meowww";

                try {
                    if (page1.id != null) {

                        Boolean succ;
                        succ = getApi().executePut("lol-perks/v1/pages/" + page1.id, newPage);
                        System.out.println("worked: " + succ);


                    } else {
                        System.out.println("something else ");
                    }

                    //succ needs to be true
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    Loads rune names and matches into hashmap
     */
    public static HashMap<String, String> getPerkNames() {
        Gson gson = new Gson();
        HashMap<String, String> runeNamesAndIds = new HashMap<>();

        String all = "";
        //empty string to add to later

        File runesReforged = new File(".\\resources\\runesReforged.json");
        try {
            Scanner reader = new Scanner(runesReforged);
            while (reader.hasNext()) {
                String current = reader.next();
                all = all + current;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //Creates RuneFamily array from string via gson
        RuneFamily[] runes = gson.fromJson(all, RuneFamily[].class);

        for (RuneFamily rune : runes) {
            Slots[] s = rune.getSlots();

            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].runes.length; j++) {
                    String aName = s[i].runes[j].name;
                    String anId = s[i].runes[j].id;
                    runeNamesAndIds.put(anId, aName);
                }
            }

        }

        return runeNamesAndIds;
    }

}
