package com.example.reciplease;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.reciplease.R;

import java.util.ArrayList;


public class RecipeListFragment extends Fragment implements View.OnClickListener {

    private Recipe[] recipeList;
    private ArrayList<String> recipes;
    private Recipe selectedRecipe = null;

    public RecipeListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Init();
    }

    private void Init(){

        recipes = new ArrayList<>();
        ListView lvRecipes = this.getView().findViewById(R.id.lv_recipes_list);
        //get all the recipes and put it into this list...
        recipeList = MainActivity.db.dao().getRecipes();
        //foreach recipe in the list add them to the recipe[] array
        for(Recipe r : recipeList){
            recipes.add(r.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), R.layout.meals_list_item, recipes);
        lvRecipes.setAdapter(adapter);

        // Adds a click feature to the Ingredients list so we can remove ingredients
        lvRecipes.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> a, View v, int p, long l) {
                for(Recipe r : recipeList){
                    if (r.toString() == recipes.get(p)){
                        selectedRecipe = r;
                    }
                }
                MainActivity.selectedRecipe = selectedRecipe;
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container,
                        new RecipeViewFragment()).addToBackStack(null).commit();
            }
        });

        Button btnGoBack = this.getView().findViewById(R.id.btn_go_back_recipe_list);
        btnGoBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_go_back_recipe_list:
                MainActivity.fragMan.popBackStack();
                break;
        }
    }

}
