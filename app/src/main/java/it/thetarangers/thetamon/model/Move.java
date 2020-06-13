package it.thetarangers.thetamon.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Move")
public class Move implements Parcelable, Comparable<Move> {
    public static final Creator<Move> CREATOR = new Creator<Move>() {
        @Override
        public Move createFromParcel(Parcel in) {
            return new Move(in);
        }

        @Override
        public Move[] newArray(int size) {
            return new Move[size];
        }
    };
    @PrimaryKey
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "Type")
    private transient String type;
    private Integer accuracy;
    private Integer power;
    private Integer priority;
    private Integer pp;
    private String flavorText;
    private transient String target;
    @ColumnInfo(name = "damage_class")
    private String damageClass;
    private String effect;
    @Ignore
    private int level;
    @Ignore
    private String learnMethod;

    public Move() {
    }

    protected Move(Parcel in) {
        id = in.readInt();
        name = in.readString();
        url = in.readString();
        type = in.readString();
        level = in.readInt();
        learnMethod = in.readString();
    }

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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int setIdFromUrl() {
        String temp = this.url;
        temp = temp.substring("https://pokeapi.co/api/v2/move/".length());
        temp = temp.substring(0, temp.length() - 1);

        //Set id from parsed string
        return this.id = Integer.parseInt(temp);
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

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPp() {
        return pp;
    }

    public void setPp(Integer pp) {
        this.pp = pp;
    }

    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDamageClass() {
        return damageClass;
    }

    public void setDamageClass(String damageClass) {
        this.damageClass = damageClass;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeInt(level);
        dest.writeString(learnMethod);
    }

    @Override
    public int compareTo(Move m) {
        return id - m.id;
    }
}
