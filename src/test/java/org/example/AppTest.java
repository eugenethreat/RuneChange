package org.example;

import static org.junit.Assert.assertTrue;

import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Ignore
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Ignore
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

        for (Map.Entry<String, JsonElement> ele : champSet) {
            //ele = each entry, which is strucutred as a key/value of
            JsonObject vals = (JsonObject) ele.getValue();
            System.out.println(vals.get("name") + " " + vals.get("key"));
            //getting key/vals of name + key returned by client

            keyChampNamePairs.put(vals.get("key").toString(), vals.get("name").toString());
            //adding...
        }


    }

    @Test
    public void scrapeRunes() {
        /*
        urls structured like this:
        https://na.op.gg/champion/poppy/statistics/top

        if you remove role, defaults to most common one: ie -
        https://na.op.gg/champion/poppy/statistics > top runes appear first
         */

        //really just excellent code in here
        try {
            ArrayList<String> runeNames = new ArrayList<>();

            String champ = "aatrox";
            champ = champ.toLowerCase();

            String url = "https://na.op.gg/champion/" + champ + "/statistics/";

            //Document doc = Jsoup.connect("https://na.op.gg/champion/poppy/statistics/top").get();

            Document doc = Jsoup.connect(url).get();


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


    }
}
