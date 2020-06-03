package com.example.toilet_bowl.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.toilet_bowl.Main.Fragment.Board;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.Main.Fragment.Home;
import com.example.toilet_bowl.Main.Fragment.Notifications;
import com.example.toilet_bowl.Main.Fragment.mypage;
import com.example.toilet_bowl.model.FirebaseUserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Board board;
    private Home home;
    private Notifications notifications;
    private mypage mypage;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String UserNickName= intent.getStringExtra("UserNickName");
        if (UserNickName != null) {
            sendUserInfoToServer(UserNickName);
        }
        else {//자동로그인일 경우
            updateUserInfoToServer();
       }

        bottomNavigationView = findViewById(R.id.bottomNavi);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottommenu_home:
                        setFrag(0);
                        break;
                    case R.id.bottommenu_board:
                        setFrag(1);
                        break;
                    case R.id.bottommenu_notifications:
                        setFrag(2);
                        break;
                    case R.id.bottommenu_mypage:
                        setFrag(3);
                        break;
                }

                return true;
            }
        });
        //변화
        board = new Board();
        home = new Home();
        mypage = new mypage();
        notifications = new Notifications();
        setFrag(0);//첫화면

    }



    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, home);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, board);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, notifications);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, mypage);
                ft.commit();
                break;
        }
    }

    private void sendUserInfoToServer(String UserNickName) {//토큰 서버에 보내기
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(uid);//자기 아이디 구독
        String token = FirebaseInstanceId.getInstance().getToken();

        //Log.d("Token", token);
        FirebaseUserModel firebaseUserModel = new FirebaseUserModel(
                token
                , uid
                , 0
                , "Level:" + 0
                , UserNickName
                , new Date());

        mStore.collection("users")
                .document(uid)
                .set(firebaseUserModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log.d("시발","업로드성공");
                    }
                });
    }
    private void updateUserInfoToServer() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final DocumentReference df=mStore.collection("users").document(uid);
        mStore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FirebaseUserModel fm=documentSnapshot.toObject(FirebaseUserModel.class);
                        assert fm != null;
                        int count=fm.getLikecount();
                        if(count>100){
                            df.update("nickname","Level"+10);
                        }else if(count>90){
                            df.update("nickname","Level"+9);
                        }else if(count>80){
                            df.update("nickname","Level"+8);
                        }else if(count>70){
                            df.update("nickname","Level"+7);
                        }else if(count>60){
                            df.update("nickname","Level"+6);
                        }else if(count>50){
                            df.update("nickname","Level"+5);
                        }else if(count>40){
                            df.update("nickname","Level"+4);
                        }else if(count>30){
                            df.update("nickname","Level"+3);
                        }else if (count>20){
                            df.update("nickname","Level"+2);
                        }else if(count>10){
                            df.update("nickname","Level"+1);
                        }else{
                            df.update("nickname","Level"+0);
                        }

                    }
                });
    }

}
