package com.mallock.sch_email2;     //get all the details of the email :to, body, subject.

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MailActivity extends Activity {

    EditText edit_body;
    EditText edit_rec;
    EditText edit_subj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.e("TAG", "StartedActivity1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_dets);
        edit_body=(EditText)findViewById(R.id.body);
        edit_rec=(EditText)findViewById(R.id.recp);
        edit_subj=(EditText)findViewById(R.id.subj);
    }
    public static String rec="default";
    public static String body="default";
    public static String subj="default";



    public void clicked(View v){
        //Toast.makeText(this, "I got clicked", Toast.LENGTH_LONG).show();
        rec=edit_rec.getText().toString();
        body=edit_body.getText().toString();
        subj=edit_subj.getText().toString();
        Intent i =new Intent(this,DateTimeActivity.class);
        startActivity(i);
    }
}
