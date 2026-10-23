package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvIngredients, tvInstructions;
    private DatabaseHelper dbHelper;
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvDetailRecipeName);
        tvIngredients = findViewById(R.id.tvDetailIngredientsList);
        tvInstructions = findViewById(R.id.tvDetailInstructions);

        // Get recipe ID passed from SuggestedRecipesActivity
        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (recipeId != -1) {
            loadRecipeDetails();
        }
    }

    private void loadRecipeDetails() {
        Recipe recipe = dbHelper.getRecipeById(recipeId);
        if (recipe != null) {
            tvTitle.setText(recipe.getName());
            // Format ingredients nicely by replacing commas with line breaks
            tvIngredients.setText(recipe.getIngredients().replace(",", "\n"));
            tvInstructions.setText(recipe.getInstructions());
        }
    }
}