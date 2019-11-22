package com.example.reciplease;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DatabaseAccessObject {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void addRecipe(Recipe r);

    @Query("SELECT * FROM Recipe")
    Recipe[] getRecipes();

    @Update
    void updateRecipe(Recipe r);

    @Delete
    void removeRecipe(Recipe r);

}