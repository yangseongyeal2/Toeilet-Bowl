package com.example.toilet_bowl.Main.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.toilet_bowl.Adapter.HomeAdapter;
import com.example.toilet_bowl.Adapter.HomeAdapter2;
import com.example.toilet_bowl.Board.DetailActivity;
import com.example.toilet_bowl.R;
import com.example.toilet_bowl.model.BoardInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private View view;
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private ProgressDialog loadingbar;
    private List<BoardInfo> mBoardList;
    private List<BoardInfo> mBoardList2;
    private HomeAdapter mHomeAdapter;
    private HomeAdapter2 mHomeAdapter2;
    private RecyclerView mHomeRecyclerView;
    private RecyclerView mHomeWaitRecyclerView;
    private BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout swipeRefreshLayout;


    private final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.home,container,false);
        loadingbar=new ProgressDialog(getContext());
        mHomeRecyclerView=view.findViewById(R.id.home_mypost_RecyclerView);
        mHomeWaitRecyclerView=view.findViewById(R.id.home_waittingRequest_RecyclerView);
        bottomNavigationView=getActivity().findViewById(R.id.bottomNavi);
        retreive_mypost();//내가 올린 상담글과 답변이 0개 달린 게시글 가져오기
        retreive_waittingreply();

        view.findViewById(R.id.home_write_Button).setOnClickListener(new View.OnClickListener() {//Board fragment로 데이터 보내기
            @Override
            public void onClick(View v) {//fragment에서 fragment로 데이터 보내는방법
               Bundle bundle=new Bundle();
               bundle.putString("write","디테일");
                FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                Board board=new Board();
                board.setArguments(bundle);
                bottomNavigationView.setSelectedItemId(R.id.bottommenu_board);//하단바 조정정
               transaction.replace(R.id.main_frame,board);
                transaction.commit();
            }
        });
        swipeRefreshLayout=view.findViewById(R.id.item_home_refresh1);//새로고침기능
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retreive_mypost();
                retreive_waittingreply();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        return view;
    }

    private void retreive_mypost(){
        loadingbar.setTitle("Set profile image");
        loadingbar.setMessage("pleas wait업로딩중");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        mBoardList=new ArrayList<>();
        mBoardList2=new ArrayList<>();
        mStore.collection("Board")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()&&task.getResult()!=null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //        for (QueryDocumentSnapshot document : task.getResult()) {

                        BoardInfo boardInfo = document.toObject(BoardInfo.class);
                        assert firebaseUser != null;
                        if(boardInfo.getDeleted_at().equals("0")&&firebaseUser.getUid().equals(boardInfo.getUid())){
                            // Log.d("yangseongyeal",boardInfo.getContent());
                            if(mBoardList.size()<4){//5개 까지만보이게
                                mBoardList.add(boardInfo);
                            }
                        }

                    }
                    mHomeAdapter = new HomeAdapter(mBoardList);//내가 올린 게시글
                    mHomeAdapter.setOnIemlClickListner(new HomeAdapter.OnItemClickListener() {
                        @Override
                        public void onitemClick(View v, int pos) {
                            Log.d("클릭","내가쓴글:"+String.valueOf(pos));
                            Intent intent=new Intent(getActivity(),DetailActivity.class);
                            intent.putExtra("DocumentId",mBoardList.get(pos).getDocumentId());
                            startActivity(intent);
                        }
                    });
                    mHomeRecyclerView.setAdapter(mHomeAdapter);

                    loadingbar.dismiss();
                } else {
                    Log.d("양성열", "Error getting documents: ", task.getException());
                    loadingbar.dismiss();
                }
            }
        });
    }
    private void retreive_waittingreply(){

        mBoardList2=new ArrayList<>();
        mStore.collection("Board")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()&&task.getResult()!=null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //        for (QueryDocumentSnapshot document : task.getResult()) {

                        BoardInfo boardInfo = document.toObject(BoardInfo.class);
                        assert firebaseUser != null;
                        if(boardInfo.getDeleted_at().equals("0")&&boardInfo.getReplycount()==0){
                            if(mBoardList2.size()<4){//5개 까지만보이게
                                mBoardList2.add(boardInfo);
                            }
                        }
                    }
                    mHomeAdapter2 = new HomeAdapter2(mBoardList2);//내가 올린 게시글
                    mHomeAdapter2.setOnIemlClickListner(new HomeAdapter.OnItemClickListener() {
                        @Override
                        public void onitemClick(View v, int pos) {
                            Log.d("클릭","내가쓴글:"+String.valueOf(pos));
                            Intent intent=new Intent(getActivity(),DetailActivity.class);
                            intent.putExtra("DocumentId",mBoardList2.get(pos).getDocumentId());
                            startActivity(intent);
                        }
                    });
                    mHomeWaitRecyclerView.setAdapter(mHomeAdapter2);

                } else {
                    Log.d("양성열", "Error getting documents: ", task.getException());

                }
            }
        });

    }
}
