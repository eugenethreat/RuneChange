package org.example.puller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class RunePuller {

    /*
        this class will pull runes from whatever resource (riot, op.gg, etc)
        in order to create preferred rune pages for each champ.
     */

    ArrayList<String> runeNames = new ArrayList<>();


    public RunePuller() {

    }

    public ArrayList<String> returnRunes(String champ) {

           /*
        urls structured like this:
        https://na.op.gg/champion/poppy/statistics/top

        if you remove role, defaults to most common one: ie -
        https://na.op.gg/champion/poppy/statistics > top runes appear first
         */

        //really just excellent code in here
        try {
            champ = champ.toLowerCase();
            champ = champ.replace("\"", "");

            String url = "https://na.op.gg/champion/" + champ + "/statistics/";

            //Document doc = Jsoup.connect("https://na.op.gg/champion/poppy/statistics/top").get();

            Document doc = Jsoup.connect(url).get();
            System.out.println(url);
            System.out.println(champ);


            Elements runes = doc.getElementsByClass("tabItem ChampionKeystoneRune-1");
            //gets the first table of runes

            Element selected = runes.first();

            Elements keystone = selected.getElementsByClass("perk-page__item perk-page__item--keystone perk-page__item--active");

            Element realKeystone = keystone.first().getElementsByClass("perk-page__image").first();
            String[] keyStoneName = realKeystone.html().split(">");
            //System.out.println(keyStoneName[1].split("<")[0]);
            //good regex - rewrite later

            runeNames.add(keyStoneName[1].split("<")[0]);

            Elements a = selected.getElementsByClass("perk-page__item  perk-page__item--active");
            //also need to grab secondary tree

            Element first = a.get(0);
            Element second = a.get(1);
            Element third = a.get(2);

            Elements minorRunes = new Elements(first, second, third);

            for (Element ele : minorRunes) {
                Element realMinor = ele.getElementsByClass("perk-page__image").first();
                String[] minorName = realMinor.html().split(">");
                //System.out.println(keyStoneName[1].split("<")[0]);
                //good regex - rewrite later

                runeNames.add(minorName[1].split("<")[0]);

            }

            System.out.println(runeNames);

            //now to get secondary runes ...
            Elements secondary = selected.getElementsByClass("perk-page__item perk-page__item--active");

            secondary.remove(3);
            secondary.remove(2);

            for (Element ele : secondary) {
                Element realMinor = ele.getElementsByClass("perk-page__image").first();
                String[] minorName = realMinor.html().split(">");
                //System.out.println(keyStoneName[1].split("<")[0]);
                //good regex - rewrite later

                runeNames.add(minorName[1].split("<")[0]);
            }

            System.out.println(runeNames);

            //and now, the miniperk things
            Elements tertiary = selected.getElementsByClass("active tip");

            tertiary.remove(5);
            tertiary.remove(4);
            tertiary.remove(3);

            for (Element ele : tertiary) {
                Elements active = ele.getElementsByClass("active tip");
                String finallyName = active.toString().split("<span>")[1].split("</span>")[0];

                runeNames.add(finallyName);

            }

            System.out.println(runeNames);


        } catch (
                IOException e) {
            e.printStackTrace();
        }


        return runeNames;
    }

}
