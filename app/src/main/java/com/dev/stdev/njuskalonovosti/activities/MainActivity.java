package com.dev.stdev.njuskalonovosti.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.dev.stdev.njuskalonovosti.R;
import com.dev.stdev.njuskalonovosti.models.SearchClass;
import com.dev.stdev.njuskalonovosti.services.DeleteSearchService;
import com.dev.stdev.njuskalonovosti.services.GetAllSearchService;
import com.dev.stdev.njuskalonovosti.services.StartAlarmsAfterAppStartService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final static String MESSAGE_GD = "MESSAGE_GD";
    public final static String MESSAGE_GK = "MESSAGE_GK";
    public final static String MESSAGE_GS = "MESSAGE_GS";
    public final static String MESSAGE_GA = "MESSAGE_GA";
    public final static String MESSAGE_GAL = "MESSAGE_GAL";
    public final static String MESSAGE_RGAL = "MESSAGE_RGAL";
    public final static String MESSAGE_BS = "MESSAGE_BS";
    //public final static String MESSAGE_PA = "MESSAGE_PA";
    public final static String MESSAGE_PNA = "MESSAGE_PNA";
    public final static String MESSAGE_STRA = "MESSAGE_STRA";
    public final static String MESSAGE_STPA = "MESSAGE_STPA";
    public final static String MESSAGE_ALARM = "MESSAGE_ALARM";

    private bReceiver bRec;

    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bRec = new MainActivity.bReceiver();
        IntentFilter filter = new IntentFilter("PRETRAGE_RESP");
        registerReceiver(bRec,filter);


        //Start GET ALL SEARCHES Service
        Intent srvc = new Intent(this, GetAllSearchService.class);
        srvc.putExtra("PRETRAGA",MESSAGE_GS);
        startService((srvc));


        //Start ALARM START Service
        Intent srva = new Intent(this, StartAlarmsAfterAppStartService.class);
        startService((srva));


        //------------------spinner select value------------------------------------
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                TextView tv = (TextView) findViewById(R.id.messageEdit);
                tv.setText(item);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }

        });
        //--------------------------------------------------------------------------------



    }



    //private BroadcastReceiver bReceiver = new BroadcastReceiver() {
   private class bReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            SearchClass prt = (SearchClass) intent.getSerializableExtra("PRETRAGA_OBJ");

            //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainLayout);

            Spinner sp = (Spinner) findViewById(R.id.spinner);

            //not alarm pretraga, 0-not alarm, 1 yes alarm
            if(prt.getType().equals("0")) {

                ArrayAdapter<String> adapter;

                list.add(prt.getSearch()); //prt.getGeneralId()+","+prt.getSearch()
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adapter);
            }

        }
    }




    /** Called when the user clicks the Dohvati button */
    public void getFlatsAdvertisments(View view) {
        // Do something in response to button

        EditText editText = (EditText) findViewById(R.id.messageEdit);
        String message = editText.getText().toString();

        //Log.d("PORUKALINK",message);
        //search string must not be empty
        if (!(message.equals(""))) {
            Intent intent = new Intent(this, SearchNewFlatAdvertisementsActivity.class);
            intent.putExtra(MESSAGE_GD, message);
            startActivity(intent);
        }

    }


    public void deleteSearch(View view)
    {

        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String text = spinner.getSelectedItem().toString(); //getFlatsAdvertisments oznacenu pretragu

        if (!(text.equals(""))) {

            ArrayAdapter<String> adapter;

            list.clear();
            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            //Start Service
            Intent srvc = new Intent(this, DeleteSearchService.class);
            srvc.putExtra(MESSAGE_BS, text);
            startService((srvc));
        }

    }



    public void configure(View view) {
        // Do something in response to button

        EditText editText = (EditText) findViewById(R.id.messageEdit);
        String message = editText.getText().toString();

        //search string must not be empty
        if(!(message.equals(""))) {
            Intent intent = new Intent(this, AlarmConfigurationActivity.class);
            intent.putExtra(MESSAGE_GK, message);
            startActivity(intent);
        }

    }

    public void alarmList(View view) {

            Intent intent = new Intent(this, AlarmListActivity.class);
            intent.setAction(MESSAGE_GA);
            intent.putExtra(MESSAGE_GA, MESSAGE_GA);
            startActivity(intent);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bRec);
    }



}
