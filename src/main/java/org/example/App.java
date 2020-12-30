package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;

import java.io.*;
import java.util.*;

import generated.LolPerksPerkPageResource;
import org.example.champs.ChampLoader;
import org.example.puller.RunePuller;
import org.example.runes.RuneFamily;
import org.example.runes.RuneSlots;

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

    static RunePuller opggGetter = new RunePuller();

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

    private static void whenConnected() {
        waitUntilLockin();
        setNewPage();
    }

    /*
        Gates setting rune pages until champ locked in
     */
    private static void waitUntilLockin() {
        try {
            JsonObject details = getApi().executeGet("/lol-champ-select/v1/session", JsonObject.class);

            while (details == null) {
                //wait until champ select is entered
                details = getApi().executeGet("/lol-champ-select/v1/session", JsonObject.class);
            }
            //better performance may be to hook into client and wait until...

            /*
            id: {actions=[[{actorCellId=0.0, championId=266.0, completed=false, id=1.0, isAllyAction=true, isInProgress=true,
            pickTurn=1.0, type=pick}]], allowBattleBoost=false, allowDuplicatePicks=false, allowLockedEvents=false, allowRerolling=false,
            allowSkinSelection=true, bans={myTeamBans=[], numBans=0.0, theirTeamBans=[]}, benchChampionIds=[], benchEnabled=false, boostableSkinCount=1.0, c
            hatDetails={chatRoomName=c1~3c2b59ea33b56d961430c99f762153798f74b940@sec.pvp.net, chatRoomPassword=yeBruCQ6a6DyVmpS}, counter=-1.0,
            entitledFeatureState={additionalRerolls=0.0, unlockedSkinIds=[]}, gameId=0.0, hasSimultaneousBans=false, hasSimultaneousPicks=true,
            isCustomGame=true, isSpectating=false, localPlayerCellId=0.0, lockedEventIndex=-1.0, myTeam=[{assignedPosition=, cellId=0.0, championId=266.0,
            championPickIntent=0.0, entitledFeatureType=, selectedSkinId=266000.0, spell1Id=4.0, spell2Id=12.0, summonerId=5.3750041E7, team=1.0, wardSkinId=-1.0}],
             rerollsRemaining=0.0, skipChampionSelect=false, theirTeam=[], timer={adjustedTimeLeftInPhase=88232.0, internalNowInEpochMs=1.609268840573E12, isInfinite=false,
             phase=BAN_PICK, totalTimeInPhase=92624.0}, trades=[]}
             */

            /*
            wait till a champ is locked in
             */
            Object de2 = getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);

            while (de2.toString().equals("0.0")) {
                //nothing
                de2 = getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);
            }

        } catch (IOException e) {
            e.printStackTrace();
            api.stop();
        }

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

            String champ = getCurrentChamp();
            LolPerksPerkPageResource newPage = doTheThing(champ);

            getApi().executePost("/lol-perks/v1/pages/", newPage);

        } catch (IOException e) {
            e.printStackTrace();
            api.stop();
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
            newPage.primaryStyleId = 8500;
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
            newPage.subStyleId = 8500;
        }

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
