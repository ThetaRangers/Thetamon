package it.thetarangers.thetamon.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "Pokemon")
public class Pokemon {
    @NonNull
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

    public void setIdFromUrl(){
        String temp = this.url;
        temp = temp.substring("https://pokeapi.co/api/v2/pokemon/".length());
        temp = temp.substring(0, temp.length() - 1);

        this.id = Integer.parseInt(temp);
    }

    public void setType(PokemonType type){
        this.type1 = type.name();
    }

    public void setType(PokemonType type1, PokemonType type2){
        this.type1 = type1.name();
        this.type2 = type2.name();
    }

    public void setAverageColor(String averageColor){
        this.averageColor = averageColor;
    }
}
