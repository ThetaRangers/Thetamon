package it.thetarangers.thetamon.model;

import java.util.ArrayList;
import java.util.List;

public class EvolutionDetail {
    private String gender;
    private Object held_item;
    private Object item;
    private Object known_move;
    private Object knownMoveType;
    private Object locationName;
    private int min_affection;
    private int min_beauty;
    private int min_happiness;
    private int min_level;
    private Boolean needs_overworld_rain;
    private Object party_species;
    private Object party_type;
    private Object relative_physical_stats;
    private String time_of_day;
    private Object trade_species;
    private Object trigger;
    private Boolean turn_upside_down;

    private List<EvolutionDetail> nextPokemon;
    private String name;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Object getHeld_item() {
        return held_item;
    }

    public void setHeld_item(Object held_item) {
        this.held_item = held_item;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public Object getKnown_move() {
        return known_move;
    }

    public void setKnown_move(Object known_move) {
        this.known_move = known_move;
    }

    public Object getKnownMoveType() {
        return knownMoveType;
    }

    public void setKnownMoveType(Object knownMoveType) {
        this.knownMoveType = knownMoveType;
    }

    public Object getLocationName() {
        return locationName;
    }

    public void setLocationName(Object locationName) {
        this.locationName = locationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin_affection() {
        return min_affection;
    }

    public void setMin_affection(int min_affection) {
        this.min_affection = min_affection;
    }

    public int getMin_beauty() {
        return min_beauty;
    }

    public void setMin_beauty(int min_beauty) {
        this.min_beauty = min_beauty;
    }

    public int getMin_happiness() {
        return min_happiness;
    }

    public void setMin_happiness(int min_happiness) {
        this.min_happiness = min_happiness;
    }

    public int getMin_level() {
        return min_level;
    }

    public void setMin_level(int min_level) {
        this.min_level = min_level;
    }

    public Boolean getNeeds_overworld_rain() {
        return needs_overworld_rain;
    }

    public void setNeeds_overworld_rain(Boolean needs_overworld_rain) {
        this.needs_overworld_rain = needs_overworld_rain;
    }

    public Object getParty_species() {
        return party_species;
    }

    public void setParty_species(Object party_species) {
        this.party_species = party_species;
    }

    public Object getParty_type() {
        return party_type;
    }

    public void setParty_type(Object party_type) {
        this.party_type = party_type;
    }

    public Object getRelative_physical_stats() {
        return relative_physical_stats;
    }

    public void setRelative_physical_stats(Object relative_physical_stats) {
        this.relative_physical_stats = relative_physical_stats;
    }

    public String getTime_of_day() {
        return time_of_day;
    }

    public void setTime_of_day(String time_of_day) {
        this.time_of_day = time_of_day;
    }

    public Object getTrade_species() {
        return trade_species;
    }

    public void setTrade_species(Object trade_species) {
        this.trade_species = trade_species;
    }

    public Object getTrigger() {
        return trigger;
    }

    public void setTrigger(Object trigger) {
        this.trigger = trigger;
    }

    public List<EvolutionDetail> getNextPokemon() {
        return nextPokemon;
    }

    public void setNextPokemon(List<EvolutionDetail> nextPokemonList) {
        this.nextPokemon = nextPokemonList;
    }

    public void setNextPokemon(EvolutionDetail nextPokemon){
        this.nextPokemon.add(nextPokemon);
    }

    public Boolean getTurn_upside_down() {
        return turn_upside_down;
    }

    public void setTurn_upside_down(Boolean turn_upside_down) {
        this.turn_upside_down = turn_upside_down;
    }
}
