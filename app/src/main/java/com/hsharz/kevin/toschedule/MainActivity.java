package com.hsharz.kevin.toschedule;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Main view of Retasker
 *
 * Shows the toolbar, the filter and the listview with all the tasks from the database
 */
public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper schedulerDB;
    private final Handler handler = new Handler();
    Button btnShowTask, btnUpdate, btnDelete;
    EditText editTask, editID;
    Spinner filter;
    ArrayList<Tasks> liste;
    TextView dynamicTextView;
    RelativeLayout.LayoutParams params;
    Calendar calendar;
    Calendar tempCal;

    int setFilterTo = 0;
    int today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        setSupportActionBar(toolbar);
        schedulerDB = new DatabaseHelper(this);
        calendar = Calendar.getInstance();
        today = getToday();
        tempCal = Calendar.getInstance();

        //Layout element setup
        editTask = (EditText) findViewById(R.id.showTask);
        editID = (EditText) findViewById(R.id.tempID);
        btnShowTask = (Button) findViewById(R.id.showBtn);
        btnUpdate = (Button) findViewById(R.id.updateBtn);
        btnDelete = (Button) findViewById(R.id.deleteBtn);
        filter = (Spinner) findViewById(R.id.spinnerFilter);

        //Functions
        filterSpinner();
        showTasks(schedulerDB.getAllData()); // Needed so the app doesn't load without a list
        autoRefresh();





    }

    //Inflate toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //Toolbar onclick functions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                // User chose the "Home" item, switch to home screen
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_add:
                // User chose the "Add" action, popup for adding

                openMyDialog();
                return true;

            case R.id.action_debug:
                // User chose the "Debug" action, switching to Debug screen
                startActivity(new Intent(getApplicationContext(), DebugActivity.class));


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
        Setting up the filter function

        Implementing the items of the filter and giving them their onItemSelected
     */
    public void filterSpinner(){
        String[] items = new String[]{getResources().getString(R.string.all), getResources().getString(R.string.once), getResources().getString(R.string.weekly)};

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.filterspinner_layout, items);
        filter.setAdapter(frequencyAdapter);
        filter.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                setFilterTo = position; //Later tells the database query what to ask for



            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
        Gets all the tasks from the database query that is given over the Cursor.

        First sets all the tasks that are "once" for "today".
        Then goes through and puts all the weekly tasks in order.
        Then goes through one last time and puts all the tasks that are already done for next week again.
     */
    public void showTasks(Cursor _res){

        Cursor res = _res;
        ListView taskListe = (ListView) findViewById(R.id.listOfTasks);
        liste = new ArrayList<Tasks>();
        int week =calendar.get(Calendar.WEEK_OF_YEAR);

        /**
         * Sorting the tasks
         *
         * First get all tasks that are once, and therefore today. After that, iterate through the cursor multiple times and get all the remaining tasks in order of days to come.
         */

        while(res.moveToNext()){
            if(Integer.parseInt(res.getString(2)) == 8){
                String day = checkWeekday(res.getString(2));


                liste.add(new Tasks(res.getString(0),res.getString(1), day, res.getString(3)));
            }

        }

        /**
         * Go through all the tasks, and order them beginning by the current day
         */

        for(int i = today; i < today+7; i++){
            res.moveToPosition(-1);//Resetting the cursor to the beginning, so it can iterate multiple times
            while(res.moveToNext()){

                //Put all the weekly tasks that haven't been checked as "today"
                if(res.getString(3) != null){
                    if(res.getString(3) == "-1" ){
                        Log.e("-1", "Last week");
                        liste.add(new Tasks(res.getString(0),res.getString(1), getResources().getString(R.string.today), res.getString(3)));

                    }
                     else if(Integer.parseInt(res.getString(2)) != 8 && Integer.parseInt(res.getString(3)) != (week)) { //if the date is set to 8, the task is already in the list, so don't add it
                        if (Integer.parseInt(res.getString(2)) == i || Integer.parseInt(res.getString(2)) + 7 == i) {

                            String day = checkWeekday(res.getString(2));

                            //Set up remaining tasks from last days, above "if" checks for this -1
                            if(i == today){
                                schedulerDB.updateWeek(res.getString(0), "-1");
                            }

                            liste.add(new Tasks(res.getString(0),res.getString(1), day, res.getString(3)));
                        }
                    }
                }
            }
        }
        /**
            Goes through one last time, and puts all the tasks that are next week
         */
        res.moveToPosition(-1);
        while(res.moveToNext()){
            if(res.getString(3) != null) {
                if (Integer.parseInt(res.getString(3)) == (week)) {
                    String day = getDayName(res.getString(2));


                    liste.add(new Tasks(res.getString(0), res.getString(1), day, res.getString(3)));
                }
            }
        }

        TaskAdapter taskAdapter = new TaskAdapter(this, liste);

        taskListe.setAdapter(taskAdapter);

        /**
         * Onclick listener for the listview
         *
         * Every item can be clicked on to mark them as "done" and either delete them if they are "once",
         * or move them to next week if they are "weekly".
         * You can only move tasks that are "today"
         */
        taskListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(liste.get(position).getWeek() == null) {
                    schedulerDB.deleteData(liste.get(position).getId());
                }   else if(liste.get(position).getTimeToDo()== getResources().getString(R.string.today)){
                    schedulerDB.updateWeek(liste.get(position).getId(), String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
                } else
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.NotMovingFutureTasks), Toast.LENGTH_SHORT).show();
            }
        });

        registerForContextMenu(taskListe);



    }

    /**
     * Checks the weekday from the handed over day
     *
     * Checks the given day for its day name. First checks if it is "today" or "tomorrow", if not,
     * it gets the belonging name.
     *
     * @param _day Day of the tasks that shall be checked
     * @return
     */
    public String checkWeekday(String _day) {

        if (today == Integer.parseInt(_day)) {
            return getResources().getString(R.string.today);
        } else if ((today + 1 == Integer.parseInt(_day))) {
            return getResources().getString(R.string.tomorrow);
        }
        else {
            return getDayName(_day);

        }

    }

    /**
     * Returns the belonging weekday to the given day
     *
     * @param _day Day of the tasks that shall be checked
     * @return
     */
    public String getDayName(String _day){
        switch(_day){
            case("1"):
                return getResources().getString(R.string.sunday);

            case("2"):
                return getResources().getString(R.string.monday);

            case("3"):
                return  getResources().getString(R.string.tuesday);

            case("4"):
                return  getResources().getString(R.string.wednesday);

            case("5"):
                return  getResources().getString(R.string.thursday);

            case("6"):
                return  getResources().getString(R.string.friday);

            case("7"):
                return  getResources().getString(R.string.saturday);

            default:
                return  getResources().getString(R.string.today);


        }
    }

    /**
     * Returns the number of the current day for comparison purposes.
     *
     * @return
     */
    public int getToday(){
        switch(calendar.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                return 1;
            case Calendar.MONDAY:
                return 2;
            case Calendar.TUESDAY:
                return 3;
            case Calendar.WEDNESDAY:
                return 4;
            case Calendar.THURSDAY:
                return 5;
            case Calendar.FRIDAY:
                return 6;
            case Calendar.SATURDAY:
                return 7;
            default:
                return 8;

        }
    }

    /**
     * Opens the AddDialog
     */

    private void openMyDialog() {
        AddDialog dialog = new AddDialog(this);
        dialog.show();
    }

    /**
     * Opens the EditDialog
     * @param _id   ID of the task to edit
     * @param _weekly   Week of the task to edit
     */
    private void openEditDialog(String _id, Boolean _weekly) {
        EditDialog dialog = new EditDialog(this, _id, _weekly);
        dialog.show();
    }

    /**
     * Gets called continuously to update the list view.
     *
     * Switch statement decides which query gets called from the database, decided by the filter.
     * The contents of that query are then handed over to the showtasks-function.
     */
    private void autoRefresh(){
        Cursor res;
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                switch(setFilterTo){
                    case 0:
                        Cursor all = schedulerDB.getAllData();
                        showTasks(all);
                        break;
                    case 1:
                        Cursor once = schedulerDB.getOnce();
                        showTasks(once);
                        break;
                    case 2:
                        Cursor weekly = schedulerDB.getWeekly();
                        showTasks(weekly);
                        break;
                    default:
                        break;
                }



                autoRefresh();
            }
        }, 1000);
    }

    /**
     * Longlick Context menus for the listview items
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.listOfTasks) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    /**
     * Onclick events for the listview context menus
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.edit:
                if(liste.get(info.position).getWeek() == null) {
                    openEditDialog(liste.get(info.position).getId(), false);
                } else {
                    openEditDialog(liste.get(info.position).getId(), true);
                }


                return true;
            case R.id.delete:
                schedulerDB.deleteData(liste.get(info.position).getId());

                return true;
            default: return super.onContextItemSelected(item);

        }
    }
}
