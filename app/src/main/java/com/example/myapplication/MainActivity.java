package com.example.myapplication;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.MonthAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TaskListAdapter.IOnRowClickListener {
    TextView btnAddTask;
    RecyclerView rvTaskList;
    ArrayList<TaskObject> taskObjectArrayList;
    TextView tvTaskStartTime;
    DatabaseReference databaseReference;
    String arrivalDate;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddTask = findViewById(R.id.btnAddTask);
        rvTaskList = findViewById(R.id.rvTaskList);
        taskObjectArrayList = new ArrayList<>();
        getSupportActionBar().hide();
        if (util.isNetworkAvailable(MainActivity.this)) {
            databaseReference = FirebaseDatabase.getInstance().getReference("TaskList");
        } else {
            util.showOKAlert(MainActivity.this, "Network Not Available");
        }


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTaskInfoDialog(null);


            }
        });
    }

    private void setTaskAdapter() {
        TaskListAdapter taskListAdapter = new TaskListAdapter(MainActivity.this, taskObjectArrayList);

        rvTaskList.setHasFixedSize(true);
        rvTaskList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        rvTaskList.setAdapter(taskListAdapter);
        taskListAdapter.setListener(this);
    }

    public void showTaskInfoDialog(final TaskObject taskObject) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_task_dialog);
        String[] taskStatus = {"Yet To Start", "Pending", "Completed"};

        final EditText etTaskName = (EditText) dialog.findViewById(R.id.etTaskName);
        final EditText etComment = (EditText) dialog.findViewById(R.id.etComment);
        TextView btnAddTask = (TextView) dialog.findViewById(R.id.btnSaveTask);

        final Spinner spnTaskStatus = (Spinner) dialog.findViewById(R.id.spnTaskStatus);
        ArrayAdapter taskStatusAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, taskStatus);
        taskStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTaskStatus.setAdapter(taskStatusAdapter);

        tvTaskStartTime = (TextView) dialog.findViewById(R.id.tvTaskStartTime);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(myCalendar.getTime());
        tvTaskStartTime.setText(formattedDate);

        dialog.show();

        if (taskObject != null) {
            if (taskObject.taskName != null) {
                etTaskName.setText(taskObject.taskName);
            }
            if (taskObject.comment != null) {
                etComment.setText(taskObject.comment);
            }
            if (taskObject.taskStatusId != null) {
                spnTaskStatus.setSelection(Integer.parseInt(taskObject.taskStatusId));
            }
            tvTaskStartTime.setText(taskObject.taskStartTime);
        }


        tvTaskStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePickerDialog = DatePickerDialog.newInstance(MainActivity.this,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                );
                mDatePickerDialog.setYearRange(2000, 2036);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis() - 1000);
                mDatePickerDialog.setMaxDate(c);
                mDatePickerDialog.setAccentColor(getResources().getColor(R.color.theme_color));
                mDatePickerDialog.show(getFragmentManager(), "");
                Calendar d = Calendar.getInstance();
                d.setTimeInMillis(System.currentTimeMillis() - 7776000000L); //the long value is the time in milliseconds of 90days
                mDatePickerDialog.setMinDate(d);
                MonthAdapter.CalendarDay selectedDate = mDatePickerDialog.getSelectedDay();
            }
        });


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TaskObject taskObject1 = new TaskObject();
                taskObject1.comment = etComment.getText().toString();
                taskObject1.taskStatus = spnTaskStatus.getSelectedItem().toString();
                taskObject1.taskStatusId = "" + spnTaskStatus.getSelectedItemPosition();
                taskObject1.taskName = etTaskName.getText().toString();

                taskObject1.taskStartTime = tvTaskStartTime.getText().toString();
                if (etTaskName.getText().toString().trim().length()>0) {
                    if (util.isNetworkAvailable(MainActivity.this)) {
                        if (taskObject != null && taskObject.id != null) {
                            taskObject1.id = taskObject.id;
                            databaseReference.child(taskObject1.id).setValue(taskObject1);
                        } else {
                            String id = databaseReference.push().getKey();
                            taskObject1.id = id;
                            databaseReference.child(id).setValue(taskObject1);
                        }
                    } else {
                        util.showOKAlert(MainActivity.this, "Network Not Available");
                    }
                    dialog.dismiss();
                }else {
                    util.showOKAlert(MainActivity.this, "Enter Task Name");
                }



            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (util.isNetworkAvailable(MainActivity.this)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    taskObjectArrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        TaskObject taskObject = dataSnapshot1.getValue(TaskObject.class);
                        taskObjectArrayList.add(taskObject);
                    }

                    setTaskAdapter();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        arrivalDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        tvTaskStartTime.setText(arrivalDate);
    }

    @Override
    public void onEditClicked(TaskObject taskObject) {
        showTaskInfoDialog(taskObject);
    }

    @Override
    public void onDeleteClicked(String taskId) {
        databaseReference.child(taskId).removeValue();
    }


}
