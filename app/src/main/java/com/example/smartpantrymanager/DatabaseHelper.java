package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";

    // Common Column
    public static final String COLUMN_ID = "id";

    // Pantry Table Columns
    public static final String COLUMN_PANTRY_NAME = "name";
    public static final String COLUMN_PANTRY_QTY = "quantity";
    public static final String COLUMN_PANTRY_UNIT = "unit";
    public static final String COLUMN_PANTRY_EXPIRY = "expiry_date";

    // Recipe Table Columns
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_INGREDIENTS = "ingredients"; // Formatted as "item:qty,item:qty"
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Pantry Table
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PANTRY_NAME + " TEXT NOT NULL, " +
                COLUMN_PANTRY_QTY + " REAL, " +
                COLUMN_PANTRY_UNIT + " TEXT, " +
                COLUMN_PANTRY_EXPIRY + " TEXT);";
        db.execSQL(createPantryTable);

        // Create Recipes Table
        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INGREDIENTS + " TEXT NOT NULL, " +
                COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL);";
        db.execSQL(createRecipesTable);

        // Pre-seed Recipes
        seedInitialRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    private void seedInitialRecipes(SQLiteDatabase db) {
        // 1. Omelette
        db.execSQL("INSERT INTO " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_NAME + ", " + COLUMN_RECIPE_INGREDIENTS + ", " + COLUMN_RECIPE_INSTRUCTIONS + ") VALUES (" +
                "'Omelette', " +
                "'egg:2,cheese:50', " +
                "'1. Beat eggs in a bowl.\n2. Heat a pan with butter.\n3. Pour eggs and cook on medium heat.\n4. Add cheese, fold in half, and serve hot!');");

        // 2. Tomato Salad
        db.execSQL("INSERT INTO " + TABLE_RECIPES + " (" +
                COLUMN_RECIPE_NAME + ", " + COLUMN_RECIPE_INGREDIENTS + ", " + COLUMN_RECIPE_INSTRUCTIONS + ") VALUES (" +
                "'Tomato Salad', " +
                "'tomato:2,olive oil:10', " +
                "'1. Chop tomatoes into bite-sized pieces.\n2. Drizzle olive oil over tomatoes.\n3. Season with salt and pepper, then toss gently.');");
    }

    // --- PANTRY CRUD OPERATIONS ---

    public boolean addPantryItem(String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_QTY, quantity);
        cv.put(COLUMN_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        long result = db.insert(TABLE_PANTRY, null, cv);
        return result != -1;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PANTRY, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_NAME));
                double qty = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_UNIT));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PANTRY_EXPIRY));

                list.add(new PantryItem(id, name, qty, unit, expiry));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PANTRY, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean updatePantryItem(int id, String name, double quantity, String unit, String expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PANTRY_NAME, name.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_QTY, quantity);
        cv.put(COLUMN_PANTRY_UNIT, unit.trim().toLowerCase());
        cv.put(COLUMN_PANTRY_EXPIRY, expiryDate);

        int rows = db.update(TABLE_PANTRY, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // --- GET RECIPE BY ID ---

    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES + " WHERE id = ?", new String[]{String.valueOf(recipeId)});

        Recipe recipe = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENTS));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

            recipe = new Recipe(id, name, ingredients, instructions);
            cursor.close();
        }
        return recipe;
    }

    // --- STRICT MATCHING RECIPE ALGORITHM ---

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> matchingRecipes = new ArrayList<>();
        List<PantryItem> pantryItems = getAllPantryItems();

        Map<String, Double> pantryMap = new HashMap<>();
        for (PantryItem item : pantryItems) {
            String name = item.getName().trim().toLowerCase();
            pantryMap.put(name, pantryMap.getOrDefault(name, 0.0) + item.getQuantity());
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String ingredientsStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INGREDIENTS));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

                boolean hasAllIngredients = true;
                String[] reqPairs = ingredientsStr.split(",");

                for (String pair : reqPairs) {
                    String[] parts = pair.split(":");
                    if (parts.length == 2) {
                        String reqName = parts[0].trim().toLowerCase();
                        double reqQty = Double.parseDouble(parts[1].trim());

                        if (!pantryMap.containsKey(reqName) || pantryMap.get(reqName) < reqQty) {
                            hasAllIngredients = false;
                            break;
                        }
                    }
                }

                if (hasAllIngredients) {
                    matchingRecipes.add(new Recipe(id, name, ingredientsStr, instructions));
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        return matchingRecipes;
    }
}