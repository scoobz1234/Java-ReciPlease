package com.example.reciplease;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.reciplease.R;

import java.util.ArrayList;

public class RecipeCreationFragment extends Fragment implements View.OnClickListener{

    private Button btnAdd, btnCreate, btnCancel;
    private ListView lvIngredients;
    private EditText ingredientAmount, ingredientName, recipeName;
    private Spinner ingredientType, recipeType;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayList<Ingredient> ingredients = new ArrayList<>();

    public RecipeCreationFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_creation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btn_add_ingredient);
        btnAdd.setOnClickListener(this);
        btnAdd.setEnabled(false);

        btnCreate = view.findViewById(R.id.btn_submit_recipe);
        btnCreate.setOnClickListener(this);

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

        lvIngredients = view.findViewById(R.id.lv_ingredients_list);
        ingredientAmount = view.findViewById(R.id.et_ingredient_amount);
        ingredientAmount.setEnabled(false);
        ingredientName = view.findViewById(R.id.et_ingredient_name);

        ingredientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length() == 0){
                    ingredientAmount.setEnabled(false);
                }else{
                    ingredientAmount.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    ingredientAmount.setEnabled(false);
                }else{
                    ingredientAmount.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() == 0){
                    ingredientAmount.setEnabled(false);
                }else{
                    ingredientAmount.setEnabled(true);
                }
            }
        });

        ingredientAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length() == 0){
                    btnAdd.setEnabled(false);
                }
                else{
                    btnAdd.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() == 0){
                    btnAdd.setEnabled(false);
                }
                else{
                    btnAdd.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().trim().length() == 0){
                    btnAdd.setEnabled(false);
                }else{
                    btnAdd.setEnabled(true);
                }
            }
        });

        ingredientType = view.findViewById(R.id.spinner_measurement_type);
        recipeName = view.findViewById(R.id.et_recipe_name);
        recipeType = view.findViewById(R.id.spinner_recipe_type);

        adapter = new ArrayAdapter<>(this.getContext(), R.layout.ingredient_list_item, listItems);
        lvIngredients.setAdapter(adapter);

        // Adds a click feature to the Ingredients list so we can remove ingredients
        lvIngredients.setOnItemClickListener((a, v, p, l) -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Delete?");
            Object o = lvIngredients.getAdapter().getItem(p);
            adb.setMessage("Are you sure you want to delete:\n (" + o.toString() + ")");
            final int positionToRemove = p;
            adb.setNegativeButton("Cancel", null);
            adb.setPositiveButton("Ok", (d, which) -> {
        listItems.remove(positionToRemove);
        adapter.notifyDataSetChanged();
            });
            adb.show();
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_add_ingredient:
                Ingredient tmp = CreateIngredient();
                ingredients.add(tmp);
                listItems.add(tmp.toString());
                btnAdd.setEnabled(false);
                ClearTextBoxes();
                break;
            case R.id.btn_cancel:
                MainActivity.fragMan.popBackStack();
                break;
            case R.id.btn_submit_recipe:
                CreateRecipe();
                MainActivity.fragMan.popBackStack();
                break;
        }
    }

    private Ingredient CreateIngredient(){
        float tmpAmount = Float.parseFloat(ingredientAmount.getText().toString());
        String tmpName = ingredientName.getText().toString();
        String tmpType = ingredientType.getSelectedItem().toString();

        if (tmpName.matches("")){
            tmpName = "I Forgot To Name This Ingredient";
        }
        if (tmpType.matches("")){
            tmpType = "Cups";
        }

        Measurements measurement = Measurements.NA;

        for(Measurements m : Measurements.values()){
            if (m.name().equals(tmpType)){
                measurement = m;
            }
        }

        Ingredient i = new Ingredient(tmpAmount,tmpName,measurement);
        return i;
    }

    private void CreateRecipe(){
        String tmpName = recipeName.getText().toString();
        String tmpType = recipeType.getSelectedItem().toString();
        Types type = Types.NA;

        for(Types t : Types.values()){
            if (t.name().equals(tmpType)){
                type = t;
            }
        }

        Recipe r = new Recipe(tmpName, type, ingredients);

        MainActivity.db.dao().addRecipe(r);

        MainActivity.fragMan.popBackStack();
        Toast.makeText(this.getContext(),"Successfully added Recipe!",Toast.LENGTH_SHORT).show();
    }

    private void ClearTextBoxes(){
        ingredientAmount.setText("");
        ingredientName.setText("");

        if(ingredientAmount.hasFocus()){
            ingredientAmount.clearFocus();
        }
        if(ingredientName.hasFocus()){
            ingredientName.clearFocus();
        }
        if(ingredientType.hasFocus()){
            ingredientType.clearFocus();
        }

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(btnAdd.getWindowToken(),0);
    }
}
