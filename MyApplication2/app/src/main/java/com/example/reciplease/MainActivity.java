package com.example.reciplease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.os.Bundle;

import com.example.reciplease.R;

public class MainActivity extends AppCompatActivity {

    public static FragmentManager fragMan;
    public static RecipeDatabase db;
    public static Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);

    }

    private void init(Bundle b){
        fragMan = getSupportFragmentManager();
        //deleteDatabase("RecipeDB");
        db = Room.databaseBuilder(getApplicationContext(),
                RecipeDatabase.class,"RecipeDB").allowMainThreadQueries().build();

        if(findViewById(R.id.fragment_container) != null){
            if(b != null){
               return;
            }
            fragMan.beginTransaction().add(R.id.fragment_container,
                    new MainMenuFragment()).commit();
        }
    }

}
