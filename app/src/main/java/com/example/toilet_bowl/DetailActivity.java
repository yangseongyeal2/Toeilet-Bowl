package com.example.toilet_bowl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Adapter.ReplyAdapter;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.model.BoardInfo;
import com.example.toilet_bowl.model.NotificationModel;
import com.example.toilet_bowl.model.ReplyInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity implements OnItemClick {
    private static final String TAG="DetailActivity";
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private TextView mTitle;
    private TextView mContent;
    private TextInputEditText mEditText;
    private DocumentReference documentReference;
    private String uid;
    private ReplyAdapter mReplyAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    //device to device notification
    //private RequestQueue mRequesQue;
    private RequestQueue mRequesQue;
    private String URL="https://fcm.googleapis.com/fcm/send";
    private ProgressDialog loadingbar;
    private String mDocumentId_send;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTitle=findViewById(R.id.detail_title);
        mContent=findViewById(R.id.detail_content);
        mEditText=findViewById(R.id.detail_reply_EditText);
        mRecyclerView=findViewById(R.id.detail_recyclerview);
        mRequesQue= Volley.newRequestQueue(this);
        TextInputLayout mTextInputLayout = findViewById(R.id.detail_TextIputLayout);
        final String mDocumentId = getIntent().getStringExtra("DocumentId");//mDocumentId는 디테일 정보받아오기
        mDocumentId_send=mDocumentId;
        swipeRefreshLayout=findViewById(R.id.Board_SwipeRefreshLayout);
        loadingbar=new ProgressDialog(this);
        if(mDocumentId!=null){
            documentReference=mStore.collection("Board").document(mDocumentId);
        }else {
            Log.d(TAG,"보내기실패");
        }
        initUid();//uid 전역변수로 사용가능
        upviewcount();//조회수 1올리기
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//새로고침
                retreiveReply(documentReference);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        retreiveDocumentReference(documentReference);
        retreiveReply(documentReference);
        //updateReply(documentReference);
        mRecyclerView.setAdapter(mReplyAdapter);
        mTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {//에딧텍스트 업로드
            @Override
            public void onClick(View v) {
                loadingbar.setTitle("Set profile image");
                loadingbar.setMessage("pleas wait업로딩중");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                final DocumentReference replyDocumentreference = documentReference.collection("reply").document();
                if(mEditText.getText()!=null){
                    String reply_string = mEditText.getText().toString();
                    assert firebaseUser != null;
                    final ReplyInfo replyInfo=new ReplyInfo(firebaseUser.getUid(),"0",reply_string,new Date(),replyDocumentreference.getId(), Arrays.asList(""));
                    //mReplyInfoList.add(replyInfo);
                    replyDocumentreference.set(replyInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(!uid.equals(firebaseUser.getUid())){//다른사람이 내 게시판에 글 올릴떄만 알림
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        BoardInfo boardInfo=documentSnapshot.toObject(BoardInfo.class);
                                        String title=boardInfo.getTitle();
                                        String cotent=replyInfo.getContent();
                                        sendNotification(mDocumentId,title,cotent);
                                    }
                                });
                            }else if(uid.equals(firebaseUser.getUid())){//올린사람이 댓글을 달았을때 댓글달린사람 uidlist로 매세지보내기.
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                        final BoardInfo boardInfo=documentSnapshot.toObject(BoardInfo.class);
                                        String title=boardInfo.getTitle();
                                        String cotent=replyInfo.getContent();
                                        documentReference.collection("reply").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.getResult()!=null){
                                                    for(QueryDocumentSnapshot data:task.getResult()){
                                                        ReplyInfo ri=data.toObject(ReplyInfo.class);
                                                        if(!ri.getUid().equals(uid)){
                                                            String title=boardInfo.getTitle();
                                                            String cotent=replyInfo.getContent();
                                                            sendNotification(ri.getUid(),title,cotent);
                                                        }
                                                    }
                                                }

                                            }
                                        });

                                    }
                                });
                            }
                            mEditText.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                            retreiveReply(documentReference);
                            loadingbar.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingbar.dismiss();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"댓글을 입력하시오",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void upviewcount() {
        documentReference.update("viewcount", FieldValue.increment(1));
    }

    private void initUid() {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {//uid 얻어오기
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        uid=document.get("uid").toString();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
    }

    private void retreiveDocumentReference(DocumentReference documentReference) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                BoardInfo boardInfo = documentSnapshot.toObject(BoardInfo.class);
                assert boardInfo != null;
                mTitle.setText(boardInfo.getTitle());
                mContent.setText(boardInfo.getContent());
            }
        });

    }
    private void retreiveReply(final DocumentReference documentReference){
        CollectionReference collectionReference=documentReference.collection("reply");
        collectionReference
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ReplyInfo> list=new ArrayList<>();
                if(task.getResult()!=null) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        ReplyInfo replyInfo=documentSnapshot.toObject(ReplyInfo.class);
                        Log.d(TAG, "댓글불러오기 성공");
                        assert replyInfo != null;
                        if(replyInfo.getDeleted_at().equals("0")){
                            list.add(replyInfo);
                        }
                    }
                    mReplyAdapter=new ReplyAdapter(list,documentReference,DetailActivity.this,DetailActivity.this,mEditText);
                    mRecyclerView.setAdapter(mReplyAdapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"댓글불러오기 실패");
            }
        });
    }
    private void updateReply(final DocumentReference documentReference){
        documentReference.collection("reply").orderBy("date", Query.Direction.ASCENDING).whereEqualTo("deleted_at","0")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<ReplyInfo> list=new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {
                            ReplyInfo replyInfo=doc.toObject(ReplyInfo.class);
                            Log.d(TAG, replyInfo.getContent());

                                list.add(replyInfo);

                        }
                        mReplyAdapter=new ReplyAdapter(list,documentReference,DetailActivity.this,DetailActivity.this,mEditText);
                        mRecyclerView.setAdapter(mReplyAdapter);

                    }
                });




    }


    @Override
    public void onClick(String value) {//어댑터에서 클릭후 오는 정보는 여기로옴
        // value this data you receive when increment() / decrement()
        if(value.equals("실시간 댓글 삭제")){
            retreiveReply(documentReference);
        }
    }

    void subscribe(String mDocumentId){
        FirebaseMessaging.getInstance().subscribeToTopic(mDocumentId);
    }
    private void sendNotification(String documentId,String title,String content){
        /* our json object will lokk loke
        {
            "to": "topics/topic name",
            notification:{
                title: "some title",
                 body: "some body"
            }
        }
        */
        JSONObject mainObj=new JSONObject();
        try {
            mainObj.put("to","/topics/"+documentId);
            JSONObject notificationObj=new JSONObject();
            notificationObj.put("title",title+"에 댓글이 달렸습니다");
            notificationObj.put("body","댓글:"+content);
            notificationObj.put("documentId",mDocumentId_send);
            Log.d("알림기능",mDocumentId_send);
           // mainObj.put("notification",notificationObj);
            mainObj.put("data",notificationObj);


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




}

