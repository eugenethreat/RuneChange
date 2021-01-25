package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import generated.LolPerksPerkPageResource;
import org.example.champs.ChampLoader;
import org.example.puller.RunePuller;
import org.example.runes.RuneFamily;
import org.example.runes.RuneSlots;
import view.Mainframe;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.formdev.flatlaf.*;


public class App {

    static ClientApi api = new ClientApi();
    //LCU client object - works like magic

    static HashMap<String, String> runeNamesAndIds = getPerkNames();
    //map for matching ids to strings

    static ChampLoader champLoader = new ChampLoader();
    static HashMap<String, String> champNamesAndKeys = champLoader.getChampMap();

    static RunePuller opggGetter = new RunePuller();

    private static Mainframe viewer;

    public static void main(String[] args) {

        System.out.println("running...");

        FlatLightLaf.install();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

// create UI here...

        viewer = new Mainframe();

        /*
        Adds a listener waiting for an instance of the League Client
        Binds and does a thing
         */
        //waitForClient();


        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                System.out.println("connected");

                waitForClient();


                waitUntilLockin();
                setNewPage();

            }

            @Override
            public void onClientDisconnected() {
                System.out.println("disconnected");
            }
        });


    }

    /*
    Waits for client to launch before trying to connect
     */
    private static void waitForClient() {

//        long p = Runtime.getRuntime().freeMemory();

        //this will be fixed later using the process API
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //wait 10 seconds

    }

    /*
        Gates setting rune pages until champ locked in
     */
    private static void waitUntilLockin() {
        try {

            viewer.getContent().getProgress().setText("waiting for champ select...");

            JsonObject details = null;

            while (details == null) {
                //wait until champ select is entered
                details = getApi().executeGet("/lol-champ-select/v1/session", JsonObject.class);
            }
            //better performance may be to hook into client and wait until...

            /*
            wait till a champ is locked in
             */
            Object champAsInteger = getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);

            while (champAsInteger.toString().equals("0.0")) {
                //nothing
                champAsInteger = getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
        gets champ portrait from datadragon and sets
     */
    private static void getChampPortrait(String champ) {
        String path = ".\\resources\\tiles\\";

        champ = champ.substring(0, 1).toUpperCase() + champ.substring(1);
        champ = champ.replace("\"", "");

        File portrait = new File(path + champ + "_0.jpg");
        System.out.println(portrait);

        ImageIcon champIcon = new ImageIcon(path + champ + "_0.jpg");

        //https://stackoverflow.com/questions/16343098/resize-a-picture-to-fit-a-jlabel
        BufferedImage img = null;
        try {
            img = ImageIO.read(portrait);
            //sets img to champ portrait

        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel here = viewer.getContent().getChampNameLabel();
        Image dimg = img.getScaledInstance(here.getWidth(), here.getHeight(),
                Image.SCALE_SMOOTH);
        ImageIcon asdf = new ImageIcon(dimg);

        viewer.getContent().getChampNameLabel().setIcon(asdf);
    }

    /*
        gets champ name text from "id"
     */
    private static String getCurrentChamp() {
        String champ = "";

        try {
            //System.out.println(getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class));
            //oop baby

            double idBack = (double) getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);
            String strBack = String.valueOf(idBack).split("\\.")[0];
            strBack = "\"" + strBack + "\"";
            //jank regex garbage

            champ = champNamesAndKeys.get(strBack);
            //searches map for what champ was picked

        } catch (IOException e) {
            e.printStackTrace();
        }


        viewer.getContent().getChampNameLabel().setText(champ);

        System.out.println("CHAMP: " + champ);
        return champ;
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

            if (pages[0].isDeletable) {
                //deletes pages that already exist
                //change this to only delete first/active page?
                getApi().executeDelete("/lol-perks/v1/pages");
            }

            viewer.getContent().getProgress().setText("fetching champion...");
            String champ = getCurrentChamp();

            getChampPortrait(champ);

            viewer.getContent().getProgress().setText("fetching runes...");
            LolPerksPerkPageResource newPage = doTheThing(champ);

            viewer.getContent().getProgress().setText("setting runepage...");
            getApi().executePost("/lol-perks/v1/pages/", newPage);

            viewer.getContent().getProgress().setText("all clear!");


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /*
    Gets locked in champ and fetches runes
     */
    private static LolPerksPerkPageResource doTheThing(String champ) {
        ArrayList<String> runenameStrings = opggGetter.returnRunes(champ);

        List<Integer> opRunes = new ArrayList();

        //System.out.println(runeNamesAndIds);

        for (String str : runenameStrings) {
            //System.out.println(str);
            str = str.replace(" ", "");
            //removes spacing

            String res = runeNamesAndIds.get(str);
            //System.out.println(res);
            if (res != null) {
                opRunes.add(Integer.parseInt(res));
                //if its not a tertiary
            }
        }

        opRunes.add(5005);
        opRunes.add(5008);
        opRunes.add(5002);

        LolPerksPerkPageResource newPage = new LolPerksPerkPageResource();

        //List<Integer> poppyRunes = Arrays.asList(8437, 8446, 8429, 8451, 8345, 8313, 5005, 5008, 5002);
        //grasp, demolish,
        //perfect timing
        //resolve > inspiration
        /*
        5005, > 10% atck speed
        5008, > +9 adaptive force
        5002  > +6 armor
         */

        newPage.name = "Runes: " + champ;

        viewer.getContent().getListOfRunes().setText(runenameStrings.toString());

        //GOTTA GET THESE SOMEHOW
        /*
            precision: 8000
            domination: 8100
            sorcery: 8200
            inspiration: 8300
            resolve: 8400
         */

        //primary id
        String arbFirst = String.valueOf(opRunes.get(0));

        if (arbFirst.matches("81[0-9][0-9]")) {
            newPage.primaryStyleId = 8100;
        } else if (arbFirst.matches("82[0-9][0-9]")) {
            newPage.primaryStyleId = 8200;
        } else if (arbFirst.matches("83[0-9][0-9]")) {
            newPage.primaryStyleId = 8300;
        } else if (arbFirst.matches("84[0-9][0-9]")) {
            newPage.primaryStyleId = 8400;
        } else if (arbFirst.matches("80[0-9][0-9]")) {
            newPage.primaryStyleId = 8000;
        }

        String arbSecond = String.valueOf(opRunes.get(4));
        //first secondary rune
        if (arbSecond.matches("81[0-9][0-9]")) {
            newPage.subStyleId = 8100;
        } else if (arbSecond.matches("82[0-9][0-9]")) {
            newPage.subStyleId = 8200;
        } else if (arbSecond.matches("83[0-9][0-9]")) {
            newPage.subStyleId = 8300;
        } else if (arbSecond.matches("84[0-9][0-9]")) {
            newPage.subStyleId = 8400;
        } else if (arbSecond.matches("80[0-9][0-9]")) {
            newPage.subStyleId = 8000;
        }

        System.out.println(opRunes);
        //soraka works?
        //skarner works

        newPage.selectedPerkIds = opRunes;

        return newPage;


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
            RuneSlots[] s = rune.getSlots();

            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].runes.length; j++) {
                    String aName = s[i].runes[j].name;
                    aName = aName.replace("\'", "");
                    //removes ' for future's market
                    String anId = s[i].runes[j].id;
                    runeNamesAndIds.put(aName, anId);
                }
            }

        }

        return runeNamesAndIds;
    }

    public static ClientApi getApi() {
        return api;
    }

}
