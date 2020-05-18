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

import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
//        Intent intent=getIntent();
//        String nickName=intent.getStringExtra("nickName");
//        String photoUrl=intent.getStringExtra("photoURL");
        findViewById(R.id.Board_FloatingActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//        mBoardAdapter.setOnIemlClickListner(new BoardAdapter.OnItemClickListener() {
//            @Override
//            public void onitemClick(View v, int pos) {
//
//            }
//        });

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
