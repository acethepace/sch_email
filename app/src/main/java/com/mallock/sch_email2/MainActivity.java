package com.mallock.sch_email2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final static String fileName = "Scheduled.txt";
    private String TAG = "InternalFileWriteReadActivity";
    ListView lv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_of_alarms);
        lv=(ListView)findViewById(R.id.listView);
        // Read the data from the text file and display it
        readFile(this.findViewById(R.id.listView));
    }

    private void writeFile() throws FileNotFoundException {

        FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);

        PrintWriter pw = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(fos)));

        pw.println("Line 1: This is a test of the File Writing API");


        pw.close();

    }

    public void readingFile(ListView lv) throws IOException {
        FileInputStream fis = openFileInput(fileName);
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        //Toast.makeText(this, "reading file", Toast.LENGTH_SHORT).show();
        String lines = "";
        while (null != (line = br.readLine())) {
            if (line.length() > 0 && line.charAt(line.length() - 1) != '_') {
                line = line.concat("\n");
            }
            lines = lines.concat(line);
        }
        String[] linesArray = lines.split("_");
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_view, linesArray);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        br.close();
    }

    public void readFile(View v){
        //Log.e("TAG","trying to read file now.");
        try{
            readingFile(lv);
        }catch(Exception e){
            Log.e("TAG",e.toString());
        }
    }

    public void AddData(View view)
    {
        Intent i= new Intent(this,MailActivity.class);
        startActivity(i);
    }
}
