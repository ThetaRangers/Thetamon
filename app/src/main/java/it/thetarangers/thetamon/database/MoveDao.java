package it.thetarangers.thetamon.database;

import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Move;

public abstract class MoveDao {

    @Query("SELECT * FROM MOVE")
    abstract List<Move> getMovesByNameInterface(String name);

    public List<Move> getMoveByName(String name){
        return getMovesByNameInterface(name + "%");
    }

}
