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
 * Dialog for editing tasks in the database
 */
public class EditDialog extends Dialog implements android.view.View.OnClickListener {
    DatabaseHelper schedulerDB;
    Calendar calendar;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public Activity activity;
    private Button bnOk = null;
    private Button bnCancel = null;
    private EditText nameTask = null;
    private Spinner weekday = null;
    private String id;
    private Boolean weekly;

    private String day = "1";


    public EditDialog(Activity activity, String _id, Boolean _weekly) {
        super(activity);
        this.activity = activity;
        this.id = _id;
        this.weekly = _weekly;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_dialog);
        schedulerDB = new DatabaseHelper(getContext());
        calendar = Calendar.getInstance();
        bnOk = (Button) findViewById(R.id.addOkBtn);
        bnCancel = (Button) findViewById(R.id.addCancelBtn);
        nameTask = (EditText) findViewById(R.id.nameTaskEdit);
        weekday = (Spinner)findViewById(R.id.dropDownWeekday);


        spinner();

        clickOk();
        clickCancel();


    }

    /**
     * Setup the spinners for the weekday
     */
    public void spinner() {
        /**
         * calls if the tasks was weekly, if not, it will be automatically set to "once"
         */
        if(weekly) {
            String[] items = new String[]{activity.getResources().getString(R.string.sunday), activity.getResources().getString(R.string.monday), activity.getResources().getString(R.string.tuesday), activity.getResources().getString(R.string.wednesday), activity.getResources().getString(R.string.thursday), activity.getResources().getString(R.string.friday), activity.getResources().getString(R.string.saturday)};
            ArrayAdapter<String> frequencyAdapter = new ArrayAdapter<String>(activity.getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
            weekday.setAdapter(frequencyAdapter);
            weekday.getBackground().setColorFilter(activity.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            weekday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        day = String.valueOf(position + 1); //chosen day is stored

                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            day = "8";
            weekday.setVisibility(View.GONE);
        }
    }

    /**
     * Onclick for OK-button
     *
     * Checks if there is a name but into the nameTask edittext. If not, the user is asked to put one.
     * Edits the name and the date of the specified task via the id in the database
     */
    private void clickOk(){
        bnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(nameTask.getText().toString().matches("")){
                    Toast.makeText(activity.getApplicationContext(), activity.getResources().getString(R.string.noNameEntered), Toast.LENGTH_SHORT ).show();
                    return;
                }

                schedulerDB.updateData(id, nameTask.getText().toString(), day);
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
