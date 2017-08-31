package in.springpebbles.newspapertripura;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //notification("Good Morning","Good Morning, Your newspapers are ready...");


        Intent i = new Intent(getApplicationContext(),newspaperlist.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent broadcastIntent = new Intent(getApplicationContext(),notification_receiver.class);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY,6);
        calendar.set(java.util.Calendar.MINUTE,30);
        calendar.set(java.util.Calendar.SECOND,0);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,123,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


        //Toast.makeText(getApplicationContext(),"Alarm Service SET",Toast.LENGTH_LONG).show();

        startActivity(i);
    }


}
