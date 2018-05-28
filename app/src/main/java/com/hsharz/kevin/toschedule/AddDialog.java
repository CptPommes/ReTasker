package com.hsharz.kevin.toschedule;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Dialog for adding tasks to the database
 */
public class AddDialog extends Dialog implements android.view.View.OnClickListener {
    DatabaseHelper schedulerDB;
    Calendar calendar;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public Activity activity;
    private Button bnOk = null;
    private Button bnCancel = null;
    private EditText nameTask = null;
    private Spinner frequency = null;
    private Spinner weekday = null;

    private String day = "1";
    private String tempDay = "1";


    public AddDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_dialog);
        schedulerDB = new DatabaseHelper(getContext());
        calendar = Calendar.getInstance();
        bnOk = (Button) findViewById(R.id.addOkBtn);
        bnCancel = (Button) findViewById(R.id.addCancelBtn);
        nameTask = (EditText) findViewById(R.id.nameTaskEdit);
        frequency = (Spinner)findViewById(R.id.dropDownFrequency);
        weekday = (Spinner)findViewById(R.id.dropDownWeekday);

        frequencySpinner();
        clickOk();
        clickCancel();


    }

    /**
     * Setup the spinners
     *
     * Sets both the spinners and gives them onItemSelected methods so that the chosen options can be stored in the belonging variables
     * If the first is set to "Once", the second won't be visible.
     */
    private void frequencySpinner(){
        String[] items = new String[]{activity.getResources().getString(R.string.once), activity.getResources().getString(R.string.weekly)};

        ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
        frequency.setAdapter(frequencyAdapter);
        items = new String[]{activity.getResources().getString(R.string.sunday), activity.getResources().getString(R.string.monday),activity.getResources().getString(R.string.tuesday),activity.getResources().getString(R.string.wednesday),activity.getResources().getString(R.string.thursday),activity.getResources().getString(R.string.friday),activity.getResources().getString(R.string.saturday)};
        frequencyAdapter = new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
        weekday.setAdapter(frequencyAdapter);
        frequency.getBackground().setColorFilter(activity.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        weekday.getBackground().setColorFilter(activity.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        weekday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){

                if(weekday.getVisibility() == View.GONE){
                    day = "8";
                } else
                    day = String.valueOf(position+1);

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case(0):
                        weekday.setVisibility(View.GONE);
                        tempDay = day;
                        day = "8";
                        break;
                    case(1):
                        weekday.setVisibility(View.VISIBLE);
                        day = tempDay;
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    /**
     * Onclick for OK-button
     *
     * Checks if there is a name but into the nameTask edittext. If not, the user is asked to put one.
     * Inserts a new dataset into the database with the specified name and date.
     */
    private void clickOk(){
        bnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(nameTask.getText().toString().matches("")){
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.noNameEntered), Toast.LENGTH_SHORT ).show();
                    return;
                }

                if(day == "8") {
                   schedulerDB.insertData(nameTask.getText().toString(), day, null);    //if task is "once"
                }else
                    schedulerDB.insertData(nameTask.getText().toString(), day, "0");    //if task is "weekly"
                dismiss();

            }
        });
    }

    /**
     * Onclick for cancel-button
     */
    private void clickCancel(){
        bnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                dismiss();

            }
        });
    }

    /**
     * If the user clicks next to the dialog it goes back to the previous view, dismissing the dialog.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            default:
                break;
        }
        dismiss();

    }

}
