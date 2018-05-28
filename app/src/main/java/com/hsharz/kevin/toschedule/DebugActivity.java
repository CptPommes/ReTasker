package com.hsharz.kevin.toschedule;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * DebugActivity
 *
 * Just there for development and testing purposes.
 * Will be deleted or disabled on a finished build
 */
public class DebugActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper schedulerDB;
    Button btnShowTask, btnUpdate, btnDelete, btnDropBD;
    EditText editTask, editID;

    TextView dynamicTextView;
    RelativeLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        schedulerDB = new DatabaseHelper(this);

        editTask = (EditText) findViewById(R.id.showTask);
        editID = (EditText) findViewById(R.id.tempID);
        btnShowTask = (Button) findViewById(R.id.showBtn);
        btnUpdate = (Button) findViewById(R.id.updateBtn);
        btnDelete = (Button) findViewById(R.id.deleteBtn);
        btnDropBD = (Button) findViewById(R.id.dropBtn);


        viewAll();
        updateData();
        deleteData();
        dropData();



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

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
     * Shows all the data in the database in a popup message
     */
    public void viewAll(){
        btnShowTask.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Cursor res = schedulerDB.getAllData();
                        if(res.getCount() == 0){
                            showMessage("Error", "Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while(res.moveToNext()){
                            buffer.append("ID : " + res.getString(0) +"\n");
                            buffer.append("Task : " + res.getString(1) +"\n");
                            buffer.append("Week : " + res.getString(3) + "\n");
                            buffer.append("Date : " + res.getString(2) + "\n\n");

                        }

                        showMessage("Data", buffer.toString());
                    }
                }
        );
    }

    /**
     * Used to insert data into the database
     */
    public void updateData(){
        btnUpdate.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        boolean isUpdate = schedulerDB.updateData(editID.getText().toString(), editTask.getText().toString(), "1234-01-01");
                        if(isUpdate == true){

                            Toast.makeText(DebugActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(DebugActivity.this, "not Inserted", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Used to delete data in the database
     */
    public void deleteData(){
        btnDelete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Integer deletedRows = schedulerDB.deleteData(editID.getText().toString());
                        if(deletedRows > 0){
                            Toast.makeText(DebugActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(DebugActivity.this, "not Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    /**
     * Used to drop the database
     */
    public void dropData(){
        btnDropBD.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        schedulerDB.drop();
                        Toast.makeText(DebugActivity.this, "Dropped the base", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    /**
     * Builds the message popup window
     *
     * @param title
     * @param message
     */
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    /**
     * Opens the AddDialog
     */
    private void openMyDialog() {
        AddDialog dialog = new AddDialog(this);
        dialog.show();
    }
}
