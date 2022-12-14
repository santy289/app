package com.rootnetapp.rootnetintranet.models.createworkflow;

public class ListFieldItemMeta {
    public int id;
    public String name;
    public int listId;

    public ListFieldItemMeta() {
        this.id = 0;
        this.name = name;
        this.listId = 0;
    }

    public ListFieldItemMeta(int id, String name, int listId) {
        this.id = id;
        this.name = name;
        this.listId = listId;
    }

    public ListFieldItemMeta(int id, String name) {
        this(id, name, 0);
    }
}
