package com.hsharz.kevin.toschedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Setting up the database for the app
 *
 * Methods and creation of thr database
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Scheduler.db";
    public static final String TABLE_NAME = "tasks_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TASK";
    public static final String COL_3 = "DATE";
    public static final String COL_4 = "WEEK";

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("+ COL_1 +" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_2+" TEXT, "+COL_3+" TEXT, "+ COL_4 +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
    /**
     * Inserts Data into the database. Mainly used by the AddDialog
     */
    public boolean insertData(String _task, String _date, String _week){

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, _task);
        contentValues.put(COL_3, _date);
        contentValues.put(COL_4, _week);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        } else return true;
    }

    /**
     * Query to get all the weekly tasks from the database and returns them in a cursor.
     * @return Cursor with datasets
     */
    public Cursor getWeekly(){
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where "+ COL_4 + " is not null ", null);
        return res;
    }

    /**
     * Query to get all the "once" tasks from the database and returns them in a cursor.
     * @return Cursor with datasets
     */
    public Cursor getOnce(){
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where "+ COL_4 + " is null ", null);
        return res;
    }

    /**
     * Query to get all the tasks from the database and returns them in a cursor.
     * @return Cursor with datasets
     */
    public Cursor getAllData(){

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);

        return res;
    }

    /**
     * Used to update a whole dataset
     *
     * @param _id   ID of the dataset to be updated
     * @param _task Updated task
     * @param _date Updated date
     * @return true
     */
    public boolean updateData(String _id, String _task, String _date){

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, _id);
        contentValues.put(COL_2, _task);
        contentValues.put(COL_3, _date);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {_id});

        return true;
    }


    /**
     * Updates only the week of a dataset
     *
     * @param _id   Id of the dataset to be updated
     * @param _week Updated week
     * @return true
     */
    public boolean updateWeek(String _id, String _week){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4, _week);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{_id});

        return true;
    }

    /**
     * Deletes a dataset
     *
     * @param id    ID of the dataset to be deleted
     * @return  delete query
     */
    public Integer deleteData(String id){

        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});

    }

    /**
     * Drop the table from the database
     */
    public void drop(){

        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }
}
