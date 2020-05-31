package it.thetarangers.thetamon.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Pokemon")
public class Pokemon {
    @NonNull
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

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
}
