package org.example;

/**
 * Hello world!
 */

import com.stirante.lolclient.ClientApi;
import com.stirante.lolclient.ClientConnectionListener;
import generated.LolSummonerSummoner;

import java.io.IOException;


public class App {
    static ClientApi api = new ClientApi();

    public static void main(String[] args) {

        System.out.println("running...");

        api.addClientConnectionListener(new ClientConnectionListener() {
            @Override
            public void onClientConnected() {
                System.out.println("connected");
                whenConnected();
            }

            @Override
            public void onClientDisconnected() {
                System.out.println("disconnected");
            }
        });


    }

    private static void whenConnected() {
        try {
            LolSummonerSummoner summoner = api.executeGet("/lol-summoner/v1/current-summoner", LolSummonerSummoner.class);
            //gets summoner



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
