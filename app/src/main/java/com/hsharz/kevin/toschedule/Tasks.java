package com.hsharz.kevin.toschedule;

/**
 * Tasks class to be used by the TaskAdapter and the list for the listview
 */

public class Tasks {
    private String id;
    private String task;
    private String timeToDo;
    private String week;

    public Tasks(String _id, String _task, String _timeToDo, String _week){
        this.id = _id;
        this.task = _task;
        this.timeToDo = _timeToDo;
        this.week = _week;
    }

    public String getId(){
        return id;
    }

    public String getTask(){
        return task;
    }

    public String getTimeToDo(){
        return timeToDo;
    }

    public String getWeek(){ return week; }


}
