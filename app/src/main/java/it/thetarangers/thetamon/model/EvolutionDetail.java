package it.thetarangers.thetamon.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

import it.thetarangers.thetamon.R;
import it.thetarangers.thetamon.utilities.StringManager;

public class EvolutionDetail {
    private Integer gender;
    private String held_item;
    private String item;
    private String known_move;
    private String known_move_type;
    private String locationName;
    private Integer min_affection;
    private Integer min_beauty;
    private Integer min_happiness;
    private Integer min_level;
    private Boolean needs_overworld_rain;
    private String party_species;
    private String party_type;
    private Integer relative_physical_stats;
    private String time_of_day;
    private String trade_species;
    private String trigger;
    private Boolean turn_upside_down;

    private List<EvolutionDetail> nextPokemon;
    private String name;

    private String evolutionCondition = "";

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getHeld_item() {
        return held_item;
    }

    public void setHeld_item(String held_item) {
        this.held_item = held_item;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getKnown_move() {
        return known_move;
    }

    public void setKnown_move(String known_move) {
        this.known_move = known_move;
    }

    public String getKnown_move_type() {
        return known_move_type;
    }

    public void setKnown_move_type(String known_move_type) {
        this.known_move_type = known_move_type;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getMin_affection() {
        return min_affection;
    }

    public void setMin_affection(Integer min_affection) {
        this.min_affection = min_affection;
    }

    public Integer getMin_beauty() {
        return min_beauty;
    }

    public void setMin_beauty(Integer min_beauty) {
        this.min_beauty = min_beauty;
    }

    public Integer getMin_happiness() {
        return min_happiness;
    }

    public void setMin_happiness(Integer min_happiness) {
        this.min_happiness = min_happiness;
    }

    public Integer getMin_level() {
        return min_level;
    }

    public void setMin_level(Integer min_level) {
        this.min_level = min_level;
    }

    public Boolean getNeeds_overworld_rain() {
        return needs_overworld_rain;
    }

    public void setNeeds_overworld_rain(Boolean needs_overworld_rain) {
        this.needs_overworld_rain = needs_overworld_rain;
    }

    public String getParty_species() {
        return party_species;
    }

    public void setParty_species(String party_species) {
        this.party_species = party_species;
    }

    public String getParty_type() {
        return party_type;
    }

    public void setParty_type(String party_type) {
        this.party_type = party_type;
    }

    public Integer getRelative_physical_stats() {
        return relative_physical_stats;
    }

    public void setRelative_physical_stats(Integer relative_physical_stats) {
        this.relative_physical_stats = relative_physical_stats;
    }

    public String getTime_of_day() {
        return time_of_day;
    }

    public void setTime_of_day(String time_of_day) {
        this.time_of_day = time_of_day;
    }

    public String getTrade_species() {
        return trade_species;
    }

    public void setTrade_species(String trade_species) {
        this.trade_species = trade_species;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public Boolean getTurn_upside_down() {
        return turn_upside_down;
    }

    public void setTurn_upside_down(Boolean turn_upside_down) {
        this.turn_upside_down = turn_upside_down;
    }

    public List<EvolutionDetail> getNextPokemon() {
        return nextPokemon;
    }

    public void setNextPokemon(List<EvolutionDetail> nextPokemon) {
        this.nextPokemon = nextPokemon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addCondition(String condition) {
        this.evolutionCondition += ", " + condition;
    }

    public String getTriggerFormat(Context context) {
        String result = "";
        switch (this.getTrigger()) {
            case "level-up":
                result += context.getResources().getString(R.string.trigger_level_up);
                break;
            default:
                break;
        }
        return result;
    }

    public String getEvolutionMethod(Context context) {
        String result = "";

        if (this.getTrigger() != null) {
            result = this.getTriggerFormat(context);
            result += this.evolutionCondition;
        }
        return result;
    }

    @Override
    public String toString() {
        return "EvolutionDetail{" +
                "gender=" + gender +
                ", held_item='" + held_item + '\'' +
                ", item='" + item + '\'' +
                ", known_move='" + known_move + '\'' +
                ", known_move_type='" + known_move_type + '\'' +
                ", locationName='" + locationName + '\'' +
                ", min_affection=" + min_affection +
                ", min_beauty=" + min_beauty +
                ", min_happiness=" + min_happiness +
                ", min_level=" + min_level +
                ", needs_overworld_rain=" + needs_overworld_rain +
                ", party_species='" + party_species + '\'' +
                ", party_type='" + party_type + '\'' +
                ", relative_physical_stats=" + relative_physical_stats +
                ", time_of_day='" + time_of_day + '\'' +
                ", trade_species='" + trade_species + '\'' +
                ", trigger='" + trigger + '\'' +
                ", turn_upside_down=" + turn_upside_down +
                ", nextPokemon=" + nextPokemon +
                ", name='" + name + '\'' +
                '}';
    }
}
