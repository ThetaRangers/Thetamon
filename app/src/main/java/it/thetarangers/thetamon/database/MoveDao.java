package it.thetarangers.thetamon.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Move;

@Dao
public abstract class MoveDao {

    @Insert
    public abstract void insertAll(List<Move> moves);

    @Query("DELETE FROM Move")
    public abstract void deleteAll();

    @Query("SELECT * FROM MOVE WHERE name LIKE :name")
    abstract List<Move> getMovesByNameInterface(String name);

    public List<Move> getMoveByName(String name){
        return getMovesByNameInterface(name + "%");
    }

}
