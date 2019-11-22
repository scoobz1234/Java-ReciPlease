package com.example.reciplease;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity
public class Recipe implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String recipeName;
    private String recipeType;
    private Boolean selectedMeal;

    @TypeConverters(DataConverter.class)
    private List<Ingredient> ingredients;

    // GETTERS //
    public int getId(){
        return id;
    }
    public String getRecipeName(){return recipeName;}
    public String getRecipeType(){return recipeType;}
    public List<Ingredient> getIngredients(){ return ingredients; }
    public Boolean getSelectedMeal() {return selectedMeal;}

    // SETTERS //
    public void setId(int id) {
        this.id = id;
    }
    public void setRecipeName(String name){this.recipeName = name;}
    public void setRecipeType(String recipeType){this.recipeType = recipeType;}
    public void setIngredients(List<Ingredient> ingredients){this.ingredients = ingredients;}
    public void setSelectedMeal(Boolean selectedMeal) {this.selectedMeal = selectedMeal;}

    // CONSTRUCTORS //
    public Recipe(){}
    public Recipe(String name, Types type, List<Ingredient> ingredients){
        this.recipeName = name;
        this.recipeType = type.toString();
        this.ingredients = ingredients;
        this.selectedMeal = false;
    }

    // METHODS //
    @NonNull
    @Override
    public String toString() {
        return recipeName;
    }
    public String toOtherString(){return recipeName + "," + recipeType;}
}

// OTHER //
enum Types
{
    NA, Beef, Bison, Boar,
    Chicken, Duck, Fish,
    Lamb, Pheasant, Pork,
    Rabbit, Turkey, Vegan,
    Venison, NonMeat, Vegetarian,
    Dessert, Appetizer
}
