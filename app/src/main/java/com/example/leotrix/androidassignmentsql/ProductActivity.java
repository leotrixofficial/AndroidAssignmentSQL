package com.example.leotrix.androidassignmentsql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ProductActivity extends AppCompatActivity {
    // Database and table names
    protected String databaseName = "myDB",
            loginTableName = "Login",
            productsTableName = "Products";

    // Database for Login and Products tables
    protected SQLiteDatabase myDB;

    // Products cursor for querying products data from database
    protected Cursor productsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
    }
}
