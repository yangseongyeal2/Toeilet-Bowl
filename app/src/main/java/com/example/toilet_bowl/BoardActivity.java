package com.example.toilet_bowl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.model.BoardInfo;
import com.example.toilet_bowl.model.FirebaseUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardActivity extends AppCompatActivity implements OnItemClick {
    private TextView mNickname;
    private ImageView mImageView;
    private List<BoardInfo> mBoardList;
    private List<String> mDocumentIdList;
    private final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private BoardAdapter mBoardAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView mSerchImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        mSerchImageView=findViewById(R.id.board_serch_ImageView);
        mSerchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SerchActivity.class);
                startActivity(intent);
            }
        });
        Intent intent=getIntent();


//        Intent intent=getIntent();
//        String nickName=intent.getStringExtra("nickName");
//        String photoUrl=intent.getStringExtra("photoURL");
        //Log.v("양성열", firebaseUser.getEmail()); 이메일 형식 가져오는 방법
        findViewById(R.id.Board_FloatingActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//write 엑티비티로 이동
                Intent intent =new Intent(getApplicationContext(),WriteActivity.class);
                startActivityForResult(intent,99);
            }
        });
        findViewById(R.id.Board_logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        mRecyclerView=findViewById(R.id.Board_RecyclerVIew);
        mRecyclerView.setHasFixedSize(true);

        retreive_Testing();

        swipeRefreshLayout=findViewById(R.id.Board_SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retreive_Testing();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        //detail
        //새로들어온 유저만 토큰을 서버로 보냄.
       // Intent intent=getIntent();
        String autoflag=intent.getStringExtra("자동로그인");
        if(autoflag==null){
            sendUserInfoToServer();
        }
//        else{
//
//            updateTokenToServer();
//        }

        ///////////////////////////

    }

    private void updateTokenToServer() {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStore.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseUserModel fu=documentSnapshot.toObject(FirebaseUserModel.class);
                if(fu.getLikecount()>20){
                    mStore.collection("users").document(uid).update("nickname","지리는상담사");
                    sendNotification(uid,"레벨다운","지리는상담사");
                }else if(fu.getLikecount()>10){
                    mStore.collection("users").document(uid).update("nickname","약쟁이");
                    sendNotification(uid,"레벨업","약쟁이로올라감");
                }
            }
        });
    }

    private void sendNotification(String uid,String title,String content) {
        RequestQueue mRequesQue= Volley.newRequestQueue(this);
        String URL="https://fcm.googleapis.com/fcm/send";
        JSONObject mainObj=new JSONObject();
        try {
            mainObj.put("to","/topics/"+uid);
            JSONObject notificationObj=new JSONObject();
            notificationObj.put("title",title);
            notificationObj.put("body",content);
            mainObj.put("notification",notificationObj);


            JsonObjectRequest request=new JsonObjectRequest(com.android.volley.Request.Method.POST, URL,
                    mainObj,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String ,String> header=new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAAgGWB2_M:APA91bHQEzEfW7ZbMrfZNy_fBF90PEdsoEvOs32Ik-ae9N_3hE-p9HO5GVKy_7yVqw5MMxuCQvNBI4h_r_FkssbkrsjkMkRAFiKsbhq3GoyQHVWfmIjWk9Xf4Bk_89hc4dXFadIdMJeA");
                    return  header;
                }
            };

            mRequesQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendUserInfoToServer() {//토큰 서버에 보내기
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(uid);//자기 아이디 구독
        String token=FirebaseInstanceId.getInstance().getToken();
        Log.d("Token",token);
        FirebaseUserModel firebaseUserModel=new FirebaseUserModel(
                token
                ,uid
                ,0
                ,"눈팅하는새끼");
        assert token != null;

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

    public void retreive_Testing(){

        mBoardList=new ArrayList<>();
        mStore.collection("Board")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()&&task.getResult()!=null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //        for (QueryDocumentSnapshot document : task.getResult()) {

                        BoardInfo boardInfo = document.toObject(BoardInfo.class);
                        if(boardInfo.getDeleted_at().equals("0")){
                       // Log.d("yangseongyeal",boardInfo.getContent());
                        mBoardList.add(boardInfo);
                        }else{
                            Log.d("양성열","삭제됬었음");
                        }
                    }
                    mBoardAdapter = new BoardAdapter(mBoardList,BoardActivity.this,firebaseUser,BoardActivity.this);
                    mBoardAdapter.setOnIemlClickListner(new BoardAdapter.OnItemClickListener() {//Detail 액티비티로 이동
                        @Override
                        public void onitemClick(View v, int pos) {
                            Intent intent=new Intent(getApplicationContext(),DetailActivity.class);
                            intent.putExtra("DocumentId",mBoardList.get(pos).getDocumentId());
                            startActivity(intent);
                        }
                    });
                    mRecyclerView.setAdapter(mBoardAdapter);
                } else {
                    Log.d("양성열", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 99&&requestCode==99) {//write 액티비티실행후 나온 결과 받아오기.
           Log.d("양성열","리절트 함수 실행");
            retreive_Testing();//업로드 화면 끝났을때
        }else if(resultCode == RESULT_CANCELED) {
            Log.d("양성열","실패");
        }

    }

    @Override
    public void onClick(String value) {//어댑터에서 클릭후 오는 정보는 여기로옴
        // value this data you receive when increment() / decrement()
        if(value.equals("실시간 데이터 삭제")){
            retreive_Testing();
        }
    }

}
