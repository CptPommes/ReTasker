package com.hsharz.kevin.toschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for the listview that shows the tasks in MainActivity
 */

public class TaskAdapter extends BaseAdapter {

    static class ViewHolder{

        TextView task;
        TextView timeToDo;
    }

    private ArrayList<Tasks> liste = null;
    private LayoutInflater layoutInflater;
    private Context context;

    public TaskAdapter(Context _context, ArrayList<Tasks> _liste ){
        this.liste = _liste;
        layoutInflater = LayoutInflater.from(_context);
        this.context = _context;
    }

    @Override
    public int getCount() {
        return liste.size();
    }

    @Override
    public Object getItem(int position) {
        return liste.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.listview_item_layout, null);
            holder = new ViewHolder();


            holder.task = (TextView) convertView.findViewById(R.id.taskName);
            holder.timeToDo = (TextView) convertView.findViewById(R.id.timeToDo);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.task.setText(liste.get(position).getTask());
        holder.timeToDo.setText(liste.get(position).getTimeToDo());

        return convertView;
    }
}
