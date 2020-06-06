package it.thetarangers.thetamon.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Pokemon")
public class Pokemon {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "Type1")
    private String type1;

    @ColumnInfo(name = "Type2")
    private String type2;

    @ColumnInfo(name = "averageColor")
    private String averageColor;

    private String url;

    public Pokemon(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Pokemon(String name){
        this.name = name;
    }

    public void setId(int id){
        this.id = id;
    }

    public int setIdFromUrl(){
        //Parse URL to get id
        String temp = this.url;
        temp = temp.substring("https://pokeapi.co/api/v2/pokemon/".length());
        temp = temp.substring(0, temp.length() - 1);

        //Set id from parsed string
        return this.id = Integer.parseInt(temp);
    }

    public void setType(String type, int slot){
        if(slot == 1) {
            this.type1 = type;
        } else {
            this.type2 = type;
        }
    }

    public void setAverageColor(String averageColor){
        this.averageColor = averageColor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAverageColor() {
        return averageColor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }
}
