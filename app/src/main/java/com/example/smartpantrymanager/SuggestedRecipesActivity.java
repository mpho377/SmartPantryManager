package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private TextView tvEmptyState;
    private DatabaseHelper dbHelper;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        recyclerViewRecipes = findViewById(R.id.recyclerViewRecipes);
        recyclerViewRecipes.setLayoutManager(new LinearLayoutManager(this));

        tvEmptyState = findViewById(R.id.tvEmptyState);

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        // Fetch strictly matched recipes based on current pantry items
        List<Recipe> matchingRecipes = dbHelper.getSuggestedRecipes();

        if (matchingRecipes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerViewRecipes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerViewRecipes.setVisibility(View.VISIBLE);

            // Set up adapter with click listener to view full recipe details
            recipeAdapter = new RecipeAdapter(this, matchingRecipes, new RecipeAdapter.OnRecipeClickListener() {
                @Override
                public void onRecipeClick(Recipe recipe) {
                    Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                    intent.putExtra("RECIPE_ID", recipe.getId());
                    startActivity(intent);
                }
            });
            recyclerViewRecipes.setAdapter(recipeAdapter);
        }
    }
}