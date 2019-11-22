package com.example.reciplease;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class DataConverter {

    @TypeConverter
    public String fromIngredientList(List<Ingredient> ingredients){
        if (ingredients == null){
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Ingredient>>() {}.getType();
        String json = gson.toJson(ingredients, type);
        return json;
    }

    @TypeConverter
    public List<Ingredient> toIngredientList ( String ingredientsString){
        if (ingredientsString == null){
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Ingredient>>() {}.getType();
        List<Ingredient> ingredientsList = gson.fromJson(ingredientsString, type);
        return ingredientsList;
    }
}
