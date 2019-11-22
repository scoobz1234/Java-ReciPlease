package com.example.reciplease;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ingredient implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    //private int recipeID;
    private float amount;
    private String ingredientName;
    private String measurement;

    // GETTERS //
    //public int getRecipeID() {return recipeID;}
    public int getId() {return id;}
    public float getAmount() {return amount;}
    public String getIngredientName() {return ingredientName;}
    public String getMeasurement() {return measurement;}

    // SETTERS //
   // public void setRecipeID(int recipeID){this.recipeID = recipeID;}
    public void setId(int id) {this.id = id;}
    public void setAmount(float amount) {this.amount = amount;}
    public void setIngredientName(String ingredientName) {this.ingredientName = ingredientName;}
    public void setMeasurement(String measurement) {this.measurement = measurement;}

    // CONSTRUCTORS //
    public Ingredient(){}
    public Ingredient(float amount, String name, Measurements measurement){
        //this.recipeID = rID;
        this.amount = amount;
        this.ingredientName = name;
        this.measurement = measurement.toString();
    }

    // METHODS //
    @NonNull
    @Override
    public String toString() {
        return amount + " " + measurement + " of " + ingredientName;
    }
}

// OTHER //
enum Measurements
{
    NA, Boxes, Cans, Cups,
    Dash, Packets, Pinch,
    Pounds, Quarts, Whole,
    Ounces, Teaspoons, Tablespoons
}