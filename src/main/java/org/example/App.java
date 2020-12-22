package org.example;

/**
 * Hello world!
 */

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import generated.*;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import generated.LolPerksPerkPageResource;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



public class App {

    static ClientApi api = new ClientApi();
    //LCU client object - works like magic

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

    /*
    do something when connected to the client
     */
    private static void whenConnected() {
        try {
            LolSummonerSummoner summoner = api.executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class);
            //gets summoner
            try {
                //change pages
                LolPerksPerkPageResource page1 =
                        getApi().executeGet("/lol-perks/v1/currentpage", LolPerksPerkPageResource.class);
                if (!page1.isEditable || !page1.isActive) {
                    //get all rune pages
                    LolPerksPerkPageResource[] pages =
                            getApi().executeGet("/lol-perks/v1/pages", LolPerksPerkPageResource[].class);
                    //find available pages
                    List<LolPerksPerkPageResource> availablePages = Arrays.stream(pages).filter(p -> p.isEditable).collect(Collectors.toList());
                    if (availablePages.size() > 0) {

                        page1 = availablePages.get(0);
                        //rune pages are accessible!

                        for(Integer name : page1.selectedPerkIds){
                            System.out.println((name));
                        }

                        System.out.println(page1.name);
                    } else {
                        page1 = new LolPerksPerkPageResource();
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
