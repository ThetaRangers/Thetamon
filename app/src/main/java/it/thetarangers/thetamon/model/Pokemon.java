package it.thetarangers.thetamon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "Pokemon")
public class Pokemon implements Parcelable {

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

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
    @ColumnInfo(name = "flavorText")
    private String flavorText;
    @ColumnInfo(name = "genderRate")
    private int genderRate;
    @ColumnInfo(name = "captureRate")
    private int captureRate;
    @ColumnInfo(name = "growthRate")
    private String growthRate;
    @ColumnInfo(name = "height")
    private int height;
    @ColumnInfo(name = "weight")
    private int weight;
    @ColumnInfo(name = "hp")
    private int hp;
    @ColumnInfo(name = "attack")
    private int attack;
    @ColumnInfo(name = "defense")
    private int defense;
    @ColumnInfo(name = "specialAttack")
    private int specialAttack;
    @ColumnInfo(name = "specialDefense")
    private int specialDefense;
    @ColumnInfo(name = "speed")
    private int speed;
    @ColumnInfo(name = "habitat")
    private String habitat;

    //TODO foreign key
    @Ignore
    private List<Move> movesList;

    //TODO foreign key
    @Ignore
    private List<Ability> abilityList;

    public Pokemon(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    protected Pokemon(Parcel in) {
        //TODO fix parcelable
        id = in.readInt();
        name = in.readString();
        type1 = in.readString();
        type2 = in.readString();
        averageColor = in.readString();
        url = in.readString();
    }

    @Ignore
    public Pokemon(String name) {
        this.name = name;
    }

    public int setIdFromUrl() {
        //Parse URL to get id
        String temp = this.url;
        temp = temp.substring("https://pokeapi.co/api/v2/pokemon/".length());
        temp = temp.substring(0, temp.length() - 1);

        //Set id from parsed string
        return this.id = Integer.parseInt(temp);
    }

    public void setType(String type, int slot) {
        if (slot == 1) {
            this.type1 = type;
        } else {
            this.type2 = type;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setAverageColor(String averageColor) {
        this.averageColor = averageColor;
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

    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public int getGenderRate() {
        return genderRate;
    }

    public void setGenderRate(int genderRate) {
        this.genderRate = genderRate;
    }

    public int getCaptureRate() {
        return captureRate;
    }

    public void setCaptureRate(int captureRate) {
        this.captureRate = captureRate;
    }

    public String getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(String growthRate) {
        this.growthRate = growthRate;
    }

    public List<Move> getMovesList() {
        return movesList;
    }

    public void setMovesList(List<Move> movesList) {
        this.movesList = movesList;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Ability> getAbilityList() {
        return abilityList;
    }

    public void setAbilityList(List<Ability> abilityList) {
        this.abilityList = abilityList;
    }

    public void setStats(int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
    }

    public List<Integer> getStats() {
        List<Integer> statList = new ArrayList<>();

        statList.add(hp);
        statList.add(attack);
        statList.add(defense);
        statList.add(specialAttack);
        statList.add(specialDefense);
        statList.add(speed);

        return statList;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public void setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(type1);
        dest.writeString(type2);
        dest.writeString(averageColor);
        dest.writeString(url);
    }
}
