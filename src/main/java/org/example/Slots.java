package org.example;

/*
SLOTS: Array of runes
Rune: individual runes with id, key, name, desc, etc
 */

public class Slots {

    public Rune[] runes;

    public Slots(Rune[] a) {
        runes = a;
    }



}

/*
inner tier:
            "id": 8112,
            "key": "Electrocute",
            "icon": "perk-images/Styles/Domination/Electrocute/Electrocute.png",
            "name": "Electrocute",
            "shortDesc": "Hitting a champion with 3 <b>separate</b> attacks or abilities in 3s deals bonus <lol-uikit-tooltipped-keyword key='LinkTooltip_Description_AdaptiveDmg'>adaptive damage</lol-uikit-tooltipped-keyword>.",
            "longDesc": "Hitting a champion with 3 <b>separate</b> attacks or abilities within 3s deals bonus <lol-uikit-tooltipped-keyword key='LinkTooltip_Description_AdaptiveDmg'><font color='#48C4B7'>adaptive damage</font></lol-uikit-tooltipped-keyword>.<br><br>Damage: 30 - 180 (+0.4 bonus AD, +0.25 AP) damage.<br><br>Cooldown: 25 - 20s<br><br><hr><i>'We called them the Thunderlords, for to speak of their lightning was to invite disaster.'</i>"

 */
class Rune {

    public String id;
    public String key;
    public String icon;
    public String name;
    public String shortDesc;
    public String longDesc;

    public Rune(String a, String b, String c, String d, String e, String f) {
        id = a;
        key = b;
        icon = c;
        name = d;
        shortDesc = e;
        longDesc = f;
    }

}
