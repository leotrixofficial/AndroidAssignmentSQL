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
    // Login cursor for querying login data from database
    protected Cursor loginCursor;

    // EditText's for username and password input
    protected EditText usernameText, passwordText;

    // Database for Login and Products tables
    protected SQLiteDatabase myDB;

    // Database and table names
    protected String databaseName = "myDB",
            loginTableName = "Login",
            productsTableName = "Products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign EditText's
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        // Open or create the database
        myDB = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);

        // Initialize Login and Products as tables in the database
        initializeLoginAndProductsTables();

        // Set an onClick listener for login button
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // Set an onClick listener for register button
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    // Creates a Login table and a Products table if they don't already exist
    protected void initializeLoginAndProductsTables() {
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + loginTableName
                + "(Username VARCHAR,Password VARCHAR);");
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " + productsTableName
                + " (Name VARCHAR,Cost DOUBLE)");

        // Select all from the Login table
        loginCursor = myDB.rawQuery("SELECT * FROM " + loginTableName, null);

        // Insert admin login values but ignore if admin login values already exists
        if (!userExists("admin"))
            myDB.execSQL("INSERT INTO " + loginTableName + " VALUES('admin','root');");
    }

    // Validate user input and login
    protected void login() {
        // Move cursor to start of table
        loginCursor.moveToFirst();
        // While not after the last row in the table
        while (!loginCursor.isAfterLast()) {
            // Get username and password from table
            String usernameFromDB = loginCursor.getString(0),
                    passwordFromDB = loginCursor.getString(1);

            if (usernameFromDB.equals(usernameText.getText().toString()) &&
                    passwordFromDB.equals(passwordText.getText().toString())) {
                // Open new activity
                startActivity(new Intent(this, ProductActivity.class));
                // Close login activity
                this.finish();
                // Show message if successful
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Return out of method
                return;
            }
            // Move to next row
            loginCursor.moveToNext();
        }

        // Show message if not successful
        Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show();
    }

    // Restarts activity
    protected void restart() {
        // Open new activity
        startActivity(new Intent(this, LoginActivity.class));
        // Close current activity
        this.finish();
    }

    protected void register() {
        String newUsername = usernameText.getText().toString(), newPassword = passwordText.getText()
                .toString();
        // If the username doesn't already exist in the database
        if (!userExists(newUsername)) {
            // Add new username and password to login table
            myDB.execSQL("INSERT INTO " + loginTableName + " VALUES('" + newUsername + "','" +
                    newPassword + "');");
            restart();
        } else
            Toast.makeText(this, newUsername + " already exists!", Toast.LENGTH_SHORT).show();
    }

    protected boolean userExists(String username) {
        // Move cursor to start of table
        loginCursor.moveToFirst();
        // While not after the last row in the table
        while (!loginCursor.isAfterLast()) {
            // If username matches, return true
            if (username.equalsIgnoreCase(loginCursor.getString(0))) {
                return true;
            }
            // Move to next row
            loginCursor.moveToNext();
        }
        // If code reaches to this point, return false as the username doesn't exist
        return false;
    }
}
