package com.example.android.ReMinder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskHome extends AppCompatActivity {

    private Activity activity;
    private TaskDBHelper mydb;
    private NoScrollListView taskListToday, taskListTomorrow, taskListUpcoming;
    private NestedScrollView scrollView;
    private ProgressBar loader;
    private TextView todayText, tomorrowText, upcomingText;
    private ArrayList<HashMap<String, String>> todayList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> tomorrowList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> upcomingList = new ArrayList<HashMap<String, String>>();

    public static String KEY_ID = "id";
    public static String KEY_TASK = "task";
    public static String KEY_DESC = "desc";
    public static String KEY_DATE = "date";
    public static String KEY_PHONE = "phone";
    public static String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_home);

        activity = TaskHome.this;
        mydb = new TaskDBHelper(activity);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        loader = (ProgressBar) findViewById(R.id.loader);
        taskListToday = (NoScrollListView) findViewById(R.id.taskListToday);
        taskListTomorrow = (NoScrollListView) findViewById(R.id.taskListTomorrow);
        taskListUpcoming = (NoScrollListView) findViewById(R.id.taskListUpcoming);

        todayText = (TextView) findViewById(R.id.todayText);
        tomorrowText = (TextView) findViewById(R.id.tomorrowText);
        upcomingText = (TextView) findViewById(R.id.upcomingText);
    }

    public void openAddTask(View v) {
        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    public void populateData() {
        mydb = new TaskDBHelper(activity);
        scrollView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        LoadTask loadTask = new LoadTask();
        loadTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateData();

    }

    class LoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            todayList.clear();
            tomorrowList.clear();
            upcomingList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            Cursor today = mydb.getDataToday();
            loadDataList(today, todayList);

            Cursor tomorrow = mydb.getDataTomorrow();
            loadDataList(tomorrow, tomorrowList);

            Cursor upcoming = mydb.getDataUpcoming();
            loadDataList(upcoming, upcomingList);

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            loadListView(taskListToday, todayList);
            loadListView(taskListTomorrow, tomorrowList);
            loadListView(taskListUpcoming, upcomingList);


            if (todayList.size() > 0) {
                todayText.setVisibility(View.VISIBLE);
            } else {
                todayText.setVisibility(View.GONE);
            }

            if (tomorrowList.size() > 0) {
                tomorrowText.setVisibility(View.VISIBLE);
            } else {
                tomorrowText.setVisibility(View.GONE);
            }

            if (upcomingList.size() > 0) {
                upcomingText.setVisibility(View.VISIBLE);
            } else {
                upcomingText.setVisibility(View.GONE);
            }


            loader.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }


    public void loadDataList(Cursor cursor, ArrayList<HashMap<String, String>> dataList) {
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                HashMap<String, String> mapToday = new HashMap<String, String>();
                mapToday.put(KEY_ID, cursor.getString(0).toString());
                mapToday.put(KEY_TASK, cursor.getString(1).toString());
                mapToday.put(KEY_DATE, Function.Epoch2DateString(cursor.getString(2).toString(), "dd-MM-yyyy"));
                mapToday.put(KEY_DESC, cursor.getString(3).toString());
                mapToday.put(KEY_PHONE, cursor.getString(7).toString());
                mapToday.put(KEY_EMAIL, cursor.getString(8).toString());
                dataList.add(mapToday);
                cursor.moveToNext();
            }
        }
    }

    public void loadListView(ListView listView, final ArrayList<HashMap<String, String>> dataList) {
        ListTaskAdapter adapter = new ListTaskAdapter(activity, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(activity, AddTask.class);
                i.putExtra("isUpdate", true);
                i.putExtra("id", dataList.get(+position).get(KEY_ID));

                startActivity(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String d = dataList.get(+position).get(KEY_DESC);

                openDialog(d);
                return true;
            }
        });
    }

    public void openDialog(String d) {
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this);

        builderSingle.setTitle("Task Description").setMessage(d).show();

        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){

            }
        });
    }
}
