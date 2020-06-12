package it.thetarangers.thetamon.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Move")
public class Move {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "url")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId(){
        return this.id;
    }

    public int setIdFromUrl(){
        String temp = this.url;
        temp = temp.substring("https://pokeapi.co/api/v2/move/".length());
        temp = temp.substring(0, temp.length() - 1);

        //Set id from parsed string
        return this.id = Integer.parseInt(temp);
    }

    public void setId(int id) {
        this.id = id;
    }
}
