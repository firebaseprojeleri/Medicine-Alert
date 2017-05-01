package com.vne.medicinealert;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vne.medicinealert.Modal.Medicine;
import com.vne.medicinealert.Service.NotificationPublisher;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Volkan Şahin on 19.04.2017.
 */

public class AddMedicineActivity extends AppCompatActivity{
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserMedicinesReference;

    EditText edtMedicineName;
    TimePicker timePicker;

    String currentUserId;
    int tpHour;
    int tpMinute;

    static int notifId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        Intent intent = getIntent();
        currentUserId = intent.getExtras().getString("userid");

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        edtMedicineName = (EditText) findViewById(R.id.edtMedicineName);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mUserMedicinesReference = mDatabaseReference.child("medicines").child(currentUserId);
    }

    protected void btnSaveClick(View v){
        Medicine m = new Medicine();
        m.setMedicineName(edtMedicineName.getText().toString());
        int tpHour = timePicker.getCurrentHour();
        int tpMinute = timePicker.getCurrentMinute();

        String formattedHour = "", formattedMinute = "";

        if(tpHour < 10){
            formattedHour = "0" + tpHour;
        } else{
            formattedHour = String.valueOf(tpHour);
        }

        if(tpMinute < 10){
            formattedMinute = "0" + tpMinute;
        } else {
          formattedMinute = String.valueOf(tpMinute);
        }

        m.setMedicineTime(formattedHour+ ":" + formattedMinute);
        mUserMedicinesReference.push().setValue(m);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, tpHour);
        calendar.set(Calendar.MINUTE, tpMinute);

        long delay = calendar.getTimeInMillis() - System.currentTimeMillis();
        scheduleNotification(getNotification(m.getMedicineName() + " ilacının zamanı geldi"), delay);
        finish();

    }

    private void scheduleNotification(Notification notification, long delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notifId + 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notifId + 1, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notifId++;
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("İlaç Zamanı");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.notiflogo);
        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        return builder.build();
    }
}
