package com.example.leotrix.androidassignmentsql;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {
    // Products adapter for list view
    protected ArrayAdapter productsAdapter;

    // Products cursor for querying products data from database
    protected Cursor productsCursor;

    // EditText's for product and cost
    protected EditText productNameText, costText;

    // ListView for products
    protected ListView productsListView;

    // Database for Login and Products tables
    protected SQLiteDatabase myDB;

    // Database and table names
    protected String databaseName = "myDB",
            productsTableName = "Products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Assign EditText's
        productNameText = (EditText) findViewById(R.id.productNameText);
        costText = (EditText) findViewById(R.id.costText);

        // Assign ListView
        productsListView = (ListView) findViewById(R.id.productsListView);

        // Open or create the database
        myDB = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);

        // Select all from products table
        productsCursor = myDB.rawQuery("SELECT * FROM " + productsTableName, null);

        // Update products to ListView with products from the database
        updateProducts();

        // Buttons
        Button addButton = (Button) findViewById(R.id.addButton),
                deleteAllButton = (Button) findViewById(R.id.deleteAllButton);

        // Add button on click listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct(productNameText.getText().toString(), costText.getText().toString());
            }
        });

        // Delete all button on click listener
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllProducts();
            }
        });

        // Products ListView items on click listener
        productsListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int index, long id) {
                String product = (String) productsListView.getItemAtPosition(index);
                displayMessage("Cost of " + product + ": $" + getCostOfProduct(product));
            }
        });

        // Make each item long clickable
        productsListView.setLongClickable(true);

        // Products ListView items on long click listener for deleting products
        productsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,int index, long id) {
                String product = (String) productsListView.getItemAtPosition(index);
                deleteProduct(product);
                return false;
            }
        });
    }

    // Displays Toast message
    protected void displayMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Adds product to table
    protected void addProduct(String productName, String cost) {
        // If product doesn't exist already, add to Products table
        if (!productExists(productName)) {
            myDB.execSQL("INSERT INTO " + productsTableName + " VALUES('" + productName + "'," +
                    cost + ");");
            updateProducts();
            displayMessage(productName + " added!");
            restart();
        }
    }

    // Deletes product from table
    protected void deleteProduct(String productName) {
        final String tempProductName = productName;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this product?");
        builder.setMessage("Are you sure you want to delete " + productName + "?");

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                myDB.execSQL("DELETE FROM " + productsTableName + " WHERE Name = '" +
                        tempProductName + "'");
                updateProducts();
                displayMessage(tempProductName + " deleted!");
                restart();
                // close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void deleteAllProducts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all products");
        builder.setMessage("Are you sure you want to delete all products?");

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // If yes is clicked, delete all from products table.
                myDB.execSQL("DELETE FROM " + productsTableName);
                updateProducts();
                displayMessage("All products deleted!");
                restart();
                // close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // Returns the cost of a product
    protected String getCostOfProduct(String productName) {
        // Move to first row
        productsCursor.moveToFirst();
        // Loop to the end of table
        while (!productsCursor.isAfterLast()) {
            // If product name matches, return cost!
            if (productsCursor.getString(0).equalsIgnoreCase(productName))
                return productsCursor.getString(1);
            // Move to next row
            productsCursor.moveToNext();
        }
        return "";
    }

    // Returns the products from the database
    protected String[] getProducts() {
        // Initialize products ArrayList
        ArrayList<String> tempProductsStrArrayList = new ArrayList<>();
        // Move to first row
        productsCursor.moveToFirst();
        // If there are products in the table
        if (productsCursor.getCount() > 0) {
            // Loop to the end of table
            while (!productsCursor.isAfterLast()) {
                // Add product name to string array
                tempProductsStrArrayList.add(productsCursor.getString(0));
                // Move to next row
                productsCursor.moveToNext();
            }
            // Convert ArrayList to String array
            String[] tempProductsStrArray = new String[tempProductsStrArrayList.size()];
            for (int i = 0; i < tempProductsStrArray.length; i++)
                tempProductsStrArray[i] = tempProductsStrArrayList.get(i);
            return tempProductsStrArray;
        } else {
            String[] temp = {""};
            return temp;
        }
    }

    // Check if product is already in the table
    protected boolean productExists(String productName) {
        productsCursor.moveToFirst(); // Move to first row in table
        // Loop to the end of table
        while (!productsCursor.isAfterLast()) {
            // Return true if the product already exists
            if (productName.equalsIgnoreCase(productsCursor.getString(0))) {
                displayMessage("Product already exists!");
                return true;
            }
            productsCursor.moveToNext();
        }
        // If code reaches this point, the product doesn't exist so return false
        return false;
    }

    // Restarts activity
    protected void restart() {
        // Open new activity
        startActivity(new Intent(this, ProductActivity.class));
        // Close current activity
        this.finish();
    }

    // Updates the ListView of products
    protected void updateProducts() {
        // Products adapter for list view
        productsAdapter = new ArrayAdapter<>(this, R.layout.activity_productslistview, getProducts());
        // Products list view
        productsListView.setAdapter(productsAdapter);
    }
}
