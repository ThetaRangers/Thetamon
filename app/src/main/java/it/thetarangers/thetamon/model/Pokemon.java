package it.thetarangers.thetamon.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity(tableName = "Pokemon")
public class Pokemon implements Parcelable {

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
    private String moveArray;
    private String abilityArray;

    @ColumnInfo(defaultValue = "false")
    private boolean isFavorite;

    private String urlEvolutionChain;

    private String evolutionChain;

    @Ignore
    private List<Move> movesList;

    @Ignore
    private List<Ability> abilityList;

    @Ignore
    private HashMap<String, String> sprites;

    public Pokemon(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Pokemon(String name) {
        this.name = name;
    }

    protected Pokemon(Parcel in) {
        id = in.readInt();
        name = in.readString();
        type1 = in.readString();
        type2 = in.readString();
        averageColor = in.readString();
        url = in.readString();
        flavorText = in.readString();
        genderRate = in.readInt();
        captureRate = in.readInt();
        growthRate = in.readString();
        height = in.readInt();
        weight = in.readInt();
        hp = in.readInt();
        attack = in.readInt();
        defense = in.readInt();
        specialAttack = in.readInt();
        specialDefense = in.readInt();
        speed = in.readInt();
        habitat = in.readString();
        urlEvolutionChain = in.readString();
        movesList = in.createTypedArrayList(Move.CREATOR);
        abilityList = in.createTypedArrayList(Ability.CREATOR);
        isFavorite = in.readInt() != 0;
    }

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

    public Pokemon() {

    }

    private void encodeMoves() {
        List<JSONObject> moveArray = new ArrayList<>();

        Gson gson = new Gson();
        for(int i = 0; i < this.movesList.size(); i++) {
            String move = gson.toJson(this.movesList.get(i));
            try {
                moveArray.add(new JSONObject(move));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONArray array = new JSONArray(moveArray);
        this.moveArray = array.toString();
    }

    private void encodeAbilities() {
        List<JSONObject> abilityArray = new ArrayList<>();
        Gson gson = new Gson();

        for(int i = 0; i < this.abilityList.size(); i++) {
            String move = gson.toJson(this.abilityList.get(i));
            try {
                abilityArray.add(new JSONObject(move));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONArray array = new JSONArray(abilityArray);
        this.abilityArray = array.toString();
    }

    public void encode() {
        this.encodeAbilities();
        this.encodeMoves();
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

    public boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
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

    public String getAbilityArray() {
        return abilityArray;
    }

    public void setAbilityArray(String abilityArray) {
        this.abilityArray = abilityArray;
    }

    public List<Move> getMovesList() {
        Gson gson = new Gson();
        if(movesList == null) {
            Type listType = new TypeToken<List<Move>>() {
            }.getType();    //Setting up the type for the conversion

            movesList = gson.fromJson(moveArray, listType);
        }

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
        Gson gson = new Gson();
        if(abilityList == null) {
            Log.d("HALO", "STO RECUPERANDO " + abilityArray);
            Type listType = new TypeToken<List<Ability>>() {
            }.getType();    //Setting up the type for the conversion

            abilityList = gson.fromJson(abilityArray, listType);
        }

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

    public HashMap<String, String> getSprites() {
        return sprites;
    }

    public void setSprites(HashMap<String, String> sprites) {
        this.sprites = sprites;
    }

    public String getUrlEvolutionChain() {
        return urlEvolutionChain;
    }

    public void setUrlEvolutionChain(String urlEvolutionChain) {
        this.urlEvolutionChain = urlEvolutionChain;
    }

    public String getMoveArray() {
        return moveArray;
    }

    public void setMoveArray(String moveArray) {
        this.moveArray = moveArray;
    }

    public String getEvolutionChain() {
        return evolutionChain;
    }

    public void setEvolutionChain(String evolutionChain) {
        this.evolutionChain = evolutionChain;
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
        dest.writeString(flavorText);
        dest.writeInt(genderRate);
        dest.writeInt(captureRate);
        dest.writeString(growthRate);
        dest.writeInt(height);
        dest.writeInt(weight);
        dest.writeInt(hp);
        dest.writeInt(attack);
        dest.writeInt(defense);
        dest.writeInt(specialAttack);
        dest.writeInt(specialDefense);
        dest.writeInt(speed);
        dest.writeString(habitat);
        dest.writeString(urlEvolutionChain);
        dest.writeTypedList(movesList);
        dest.writeTypedList(abilityList);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    public void setAll(Pokemon pokemon) {
        this.id = pokemon.getId();
        this.abilityArray = pokemon.getAbilityArray();
        this.abilityList = pokemon.getAbilityList();
        this.attack = pokemon.getAttack();
        this.averageColor = pokemon.getAverageColor();
        this.captureRate = pokemon.getCaptureRate();
        this.defense = pokemon.getDefense();
        this.specialDefense = pokemon.getSpecialDefense();
        this.evolutionChain = pokemon.getEvolutionChain();
        this.flavorText = pokemon.getFlavorText();
        this.genderRate = pokemon.getGenderRate();
        this.growthRate = pokemon.getGrowthRate();
        this.habitat = pokemon.getHabitat();
        this.height = pokemon.getHeight();
        this.hp = pokemon.getHp();
        this.isFavorite = pokemon.getFavorite();
        this.moveArray = pokemon.getMoveArray();
        this.movesList = pokemon.getMovesList();
        this.name = pokemon.getName();
        this.specialAttack = pokemon.getSpecialAttack();
        this.speed = pokemon.getSpeed();
        this.sprites = pokemon.getSprites();
        this.type1 = pokemon.getType1();
        this.type2 = pokemon.getType2();
        this.url = pokemon.getUrl();
        this.urlEvolutionChain = pokemon.getUrlEvolutionChain();
        this.weight = pokemon.getWeight();
    }

    public EvolutionDetail getEvolutionDetail() {
        return new Gson().fromJson(this.getEvolutionChain(), EvolutionDetail.class);
    }

    /*
    @Entity(primaryKeys = {"pokemonId", "moveId"})
    class PokemonCrossMoves {
        public int pokemonId;
        public int moveId;
    }

    public class PokemonWithMoves {
        @Embedded public Pokemon pokemon;
        @Relation(
                parentColumn = "pokemonId",
                entityColumn = "moveId",
                associateBy = @Junction(PokemonCrossMoves.class)
        )
        public List<Move> moves;
    }
     */
}
