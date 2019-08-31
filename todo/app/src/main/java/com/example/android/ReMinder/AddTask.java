package com.example.android.ReMinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TaskDBHelper mydb;
    private DatePickerDialog dpd;
    private int startYear = 0, startMonth = 0, startDay = 0;
    private String dateFinal;
    private String nameFinal;
    private String descFinal;
    private String messageBody;
    private String phoneNumFinal;
    private String emailFinal;
    private Intent intent;
    private Boolean isUpdate;
    private String id;
    private CheckBox check0, check1, check2, check3;
    private int checked0, checked1, checked2, checked3;
    private Button btnDelete;
    private static final int REQUEST_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_add_new);

        mydb = new TaskDBHelper(getApplicationContext());
        intent = getIntent();
        isUpdate = intent.getBooleanExtra("isUpdate", false);

        dateFinal = todayDateString();
        Date your_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(your_date);
        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH);
        startDay = cal.get(Calendar.DAY_OF_MONTH);
        Cursor task = mydb.getDataSpecific(id);
        EditText task_phone = (EditText) findViewById(R.id.task_phone);
        EditText task_email = (EditText) findViewById(R.id.task_email);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
            if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                    showMessageOKCancel("You need to allow access to Send SMS",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                                REQUEST_SMS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                        REQUEST_SMS);
                return;
            }
        }
        if(task.getString(10).toString() != null){
            task_phone.setText(task.getString(8).toString());
        }
        if(task.getString(11).toString() != null) {
            task_email.setText(task.getString(9).toString());
        }
        if(task.getString(10).toString() == null && task.getString(11).toString() == null){
            defaultPrompt();
        }
        if (isUpdate) {
            init_update();
        }
    }

    public void defaultPrompt(){

    }

    public void init_update() {
        id = intent.getStringExtra("id");
        TextView toolbar_task_add_title = (TextView) findViewById(R.id.toolbar_task_add_title);
        EditText task_name = (EditText) findViewById(R.id.task_name);
        EditText task_date = (EditText) findViewById(R.id.task_date);
        EditText task_desc = (EditText) findViewById(R.id.task_desc);
        EditText task_phone = (EditText) findViewById(R.id.task_phone);
        EditText task_email = (EditText) findViewById(R.id.task_email);

        check0 = (CheckBox) findViewById(R.id.checkBox0);
        check1 = (CheckBox) findViewById(R.id.checkBox1);
        check2 = (CheckBox) findViewById(R.id.checkBox2);
        check3 = (CheckBox) findViewById(R.id.checkBox3);

        toolbar_task_add_title.setText("Update");
        Cursor task = mydb.getDataSpecific(id);
        if (task != null) {
            task.moveToFirst();

            task_name.setText(task.getString(1).toString());
            Calendar cal = Function.Epoch2Calender(task.getString(2).toString());
            startYear = cal.get(Calendar.YEAR);
            startMonth = cal.get(Calendar.MONTH);
            startDay = cal.get(Calendar.DAY_OF_MONTH);
            task_date.setText(Function.Epoch2DateString(task.getString(2).toString(), "dd/MM/yyyy"));
            task_desc.setText(task.getString(3).toString());
            task_phone.setText(task.getString(8).toString());
            task_email.setText(task.getString(9).toString());

            if(task.getInt(4) == 1){
                check0.setChecked(true);
                if(task.getInt(5) == 1)
                    check1.setChecked(true);
                if(task.getInt(6) == 1)
                    check2.setChecked(true);
                if(task.getInt(7) == 1)
                    check3.setChecked(true);
            }
            btnDelete = (Button) findViewById(R.id.button2);
            btnDelete.setEnabled(true);
            btnDelete.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            AlertDialog.Builder build = new AlertDialog.Builder(AddTask.this);
                            build.setMessage("Are you sure you want to delete this task?");
                            build.setCancelable(true);

                            build.setPositiveButton(
                                    "Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id1) {
                                            Integer deletedRows = mydb.deleteData(id);
                                            if(deletedRows > 0) {
                                                Toast.makeText(AddTask.this, "Data Deleted", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                            else
                                                Toast.makeText(AddTask.this, "Data not Deleted", Toast.LENGTH_LONG).show();
                                            dialog.cancel();
                                        }
                                    });
                            build.setNegativeButton(
                                    "No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert1 = build.create();
                            alert1.show();
                        }
                    }
            );
        }
    }

    public String todayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());

        return dateFormat.toString();
    }

    public void closeAddTask(View v) {
        finish();
    }

    public void doneAddTask(View v) {
        int errorStep = 0;
        EditText task_name = (EditText) findViewById(R.id.task_name);
        EditText task_date = (EditText) findViewById(R.id.task_date);
        EditText task_desc = (EditText) findViewById(R.id.task_desc);
        EditText task_phone = (EditText) findViewById(R.id.task_phone);
        EditText task_email = (EditText) findViewById(R.id.task_email);

        CheckBox task_check = (CheckBox) findViewById(R.id.checkBox0);
        nameFinal = task_name.getText().toString();
        descFinal = task_desc.getText().toString();
        dateFinal = task_date.getText().toString();
        phoneNumFinal = task_phone.getText().toString();
        emailFinal = task_email.getText().toString();
        check0 = (CheckBox) findViewById(R.id.checkBox0);

        addListenerOnButton();

        if (nameFinal.trim().length() < 1) {
            errorStep++;
            task_name.setError("Provide a task name.");
        }

        if (descFinal.trim().length() < 1){
            errorStep++;
            task_desc.setError("Provide a description.");
        }
        if (dateFinal.trim().length() < 4) {
            errorStep++;
            task_date.setError("Provide a specific date.");
        }

        if (check0.isChecked() && !check1.isChecked() && !check2.isChecked() && !check3.isChecked()){
            errorStep++;
            Toast.makeText(getApplicationContext(), "You need to pick a medium to receive a reminder.", Toast.LENGTH_SHORT).show();
        }

        if(check0.isChecked() && check1.isChecked() || check0.isChecked() && check3.isChecked()) {
            if (phoneNumFinal.trim().length() < 10) {
                errorStep++;
                Toast.makeText(getApplicationContext(), "Provide an phone number.", Toast.LENGTH_SHORT).show();
            }
        }
        if(check0.isChecked() && check2.isChecked()) {
            if (emailFinal.trim().length() < 8) {
                errorStep++;
                Toast.makeText(getApplicationContext(), "Provide an email.", Toast.LENGTH_SHORT).show();
            }
        }

        if (errorStep == 0) {
            if(check0.isChecked()) {
                checked0 = 1;
                if(check1.isChecked())
                    checked1 = 1;
                if(check2.isChecked())
                    checked2 = 1;
                if(check3.isChecked())
                    checked3 = 1;
            }
            if (isUpdate) {
                mydb.updateContact(id, nameFinal, dateFinal, descFinal, checked0, checked1, checked2, checked3, phoneNumFinal, emailFinal);
                Toast.makeText(getApplicationContext(), "Task Updated.", Toast.LENGTH_SHORT).show();
            } else {
                mydb.insertContact(nameFinal, dateFinal, descFinal, checked0, checked1, checked2, checked3, phoneNumFinal, emailFinal);
                Toast.makeText(getApplicationContext(), "Task Added.", Toast.LENGTH_SHORT).show();
            }
            if(check0.isChecked()) {
                messageBody = "Task: " + nameFinal + " on " + dateFinal + ".\nDescription :\n" + descFinal + "\nPhone; " + phoneNumFinal;
                if(check1.isChecked()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("17076910799", null, messageBody, null, null);
                }
                if(check2.isChecked()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("17076910799", null, "Email; " + emailFinal, null, null);
                }
                if(check3.isChecked()) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("17076910799", null, "Phone call; " + phoneNumFinal, null, null);
                }
            }

            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("startDatepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        int monthAddOne = startMonth + 1;
        String date = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;
        EditText task_date = (EditText) findViewById(R.id.task_date);
        task_date.setText(date);
    }

    public void showStartDatePicker(View v) {
        dpd = DatePickerDialog.newInstance(AddTask.this, startYear, startMonth, startDay);
        dpd.setOnDateSetListener(this);
        dpd.show(getFragmentManager(), "startDatepickerdialog");
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(AddTask.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void addListenerOnButton(){
        check1 = (CheckBox) findViewById(R.id.checkBox1);
        check2 = (CheckBox) findViewById(R.id.checkBox2);
        check3 = (CheckBox) findViewById(R.id.checkBox3);

        check1.setEnabled(false);
        check2.setEnabled(false);
        check3.setEnabled(false);

        if(check0.isChecked()){
            StringBuffer result = new StringBuffer();
            check1.setEnabled(true);
            check2.setEnabled(true);
            check3.setEnabled(true);
            result.append("Text: ").append(check1.isChecked());
            result.append("Email: ").append(check2.isChecked());
            result.append("Phone call: ").append(check3.isChecked());
            Toast.makeText(AddTask.this, result.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    public void onDelete(){
        btnDelete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Integer deletedRows = mydb.deleteData(id);
                        if(deletedRows > 0)
                            Toast.makeText(AddTask.this, "Data Deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(AddTask.this, "Data not Deleted", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}