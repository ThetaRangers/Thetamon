package it.thetarangers.thetamon.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Pokemon")
public class Pokemon {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "Type1")
    public String type1;

    @ColumnInfo(name = "Type2")
    public String type2;

    @ColumnInfo(name = "averageColor")
    public String averageColor;

    public String url;

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
}
