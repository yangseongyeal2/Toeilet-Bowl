package com.example.toilet_bowl.FirebaseCloudMessage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

import com.example.toilet_bowl.BoardActivity;
import com.example.toilet_bowl.DetailActivity;
import com.example.toilet_bowl.MainActivity;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.SerchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.annotations.Since;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static final String TAG="파이어베이스 메세지 샘플";
    public String title;
    public String body;
    public String documentid;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
     //    Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {//data형식
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            title=remoteMessage.getData().get("title");
            body=remoteMessage.getData().get("body");
            documentid=remoteMessage.getData().get("documentId");
        }
        // 노티피케이션을 사용했을떄 데이터 가져오기
        if (remoteMessage.getNotification() != null) {//notification 형식
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            title=remoteMessage.getNotification().getTitle();
            body=remoteMessage.getNotification().getBody();

        }
        //


        sendNotification();
    }

    @Override
    public void onNewToken(@NonNull String s) {
        //Log.d(TAG, "Refreshed token: " + s);
        //서버에 토큰을 보내줘야함 만약 토큰이 새로 생겼다면(다시 다운로드 할때)
    }


    private void sendNotification() {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("DocumentId",documentid);//이거없으면 디테일 안열림.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack( DetailActivity.class );
        stackBuilder.addNextIntent(intent);
        Log.d("DocumentId",documentid);

//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT); 이게 클래식 하지만 뒤로 누르면 바로꺼짐
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);//이게 뒤로 눌렀을때 안꺼지는 방법
        String channelId = "채널 ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         //Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
