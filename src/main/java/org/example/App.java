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
        try {
            //change pages
            LolPerksPerkPageResource page1 = getApi().executeGet("/lol-perks/v1/currentpage", LolPerksPerkPageResource.class);
            if (!page1.isEditable || !page1.isActive) {
                //get all rune pages
                LolPerksPerkPageResource[] pages = getApi().executeGet("/lol-perks/v1/pages", LolPerksPerkPageResource[].class);
                //find available pages
                List<LolPerksPerkPageResource> availablePages = Arrays.stream(pages).filter(p -> p.isEditable).collect(Collectors.toList());
                if (availablePages.size() > 0) {

                    page1 = availablePages.get(0);
                    //rune pages are accessible!

                    for (Integer name : page1.selectedPerkIds) {
                        String nameStr = name.toString();
                        currentRunes.add(nameStr);

                        System.out.println(runeNamesAndIds.get(nameStr));
                    }

                } else {
                    page1 = new LolPerksPerkPageResource();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
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
