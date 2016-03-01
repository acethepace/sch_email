package com.mallock.sch_email2;         //decide the time for when we want to schedule the task
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTimeActivity extends Activity {

    PendingIntent pendingIntent;
    AlarmManager manager;
    private final static String fileName = "Scheduled.txt";
    private TextView mTimeDisplay;
    private TextView mDateDisplay;
    private Button mPickDate;
    private Button mPickTime;
    private long repeat;
    private int mHour;
    private int mMinute;
    private int mYear;
    private int mMonth;
    private int mDay;
    private Spinner spinner;
    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID =1;
    long diff;

    // The callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            updateDisplayTime();
        }
    };

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplayDate();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        spinner=(Spinner)findViewById(R.id.spinner);
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mPickTime = (Button) findViewById(R.id.pickTime);
        mDateDisplay = (TextView) findViewById(R.id.dateDIsplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        // Get the current time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //adapter for the spinner
        String[] array={"NEVER","DAILY","WEEKLY","MONTHLY","YEARLY"};
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view,
                array));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                switch(pos)
                {
                    case 0: repeat=0;
                            break;
                    case 1: repeat=AlarmManager.INTERVAL_DAY;
                        break;
                    case 2: repeat=AlarmManager.INTERVAL_DAY*7;
                        break;
                    case 3: repeat=AlarmManager.INTERVAL_DAY*30;
                        break;
                    case 4: repeat=AlarmManager.INTERVAL_DAY*365;
                        break;
                    default:Log.e("TAG","pos exception");
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        showDialog(TIME_DIALOG_ID);
        showDialog(DATE_DIALOG_ID);
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        mPickDate.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        // Display the current time  &date
        updateDisplayTime();
        updateDisplayDate();
    }

    // Update the time String in the TextView
    private void updateDisplayTime() {
        mTimeDisplay.setText(new StringBuilder().append(pad(mHour)).append(":")
                .append(pad(mMinute)));

    }

    private void updateDisplayDate() {
        mDateDisplay.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(mMonth + 1).append("-").append(mDay).append("-")
                .append(mYear).append(" "));
    }

    public void findDifference(View v){
        final Calendar c = Calendar.getInstance();
        Calendar m= Calendar.getInstance();
        m.set(Calendar.HOUR,mHour);
        m.set(Calendar.MINUTE,mMinute);
        m.set(Calendar.DAY_OF_MONTH, mDay);
        m.set(Calendar.MONTH,mMonth);
        m.set(Calendar.YEAR, mYear);
        m.set(Calendar.AM_PM,Calendar.AM);
        diff=0;
        diff=m.getTimeInMillis()-c.getTimeInMillis();
//        TextView mDifferenceDisplay = (TextView)findViewById(R.id.difference);
//        mDifferenceDisplay.setText(String.valueOf(diff));
        startAlarm(repeat);
    }

    // Prepends a "0" to 1-digit minutes
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);

        }
        return null;
    }

    public void startAlarm(long repeat) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, MailActivity.subj);
        intent.putExtra(Intent.EXTRA_TEXT, MailActivity.body);
        intent.setData(Uri.parse("mailto:".concat(MailActivity.rec)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(repeat==0)
            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diff, pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diff,repeat, pendingIntent);
        Toast.makeText(this, "Alarm Set, repeat= "+repeat, Toast.LENGTH_SHORT).show();
        Intent i= new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        try{
            writeFile();
        }catch(IOException e){
            Log.e("TAG",e.toString());
        }
    }

    private void writeFile() throws FileNotFoundException {
        Log.e("TAG", "reached WriteFile");
        FileOutputStream fos = openFileOutput(fileName, MODE_APPEND);
        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(fos)));
        pw.print(new StringBuilder().append("TO: ").append(MailActivity.rec).append("\n").append(pad(mHour)).append(":").append(pad(mMinute)).append("\t\t\t\t\t").append(pad(mDay)).append("/").append(pad(mMonth)).append("/").append(String.valueOf(mYear)).append("_"));
        Toast.makeText(this,"added",Toast.LENGTH_SHORT).show();
        pw.close();
        Log.e("TAG", "wrote to file");
    }


    public void cancelAlarm(View view) {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }
}