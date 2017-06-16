package com.example.adrialwalters.habittracker;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.adrialwalters.habittracker.data.HabitContract.HabitEntry;
import com.example.adrialwalters.habittracker.data.HabitDbHelper;

public class HabitActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private HabitDbHelper mDbHelper;

    private TextView mActivityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HabitActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

       mActivityView = (TextView) findViewById(R.id.text_view_habit);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new HabitDbHelper(this);

        //Show database info on startup
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Helper method to display information in the onscreen TextView about the state of
     * the habit database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_ACTIVITY_TYPE,
                HabitEntry.COLUMN_WEEK_DAY,
                HabitEntry.COLUMN_TIME
        };

        // Cast the TextView where data wil be displayed
        mActivityView = (TextView) findViewById(R.id.text_view_habit);

        // Perform a query on the habit table
        Cursor cursor = db.query(HabitEntry.TABLE_NAME, projection, null, null, null, null, null);

        readFromCursor(cursor);

    }

    private void readFromCursor(Cursor cursor) {
        try {
            // Set header text here
            mActivityView.setText("This habit table contains" + cursor.getCount() + "activities.\n\n");

            // Attach the column headers to the display
            mActivityView.append(HabitEntry._ID + " - " +
                    HabitEntry.COLUMN_ACTIVITY_TYPE + " - " +
                    HabitEntry.COLUMN_WEEK_DAY + " - " +
                    HabitEntry.COLUMN_TIME + " \n");

            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(HabitEntry._ID);
            int activityColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_ACTIVITY_TYPE);
            int dayColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_WEEK_DAY);
            int timeColumnIndex = cursor.getColumnIndex(HabitEntry.COLUMN_TIME);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentActivity = cursor.getString(activityColumnIndex);
                int currentDay = cursor.getInt(dayColumnIndex);
                int currentTime = cursor.getInt(timeColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                mActivityView.append("\n" + currentID + " - " +
                        currentActivity + " - " +
                        currentDay + " - " +
                        currentTime);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertHabit() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column activity type are the keys,
        // and Soccer activity attributes are the values.
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_ACTIVITY_TYPE, "Soccer");
        values.put(HabitEntry.COLUMN_WEEK_DAY, HabitEntry.DAY_SATURDAY);
        values.put(HabitEntry.COLUMN_TIME, 60);

        // Insert a new row for playing Soccer in the database, returning the ID of that new row.
        // The first argument for db.insert() is the type of activity table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the activity.
        long newRowId = db.insert(HabitEntry.TABLE_NAME, null, values);

        Log.v("HabitActivity", "New Row ID" + newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_habit.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_habit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertHabit();
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
