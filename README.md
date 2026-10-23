# Smart Pantry Manager

An intuitive, modern Android application built to help users manage kitchen inventory, track food expiration dates to reduce waste, and discover recipes based on available pantry items. 

Developed in Java using Android Studio, this project features a modern green-themed Material Design interface, a persistent bottom navigation architecture, and offline data storage.

---

## Overview & Key Features

* **Pantry Inventory Tracking:** View, add, and organize food items stored in your local pantry.
* **Smart Expiry Alerts:** Easily monitor items approaching their expiration dates to minimize food waste.
* **Recipe Suggestions:** Discover tailored recipes you can cook using the ingredients currently available in your inventory.
* **Persistent Bottom Navigation:** Seamlessly switch between **Pantry**, **Suggested Recipes**, and **Settings** using a custom green-themed `BottomNavigationView` with flicker-free activity transitions (`overridePendingTransition(0, 0)`).
* **User Preferences:** Configure expiration alert thresholds and toggle measurement units (Metric/Imperial).
* **Local Data Persistence:** SQLite integration using `DatabaseHelper` for fast, offline storage without requiring external servers.

---

## Tech Stack & Dependencies

* **Language:** Java
* **IDE:** Android Studio
* **Min SDK:** Android 7.0 (API Level 24)
* **UI Components:** 
  * Material Design Components (`BottomNavigationView`, `FloatingActionButton`)
  * `RecyclerView` & Custom Adapters (`PantryAdapter`, `RecipeAdapter`)
  * Custom XML State Selectors (`bottom_nav_color.xml`)
  * `LinearLayout` with weighted views
* **Database:** SQLite Local Storage (`DatabaseHelper`)
* **Version Control:** Git & GitHub Desktop (Structured across 10+ incremental commits)

---

## Project Architecture & Directory Layout

```text
SmartPantryManager/
│
├── app/src/main/
│   ├── java/com/example/smartpantrymanager/
│   │   ├── MainActivity.java                 # Primary screen (Pantry view)
│   │   ├── SuggestedRecipesActivity.java     # Recipe recommendation screen
│   │   ├── SettingsActivity.java             # User preferences & toggles screen
│   │   ├── AddEditPantryActivity.java        # Interface for adding/editing pantry items
│   │   ├── RecipeDetailActivity.java         # Detailed view for selected recipes
│   │   ├── DatabaseHelper.java               # SQLite database helper for local storage
│   │   ├── PantryItem.java & Recipe.java     # Data models
│   │   └── PantryAdapter.java & Adapters     # RecyclerView adapters
│   │
│   └── res/
│       ├── layout/                           # Layout XMLs (activity_main, activity_settings, etc.)
│       ├── menu/                             # bottom_nav_menu.xml
│       ├── color/                            # bottom_nav_color.xml state selector
│       └── values/                           # colors.xml, strings.xml, themes.xml
