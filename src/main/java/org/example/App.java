package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;

import java.io.*;
import java.util.*;

import generated.LolPerksPerkPageResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class App {

    static ClientApi api = new ClientApi();
    //LCU client object - works like magic

    static HashMap<String, String> runeNamesAndIds = getPerkNames();
    //map for matching ids to strings

    static ChampLoader champLoader = new ChampLoader();
    static HashMap<String, String> champNamesAndKeys = champLoader.getChampMap();

    static List<Integer> poppyRunes = Arrays.asList(8437, 8446, 8429, 8451, 8345, 8313, 5005, 5008, 5002);
    //grasp, demolish,
    //perfect timing
    //resolve > inspiration

    static List<Integer> sionRunes = Arrays.asList(8439, 8446, 8429, 8451, 8345, 8313, 5005, 5008, 5002);
    //aftershock

     /*
    precision: 8000
    domination: 8100
    sorcery: 8200
    inspiration: 8300
    resolve: 8400
     */


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

    private static String getCurrentChamp() {
        String champ = "";

        try {
            //System.out.println(getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class));
            //oop baby

            double idBack = (double) getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);
            String strBack = String.valueOf(idBack).split("\\.")[0];
            strBack = "\"" + strBack + "\"";
            //jank regex garbage

            System.out.println(strBack);

            System.out.println(champNamesAndKeys);
            champ = champNamesAndKeys.get(strBack);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("CHAMP: " + champ);
        return champ;
    }

    //how to check when locked in / what champ is picked?


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
                //pages dont exist
                System.out.println("no user-created pages!");

                LolPerksPerkPageResource newPage = new LolPerksPerkPageResource();
                newPage.name = "NEW TEST PAGE";

                List<Integer> listOfNewPerks = new ArrayList<>();

                String champ = getCurrentChamp();

                if (champ.equals("\"Poppy\"")) {

                    System.out.println("poppy!");

                    newPage.name = "POPPY";
                    listOfNewPerks = poppyRunes;
                    newPage.primaryStyleId = 8400;
                    newPage.subStyleId = 8300;

                    newPage.selectedPerkIds = listOfNewPerks;


                } else {
                    System.out.println("not poppy ");

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

                    newPage.primaryStyleId = 8100;
                    newPage.subStyleId = 8000;

                    newPage.selectedPerkIds = listOfNewPerks;

                }
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
                //pages exist

                LolPerksPerkPageResource newPage = new LolPerksPerkPageResource();
                newPage.name = "if sss exist already ...";

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

                //delete old page

                getApi().executePost("/lol-perks/v1/pages/", newPage);


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
