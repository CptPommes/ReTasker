package com.hsharz.kevin.toschedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Settings tab
 *
 * Used for all kinds of settings that the user can put
 */

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper schedulerDB;
    Button btnDrop = null;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        schedulerDB = new DatabaseHelper(this);
        btnDrop = (Button) findViewById(R.id.btnDrop);

        dropButtonClick();
    }

    //Toolbar setup
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //Toolbar onclick setup
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_add:
                // User chose the "Add" action, popup for adding...

                openMyDialog();
                return true;

            case R.id.action_debug:
                // User chose the "Debug" action, switching to Debug screen...
                startActivity(new Intent(getApplicationContext(), DebugActivity.class));


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Function for the drop database button
     *
     * Has to be clicked two times for the database to be deleted, so that it can't happen on accident
     */
    public void dropButtonClick(){
        btnDrop.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        if(counter == 0) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.clickAgain), Toast.LENGTH_SHORT).show();
                            counter++;
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.droppedDB), Toast.LENGTH_LONG).show();
                            schedulerDB.drop();
                            counter = 0;

                        }
                    }
                }
        );
    }

    /**
     * Opens the AddDialog
     */
    private void openMyDialog() {
        AddDialog dialog = new AddDialog(this);
        dialog.show();
    }
}
