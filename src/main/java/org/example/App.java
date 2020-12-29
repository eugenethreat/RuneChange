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
        peekChampSelect();
        setNewPage();
    }

    /*
    Gates app until champ select
     */
    private static void peekChampSelect() {
        try {
            JsonObject details = getApi().executeGet("/lol-champ-select/v1/session", JsonObject.class);

            while (details == null) {
                //wait until champ select is entered
                details = getApi().executeGet("/lol-champ-select/v1/session", JsonObject.class);
            }

            System.out.println(details.toString());
            /*
            what happens when you call this just sitting in the client?
             */

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
            System.out.println(de2.toString());

            while (de2.toString().equals("0.0")) {
                //nothing
                de2 = getApi().executeGet("/lol-champ-select/v1/current-champion", Object.class);
            }

            System.out.println("peeped tom1");


        } catch (IOException e) {
            e.printStackTrace();
            api.stop();
        }

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

            champ = champNamesAndKeys.get(strBack);
            //searches map for what champ was picked

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
