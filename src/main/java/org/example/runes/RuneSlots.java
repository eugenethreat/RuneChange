package org.example.runes;

/*
SLOTS: Array of runes
Rune: individual runes with id, key, name, desc, etc
 */

public class RuneSlots {

    public Rune[] runes;

    public RuneSlots(Rune[] a) {
        runes = a;
    }

    public Rune[] getRunes(){
        return runes;
    }

}

