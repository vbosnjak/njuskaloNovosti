package com.dev.stdev.njuskalonovosti;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class alarmiServis extends IntentService {

    private bReceiver bRec;
    private dbClass db = new dbClass(this);
    private List<alarmClass> alarmiLista;

    public alarmiServis() {
        super("alarmiServis");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            bRec = new bReceiver();
            IntentFilter filter = new IntentFilter(glavnaActivity.MESSAGE_PNA);
            registerReceiver(bRec, filter);
           /* IntentFilter filter1 = new IntentFilter(glavnaActivity.MESSAGE_GAL);
            registerReceiver(bRec, filter1);
            IntentFilter filter2 = new IntentFilter(glavnaActivity.MESSAGE_STPA);
            registerReceiver(bRec, filter2);*/

            String action=intent.getAction();

            Log.d("ACTION",action);

            //db = new dbClass(this);
            //if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_PA)) //pokreni postojeće alarme nakon pokretanja aplikacije
            //{
               //pokreni alarme nakon starta
                //pokreniAlarme();
           /* }
            else if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_PNA)) //pokreni novi alarm
            {
               String alDat = intent.getStringExtra(glavnaActivity.MESSAGE_PNA);
               pokreniNoviAlarm(alDat);

            }
            else if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_GAL)) //prikaži alarme
            {

                //prikazi alarme
               prikazialarme();
            }
            else if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_STPA)) //zaustavi alarm
            {

                //zaustavi alarm
                String ala = intent.getStringExtra(glavnaActivity.MESSAGE_STPA);
                zaustaviAlarm(ala);
            }*/
            //String pokal = intent.getStringExtra(glavnaActivity.MESSAGE_PA);

        }
    }




    private class bReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();

            Log.d("ACTION",action);

            /*if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_PA)) //pokreni postojeće alarme nakon pokretanja aplikacije
            {
                //pokreni alarme nakon starta
                pokreniAlarme();
            }
            else*/
            if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_PNA)) //pokreni novi alarm
            {
                String alDat = intent.getStringExtra(glavnaActivity.MESSAGE_PNA);
                pokreniNoviAlarm(alDat);

            }
            else if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_GAL)) //prikaži alarme
            {

                //prikazi alarme
                prikazialarme();
            }
            else if(action.equalsIgnoreCase(glavnaActivity.MESSAGE_STPA)) //zaustavi alarm
            {

                //zaustavi alarm
                String ala = intent.getStringExtra(glavnaActivity.MESSAGE_STPA);
                zaustaviAlarm(ala);
            }

        }
    }


    public void zaustaviAlarm(String al)
    {

        //delete alarm and its dependencies from database
        db.deleteAlarm(al);
        db.deleteApartment(al);
        db.deletePretragaByGenId(al);

        int alarmidn = Integer.parseInt(al);

        Intent intent = new Intent(this, alarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmidn, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        //---------------refresh alarm list in listaAlarmaactivity---------------
        prikazialarme();
        //-------------------------------------------------------------------
    }


    public void pokreniNoviAlarm(String alarmdata)
    {

        String[] parts = alarmdata.split("##");
        alarmClass alc = new alarmClass();
        pretrageClass ptr = new pretrageClass();

        String pretraga = parts[0];
        String intervl = parts[1];
        String tipPretrage = "1";

        alc.setInterval(intervl);
        ptr.setPretraga(pretraga);
        ptr.setTip(tipPretrage);

        int newGeneralId;

        if(db.isPretrageTableEmpty()==false)
        {


                List<pretrageClass> lP = db.getAllPretrage();

                pretrageClass prTm;

                prTm = lP.get(lP.size()-1);//take last an generate +1 id for new one
                newGeneralId = Integer.parseInt(prTm.getGeneralId()) + 1; //zadnji plus 1 je id za novu pretragu

                ptr.setGeneralId(Integer.toString(newGeneralId));
                db.addPretraga(ptr);

                alc.setGeneralid(Integer.toString(newGeneralId));
                db.addAlarm(alc);

                //dodaj novu pretragu - geerirajnovi general id, pretraži stanove na temelju novog id-a, nema ih naravno jer je nova pretraga, ddaj fld.isnew..



        }
        else //Pretrage table is empty
        {
            newGeneralId = 1000; //set initial generalid to 1000

            //dohvati zadnju pretragu jer ima

            ptr.setGeneralId(Integer.toString(newGeneralId));
            db.addPretraga(ptr);

            alc.setGeneralid(Integer.toString(newGeneralId));
            db.addAlarm(alc);

        }

        //create new alarm
        Intent intent = new Intent(this, alarmReceiver.class);
        PendingIntent pi = PendingIntent.getActivity(this, newGeneralId, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, (Integer.parseInt(intervl) * 1000) ,pi);

        //---------------show new alarm/refresh alarm list in listaAlarmaactivity---------------
        prikazialarme();
        //-------------------------------------------------------------------

    }

    public void pokreniAlarme()
    {

        List<alarmClass> al = db.getAllAlarms();

        for(int i=0; i<al.size(); i++)
        {

            alarmClass alr = al.get(i);
            int intrvl = 1000 * Integer.parseInt(alr.getInterval()); //interval is in seconds in database but alarm demands miliseconds
            int alarmid = Integer.parseInt(alr.getGeneralid());

            Intent intent = new Intent(this, alarmReceiver.class);
            PendingIntent pi = PendingIntent.getActivity(this, alarmid, intent, 0);
            AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, intrvl ,pi);

        }

    }


    public void prikazialarme()
    {

        alarmiLista = db.getAllAlarms();

        for(int i=0; i<alarmiLista.size(); i++)
        {
            alarmClass al = alarmiLista.get(i);
            List<pretrageClass> p = db.getPretragaByGenID(al.getGeneralid()); //only one in list
            al.setPretraga(p.get(0).getPretraga()); //we are doing this so that pretraga string can be shown in activity
            sendBroadcastMessage(glavnaActivity.MESSAGE_RGAL, al);
        }

    }

    private void sendBroadcastMessage(String intentFilterName, alarmClass al) {

        //Log.d("Šaljem Intent","Šaljem Intent");

        Intent intent = new Intent(intentFilterName);
        intent.setAction(intentFilterName);
        intent.putExtra(intentFilterName, al);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bRec);
    }


}
