package com.example.reciplease;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reciplease.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeViewFragment extends Fragment implements View.OnClickListener {

    private Recipe recipe;

    public RecipeViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Init(this.getView());
    }

    private void Init(View v) {

        recipe = MainActivity.selectedRecipe;
        ArrayList<String> ingredientList = new ArrayList<>();

        Button btnShare = v.findViewById(R.id.btn_share_recipe_view);
        btnShare.setOnClickListener(this);

        Button btnExit = v.findViewById(R.id.btn_exit_recipe_view);
        btnExit.setOnClickListener(this);

        Button btnAddToMeals = v.findViewById(R.id.btn_add_to_meals);
        if (MainActivity.selectedRecipe.getSelectedMeal()){
            btnAddToMeals.setText(R.string.str_remove_from_meals);
        }
        btnAddToMeals.setOnClickListener(this);

        Button btnDelete = v.findViewById(R.id.btn_delete_recipe);
        btnDelete.setOnClickListener(this);

        TextView recipeName = v.findViewById(R.id.tv_recipe_name);
        TextView recipeType = v.findViewById(R.id.tv_recipe_type);

        List<Ingredient> ingredients = MainActivity.selectedRecipe.getIngredients();
        recipeName.setText(recipe.getRecipeName());
        recipeType.setText(recipe.getRecipeType());

        ListView lvIngredients = v.findViewById(R.id.lv_recipe_ingredients);

        for(Ingredient i : ingredients){
            ingredientList.add(i.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), R.layout.ingredient_list_item, ingredientList);
        lvIngredients.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_share_recipe_view:
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container,new ShareRecipeFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_delete_recipe:
                MainActivity.db.dao().removeRecipe(recipe);
                MainActivity.fragMan.popBackStack();
                break;
            case R.id.btn_add_to_meals:
                MainActivity.selectedRecipe.setSelectedMeal(!MainActivity.selectedRecipe.getSelectedMeal());
                MainActivity.db.dao().updateRecipe(MainActivity.selectedRecipe);
                MainActivity.fragMan.beginTransaction().detach(this).attach(this).commit();
                break;
            case R.id.btn_exit_recipe_view:
                MainActivity.fragMan.popBackStack();
                break;
        }
    }
}
