package com.example.reciplease;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.reciplease.R;


public class MainMenuFragment extends Fragment implements View.OnClickListener {

    public MainMenuFragment() {
        // Required empty public constructor
    }


    // function is called on the creation of the fragment...
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_create_recipe:
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container, new RecipeCreationFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_menu:
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container, new MealsListFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_share:
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container, new ShareRecipeFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_go_to_recipe_list:
                MainActivity.fragMan.beginTransaction().replace(R.id.fragment_container, new RecipeListFragment()).addToBackStack(null).commit();
                break;
            case R.id.btn_exit:
                System.exit(0);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View v){
        Typeface t = getResources().getFont(R.font.custom_font_1);

        Button btnCreate = v.findViewById(R.id.btn_create_recipe);
        btnCreate.setTypeface(t);
        btnCreate.setOnClickListener(this);

        Button btnExit = v.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(this);
        btnExit.setTypeface(t);

        Button btnMeals = v.findViewById(R.id.btn_menu);
        btnMeals.setOnClickListener(this);
        btnMeals.setTypeface(t);

        Button btnShare = v.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);
        btnShare.setTypeface(t);

        Button btnRecipeList = v.findViewById(R.id.btn_go_to_recipe_list);
        btnRecipeList.setOnClickListener(this);
    }
}
