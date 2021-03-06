package org.example.runes;

/*
overall family of runes

Domination
Inspiration
Precision
Resolve
Sorcery

 */

public class RuneFamily {

    private String id;
    private String key;
    private String icon;
    private String name;
    RuneSlots[] slots;

    public RuneFamily(String a, String b, String c, String d, RuneSlots[] e) {
        setId(a);
        setKey(b);
        setIcon(c);
        setName(d);
        slots = e;
    }

    public RuneSlots[] getSlots() {
        return slots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

