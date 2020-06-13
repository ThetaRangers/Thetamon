package it.thetarangers.thetamon.database;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.thetarangers.thetamon.model.Move;

@Dao
public abstract class MoveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<Move> moves);

    @Query("DELETE FROM Move")
    public abstract void deleteAll();

    @Query("SELECT * FROM MOVE WHERE name LIKE :name")
    abstract List<Move> getMovesByNameInterface(String name);

    @Query("SELECT Type FROM MOVE WHERE name == :moveName")
    public abstract String getMoveType(String moveName);

    @Query("SELECT Type, damage_class FROM Move WHERE name == :moveName")
    public abstract MoveDetail getMoveDetails(String moveName);

    public List<Move> getMoveByName(String name) {
        return getMovesByNameInterface(name + "%");
    }

    static class MoveDetail { // TODO put this somewhere else?
        @ColumnInfo(name = "Type")
        public String type;
        @ColumnInfo(name = "damage_class")
        public String damageClass;
    }


}
