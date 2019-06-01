package com.example.myapplication;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView btnAddTask;
    RecyclerView rvTaskList;
    ArrayList<TaskObject> taskObjectArrayList;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddTask = findViewById(R.id.btnAddTask);
        rvTaskList = findViewById(R.id.rvTaskList);
        taskObjectArrayList = new ArrayList<>();
        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("TaskList");

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = databaseReference.push().getKey();

                TaskObject taskObject = new TaskObject();
                taskObject.id = id;
                taskObject.comment = "comment";
                taskObject.taskStatus = "complete";
                taskObject.taskName = "Name";
                databaseReference.child(id).setValue(taskObject);

//                        // Create custom dialog object
//                        final Dialog dialog = new Dialog(MainActivity.this);
//                        // Include dialog.xml file
//                        dialog.setContentView(R.layout.add_task_dialog);
//                        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
//                        image.setImageResource(R.drawable.image0);
//
//                        dialog.show();
//
//                        Button btnSaveButton = (Button) dialog.findViewById(R.id.btnSaveButton);
//                        // if decline button is clicked, close the custom dialog
//                btnSaveButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // Close dialog
//                                dialog.dismiss();
//                            }
//                        });

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskObjectArrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TaskObject taskObject = dataSnapshot1.getValue(TaskObject.class);
                    taskObjectArrayList.add(taskObject);
                }

                TaskListAdapter taskListAdapter = new TaskListAdapter(taskObjectArrayList);

                rvTaskList.setHasFixedSize(true);
                rvTaskList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                rvTaskList.setAdapter(taskListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
