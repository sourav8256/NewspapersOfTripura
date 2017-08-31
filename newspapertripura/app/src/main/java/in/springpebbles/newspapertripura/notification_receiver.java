package in.springpebbles.newspapertripura;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.renderscript.RenderScript;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Sourav on 04/07/2017.
 */

public class notification_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        notification(context,"Good Morning","Good Morning, Have you checked today's newspaper");

    }


    public void   notification(Context context,String title, String text){


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if(alarmSound == null){
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }


        Intent i = new Intent(context,newspaperlist.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,125489633,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setAutoCancel(true);

        if(Build.VERSION.SDK_INT >= 16){
            builder.setPriority(Notification.PRIORITY_MAX);
        }

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        if(hour < 10) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(123, builder.build());
        }
    }

}
