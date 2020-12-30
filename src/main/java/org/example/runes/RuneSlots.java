package org.example.runes;

/*
SLOTS: Array of runes
Rune: individual runes with id, key, name, desc, etc
 */

public class Slots {

    public Rune[] runes;

    public Slots(Rune[] a) {
        runes = a;
    }

    public Rune[] getRunes(){
        return runes;
    }

}

