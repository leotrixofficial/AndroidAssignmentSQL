package com.example.leotrix.androidassignmentsql;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    // Database and table names
    protected String databaseName = "myDB",
            loginTableName = "Login",
            productsTableName = "Products";

    // Database for Login and Products tables
    protected SQLiteDatabase myDB;

    // Login cursor for querying login data from database
    protected Cursor loginCursor;

    // EditText's for username and password input
    protected EditText usernameText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign EditText's
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        // Open or create the database
        myDB = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);

        // Initialize Login and Products as tables in the database
        initializeLoginAndProductsTables();

        // Insert admin login values but ignore if admin login values already exists
        myDB.execSQL("INSERT INTO " + loginTableName + " VALUES('admin','root');");

        // Set an onClick listener for login button
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Delete all login data
        myDB.execSQL("DELETE * FROM" + loginTableName);
    }

    // Creates a Login table and a Products table if they don't already exist
    protected void initializeLoginAndProductsTables() {
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + loginTableName
                + "(Username VARCHAR,Password VARCHAR);");
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + productsTableName
                + " (Name VARCHAR,Cost DOUBLE)");
    }

    // Validate user input and login
    protected void login(View view) {
        // Select all from the Login table
        loginCursor = myDB.rawQuery("Select * from " + loginTableName, null);

        // Move cursor to start of table
        loginCursor.moveToFirst();

        // Get username and password from table
        String usernameFromDB = loginCursor.getString(0),
                passwordFromDB = loginCursor.getString(1);

        // Match username and password to input
        if (usernameFromDB.equalsIgnoreCase(usernameText.getText().toString()) &&
                passwordFromDB.equalsIgnoreCase(passwordText.getText().toString())) {
            // Open new activity
            startActivity(new Intent(view.getContext(), ProductActivity.class));

            // Close login activity
            this.finish();

            // Show message if successful
            Toast.makeText(view.getContext(), "Login successful!", Toast.LENGTH_SHORT).show();
        } else {
            // Show message if not successful
            Toast.makeText(view.getContext(), "Login failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
