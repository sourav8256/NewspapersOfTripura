package in.springpebbles.newspapertripura;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Sourav on 07/07/2017.
 */

public class boot_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadcastIntent = new Intent(context,notification_receiver.class);
        broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY,6);
        calendar.set(java.util.Calendar.MINUTE,30);
        calendar.set(java.util.Calendar.SECOND,0);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,123,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


        Toast.makeText(context,"Alarm Service SET",Toast.LENGTH_LONG).show();
    }
}
