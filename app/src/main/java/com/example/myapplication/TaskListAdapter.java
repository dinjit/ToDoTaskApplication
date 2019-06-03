package com.example.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>{
    private ArrayList<TaskObject> taskObjectArrayList;
    Context context;
    IOnRowClickListener mlistener;    public TaskListAdapter(Context context,ArrayList<TaskObject> taskObjectArrayList) {
        this.taskObjectArrayList = taskObjectArrayList;
        this.context=context;
    }
    public void setListener(IOnRowClickListener listener) {
        this.mlistener = listener;
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
        holder.ivEditTask.setTag(taskObject);
        holder.ivdeleteTask.setTag(taskObject.id);

        holder.ivEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskObject taskObject1 = (TaskObject) view.getTag();
                mlistener.onEditClicked(taskObject1);
            }
        });

        holder.ivdeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskId = (String) v.getTag();
                mlistener.onDeleteClicked(taskId);
            }
        });
        if(taskObject.taskStatusId!=null) {
            if (taskObject.taskStatusId.equalsIgnoreCase("0")) {
                holder.tvTaskStatus.setBackgroundColor(context.getResources().getColor(R.color.dark_grey));
            } else if (taskObject.taskStatusId.equalsIgnoreCase("1")) {
                holder.tvTaskStatus.setBackgroundColor(context.getResources().getColor(R.color.pending));
            } else if (taskObject.taskStatusId.equalsIgnoreCase("2")) {
                holder.tvTaskStatus.setBackgroundColor(context.getResources().getColor(R.color.completed));
            }
        }
    }


    @Override
    public int getItemCount() {
        return taskObjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTaskName, tvTaskStatus;
        public RelativeLayout relativeLayout;
        public ImageView ivEditTask,ivdeleteTask;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTaskName = (TextView) itemView.findViewById(R.id.tvTaskName);
            tvTaskStatus = (TextView) itemView.findViewById(R.id.tvTaskStatus);
            ivdeleteTask = (ImageView) itemView.findViewById(R.id.ivdeleteTask);
            ivEditTask = (ImageView)itemView.findViewById(R.id.ivEditTask);
        }
    }

    public interface IOnRowClickListener {
        void onEditClicked(TaskObject taskObject);
        void onDeleteClicked(String taskId);
    }
}
