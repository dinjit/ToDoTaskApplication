package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{
    private ArrayList<TaskObject> taskObjectArrayList;

    // RecyclerView recyclerView;
    public TaskListAdapter(ArrayList<TaskObject> taskObjectArrayList) {
        this.taskObjectArrayList = taskObjectArrayList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.task_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskObject taskObject = taskObjectArrayList.get(position);
        holder.tvTaskName.setText(taskObject.taskName);
        holder.tvTaskStatus.setText(taskObject.taskStatus);
//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return taskObjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTaskName, tvTaskStatus;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTaskName = (TextView) itemView.findViewById(R.id.tvTaskName);
            tvTaskStatus = (TextView) itemView.findViewById(R.id.tvTaskStatus);

//            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
