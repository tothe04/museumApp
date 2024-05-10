package com.example.museum;
import static androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.CollectionReference;

public class NotificationHandler {
    private static final String CHANNEL_ID = "museum_notification_channel";
    private final int NOTIFICATION_ID = 0;
    private NotificationManager mManager;
    private Context mContext;
    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "Museum Notification",
                NotificationManager.IMPORTANCE_DEFAULT);

        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Notifications from museum app notification.");
        this.mManager.createNotificationChannel(channel);


    }
    public void send(String message){
        Intent intent = new Intent(mContext, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle("Museum Application")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_museum)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel() {
        this.mManager.cancel(NOTIFICATION_ID);
    }
}
