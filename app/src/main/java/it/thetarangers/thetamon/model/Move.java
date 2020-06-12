package it.thetarangers.thetamon.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Move")
public class Move {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "Type")
    private String type;

    @Ignore
    private int level;

    @Ignore
    private String learnMethod;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLearnMethod() {
        return learnMethod;
    }

    public void setLearnMethod(String learnMethod) {
        this.learnMethod = learnMethod;
    }
}
