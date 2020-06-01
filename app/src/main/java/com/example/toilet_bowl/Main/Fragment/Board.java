package com.example.toilet_bowl.Main.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.toilet_bowl.Adapter.BoardAdapter;
import com.example.toilet_bowl.Board.DetailActivity;
import com.example.toilet_bowl.Board.SerchActivity;
import com.example.toilet_bowl.Board.WriteActivity;
import com.example.toilet_bowl.Interface.OnItemClick;
import com.example.toilet_bowl.Login.LoginActivity;
import com.example.toilet_bowl.R;
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



public class Board extends Fragment implements OnItemClick {
    private View view;
    private List<BoardInfo> mBoardList;
    private final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private BoardAdapter mBoardAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView mSerchImageView;
    private ProgressDialog loadingbar;
    private String result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.board,container,false);
        mRecyclerView=view.findViewById(R.id.Board_RecyclerVIew);//리사이클러뷰 선언
        mRecyclerView.setHasFixedSize(true);
        mSerchImageView=view.findViewById(R.id.board_serch_ImageView);//검색기능
        mSerchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), SerchActivity.class);
                startActivity(intent);
            }
        });
        loadingbar=new ProgressDialog(getContext());//로딩바
        view.findViewById(R.id.Board_FloatingActionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//write 엑티비티로 이동
                Intent intent =new Intent(getActivity(), WriteActivity.class);
                startActivityForResult(intent,99);
            }
        });
        retreive_Testing();//보드 불러오기
        swipeRefreshLayout=view.findViewById(R.id.Board_SwipeRefreshLayout);//새로고침기능
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retreive_Testing();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        view.findViewById(R.id.Board_logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //로그아웃기능
                signOut();
            }
        });

        if(getArguments()!=null){//Home fragment에서 데이터값 받아오기
            result=getArguments().getString("write");
            if(result.equals("디테일")){
                Intent intent =new Intent(getActivity(), WriteActivity.class);
                startActivityForResult(intent,99);
            }
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 99&&requestCode==99) {//write 액티비티실행후 나온 결과 받아오기.
            Log.d("양성열","리절트 함수 실행");
            retreive_Testing();//업로드 화면 끝났을때
        }
//        else if(resultCode == RESULT_CANCELED) {
//            Log.d("양성열","실패");
//        }
    }
    public void retreive_Testing(){
        loadingbar.setTitle("Set profile image");
        loadingbar.setMessage("pleas wait업로딩중");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
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
                    mBoardAdapter = new BoardAdapter(mBoardList,getContext(),firebaseUser,Board.this);
                    mBoardAdapter.setOnIemlClickListner(new BoardAdapter.OnItemClickListener() {//Detail 액티비티로 이동
                        @Override
                        public void onitemClick(View v, int pos) {
                            Intent intent=new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("DocumentId",mBoardList.get(pos).getDocumentId());
                            startActivity(intent);
                        }
                    });
                    mRecyclerView.setAdapter(mBoardAdapter);
                    loadingbar.dismiss();
                } else {
                    Log.d("양성열", "Error getting documents: ", task.getException());
                    loadingbar.dismiss();
                }
            }
        });
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
    @Override
    public void onClick(String value) {
        if(value.equals("실시간 데이터 삭제")){
            retreive_Testing();
        }
    }
}
