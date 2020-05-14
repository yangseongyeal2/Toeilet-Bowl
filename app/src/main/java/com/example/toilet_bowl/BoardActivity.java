package com.example.toilet_bowl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {
    private TextView mNickname;
    private ImageView mImageView;
    private List<BoardInfo> mBoardList;
    private List<String> mDocumentIdList;
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private BoardAdapter mBoardAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Intent intent=getIntent();
        String nickName=intent.getStringExtra("nickName");
        String photoUrl=intent.getStringExtra("photoURL");
        findViewById(R.id.Board_FloatingActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),WriteActivity.class);
                startActivity(intent);
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









    }
    public void retreive_Testing(){
        mBoardList=new ArrayList<>();

        mStore.collection("Board")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //        for (QueryDocumentSnapshot document : task.getResult()) {

                        BoardInfo boardInfo = document.toObject(BoardInfo.class);
                        Log.d("yangseongyeal",boardInfo.getContent());

                        mBoardList.add(boardInfo);
                    }
                    mBoardAdapter = new BoardAdapter(mBoardList);
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
}
