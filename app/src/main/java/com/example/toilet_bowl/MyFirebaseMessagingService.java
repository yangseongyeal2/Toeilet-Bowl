package com.example.toilet_bowl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.LogPrinter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {//메시지가 왔을떄
        super.onMessageReceived(remoteMessage);
        Toast.makeText(this,"메시지받음",Toast.LENGTH_LONG).show();
        String msg,title;

        msg=remoteMessage.getNotification().getBody();
        title=remoteMessage.getNotification().getTitle();
        Notification.Builder noti=new Notification.Builder(this)
                .setContentTitle("New Push from"+title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,noti.build());


    }
}
