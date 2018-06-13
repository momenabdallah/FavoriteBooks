package com.example.android.favoritebooks;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.favoritebooks.Data.BookContract;
import com.example.android.favoritebooks.Data.BookContract.BookEntry;
import com.example.android.favoritebooks.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity {
    private BookDbHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        DbHelper = new BookDbHelper(this);

        // calling helper method to display information in the onscreen TextView about the state of
        // the books database.
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = DbHelper.getReadableDatabase();

        String[] projection = {BookEntry._ID,
                BookEntry.COLUMN_PRODUCT,
                BookEntry.COLUMN_PRODUCT_DESCRIPTION,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);
        TextView displayView = findViewById(R.id.text_view_books);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - product - description - price - quantity - supplier - supplier phone
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(BookEntry._ID + " - " +
                    BookEntry.COLUMN_PRODUCT + " - " + BookEntry.COLUMN_PRODUCT_DESCRIPTION + " - " +
                    BookEntry.COLUMN_PRICE + " - " + BookEntry.COLUMN_QUANTITY + " - " +
                    BookEntry.COLUMN_SUPPLIER_NAME + " - " + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int productColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT);
            int descriptionColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentProduct = cursor.getString(productColumnIndex);
                String currentDescription = cursor.getString(descriptionColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                long currentPhone = cursor.getLong(phoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentProduct + " - " + currentDescription + " - " + currentPrice + " - " +
                        currentQuantity + " - " + currentSupplier + " - " + currentPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void insertBook() {
        //Gets the data repository in write mode
        SQLiteDatabase db = DbHelper.getWritableDatabase();


        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT, "The Fault In Our Stars");
        values.put(BookEntry.COLUMN_PRODUCT_DESCRIPTION, "John Green. Love and pain");
        values.put(BookEntry.COLUMN_PRICE, 1000);
        values.put(BookEntry.COLUMN_QUANTITY, 1);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Alma littera");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 23450);

        //Insert a new row, returning the primary key value of the new row.
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);
        Log.d("MainActivity", "Dummy values inserted: " + newRowId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
