package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditPantryActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    private int itemId = -1; // -1 means we are adding a new item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pantry);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiryDate);
        btnSave = findViewById(R.id.btnSavePantry);

        // Check if we passed an item ID for editing
        if (getIntent().hasExtra("ITEM_ID")) {
            itemId = getIntent().getIntExtra("ITEM_ID", -1);
            etName.setText(getIntent().getStringExtra("ITEM_NAME"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("ITEM_QTY", 0)));
            etUnit.setText(getIntent().getStringExtra("ITEM_UNIT"));
            etExpiry.setText(getIntent().getStringExtra("ITEM_EXPIRY"));
            setTitle("Edit Pantry Item");
            btnSave.setText("Update Item");
        } else {
            setTitle("Add Pantry Item");
            btnSave.setText("Save Item");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrUpdatePantryItem();
            }
        });
    }

    private void saveOrUpdatePantryItem() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // Input Validation
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        if (qtyStr.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Please enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        if (unit.isEmpty()) {
            unit = "units";
        }

        boolean success;

        if (itemId == -1) {
            // Insert New Item
            success = dbHelper.addPantryItem(name, quantity, unit, expiry);
        } else {
            // Update Existing Item
            success = dbHelper.updatePantryItem(itemId, name, quantity, unit, expiry);
        }

        if (success) {
            String msg = (itemId == -1) ? "Item saved successfully!" : "Item updated successfully!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            finish(); // Return to MainActivity
        } else {
            Toast.makeText(this, "Error saving item to database", Toast.LENGTH_SHORT).show();
        }
    }
}