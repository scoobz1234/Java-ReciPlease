package com.example.reciplease;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reciplease.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ShoppingListFragment extends Fragment {

    private ArrayList<Ingredient> ingredients = new ArrayList<>();

    public ShoppingListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CompareIngredients();
    }

    private void CompareIngredients(){

        for(Recipe r : MainActivity.db.dao().getRecipes()){
            for(Ingredient i : r.getIngredients()){
                ingredients.add(i);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean ContainsName(final List<Ingredient> list, final String name){
        return list.stream().map(Ingredient::getIngredientName).filter(name::equals).findFirst().isPresent();
    }
}


/**
 * for each ingredient in ingredients list
 * if ingredient name == last ingredient name
 * set ingredient amount += last ingredients amount
 * if last ingredient != null remove last ingredient from list
 * set last ingredient to current ingredient
 */