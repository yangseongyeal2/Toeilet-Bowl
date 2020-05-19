package com.example.toilet_bowl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.LogPrinter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {//메시지가 왔을떄
        super.onMessageReceived(remoteMessage);
            original_receive(remoteMessage);
    }

    private void original_receive(RemoteMessage remoteMessage) {
        //        Toast.makeText(this,"메시지받음",Toast.LENGTH_LONG).show();
        String msg,mtitle;
        msg=remoteMessage.getNotification().getBody();
        mtitle=remoteMessage.getNotification().getTitle();

        PendingIntent pendingIntent = null;
        Intent intent = new Intent(this, BoardActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        //푸쉬알람기능 만드는법
        Notification.Builder noti=new Notification.Builder(this)
                .setContentTitle("New Push from"+mtitle)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);//패딩인텐트 클릭하면 실행
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,noti.build());
        //
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

//    private void sendPushNotification(String message) {
//
//        System.out.println("received message : " + message);
//
//        try {
//            JSONObject jsonRootObject = new JSONObject(message);
//            title = jsonRootObject.getString("title");
//            contents = jsonRootObject.getString("contents");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        PendingIntent pendingIntent = null;
//
//        // 푸시알림 클릭시 액티비티 실행
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        pendingIntent = PendingIntent.getActivity(this, 8888 /* Request code */, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//       // String channelId = getString(R.string.firebase_sender);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        // 알림 builder 설정
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this/*, channelId*/);
//
////        Drawable thumb = getResources().getDrawable(R.drawable.app_icon);
////        Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
//
//        // 일반적인 알림
//        // SmallIcon은 흰배경에 투명이든, 투명배경과 흰색만 있어야함.
//        notificationBuilder
//                .setSmallIcon(R.drawable.ic_menu_black_35dp) // 알림 왼쪽상단 아이콘, 아무거나 쓴다고 뜨지않음, 주의요망 필수
//                .setColor(getResources().getColor(R.color.colorPrimary))
//                .setContentTitle(title) // 알림 제목 필수
//                .setContentText(contents) // 알림 내용 필수
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri).setLights(000000255, 500, 2000)
//                .setContentIntent(pendingIntent); // 클릭시 Intent실행
//
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "powerenglish:TAG");
//        wakelock.acquire(5000);
//
//
//        notificationManager.notify(8888 /* ID of notification */, notificationBuilder.build());
  //  }






}
